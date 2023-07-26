package com.scproductions.battlepasspixelmon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import com.pixelmonmod.pixelmon.api.registries.PixelmonItems;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.items.ItemHandlerHelper;

public class BattlePassManager extends WorldSavedData {
	
	public static final String NAME = Main.MOD_ID + "_BattlePassManager";
	
	private final Map<UUID, BattlePass> DATA = new HashMap<UUID, BattlePass>();
	
	//Constructor used by calling world.getDataStorage().computeIfAbsent(BattlePassManager::new, BattlePassManager.NAME);
	public BattlePassManager(String p_i2141_1_) {
		super(p_i2141_1_);
	}
	
	public BattlePassManager() {
		this(NAME);
	}

	@Override
	public void load(CompoundNBT nbt) {
		CompoundNBT saveData = nbt.getCompound("savedata");
		//Load each data entry into the HashMap for runtime usage.
		for (int i = 0; saveData.contains("data"+i); i++) {
			BattlePass bp = BattlePass.deserialize(saveData.getCompound("data"+i));
			DATA.put(bp.uuid, bp);
		}
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		CompoundNBT saveData = new CompoundNBT();
		int i = 0;
		Logger.getLogger("BattlePassPixelmon").info("Beginning save process...");
		//Save each HashMap entry as "data1", "data2"... "datai". For data storage.
		for(Iterator<Map.Entry<UUID, BattlePass>> iterator = DATA.entrySet().iterator(); iterator.hasNext();) {
			Logger.getLogger("BattlePassPixelmon").info("Saving data" + i + "...");
			saveData.put("data" + i++, iterator.next().getValue().serialize());
		}
		Logger.getLogger("BattlePassPixelmon").info("Packing data into nbt...");
		nbt.put("savedata", saveData);
		//Return nbt to the WorldSavedData class
		Logger.getLogger("BattlePassPixelmon").info("NBT save finalizing...");
		return nbt;
	}
	
	public static void putData(BattlePass object, ServerWorld world) {
		BattlePassManager bpm = world.getDataStorage().computeIfAbsent(BattlePassManager::new, BattlePassManager.NAME);
		//Check for existing playerdata.
		if (bpm.DATA.containsKey(object.uuid)) {
			Logger.getLogger("BattlePassPixelmon").info("UUID entry found, updating entry...");
			bpm.DATA.replace(object.uuid, object);
		}
		else {
			Logger.getLogger("BattlePassPixelmon").info("UUID not found, creating entry...");
			bpm.DATA.put(object.uuid, object);
		}
		bpm.setDirty();
	}
	
	public static void putData(UUID uuid, int rank, int rankProgress, int claimedRanks) {
		ServerWorld world = ServerLifecycleHooks.getCurrentServer().overworld();
		boolean dontSpam = BattlePassManager.getSpamDisallowed(uuid);
		UUID[] claimedPacks = BattlePassManager.getClaimedUUIDS(uuid);
		BattlePassManager.putData(new BattlePass(uuid, rank, rankProgress, claimedRanks, dontSpam, claimedPacks), world);
	}
	
	public static BattlePassManager getDataHandler(ServerWorld world) {
		return world.getDataStorage().get(BattlePassManager::new, BattlePassManager.NAME);
	}
	
	//This method should always be called with allowDemotion set to false, unless you are sure an action should be allowed to REMOVE progress.
	public static void grantProgress(UUID uuid, int amount, boolean allowDemotion) {
		ServerWorld world = ServerLifecycleHooks.getCurrentServer().overworld();
		BattlePassManager bpm = world.getDataStorage().computeIfAbsent(BattlePassManager::new, BattlePassManager.NAME);
		//find pair in DATA, or create a new entry if one doesn't exist, then update it.
		if (bpm.DATA.containsKey(uuid) == false) {
			Logger.getLogger("HelloWorld").info("Found no entry, creating new PlayerData before updating...");
			putData(uuid, 0, 0, 0);
		}
		if (bpm.DATA.containsKey(uuid)) {
			Logger.getLogger("BattlePassPixelmon").info("Found an entry, updating...");
			BattlePass bp = bpm.DATA.get(uuid);
			int progress = 0;
			if (allowDemotion) {
				progress = amount;
			}
			else {
				progress = amount;
				if (progress < 0) {
					progress = 0;
				}
			}
			bp.rankProgress += progress;
			while (bp.rankProgress >= BattlePassConfig.required_progress_to_rank.get() && bp.rank < BattlePassConfig.max_rank.get()) {
				bp.rankProgress -= BattlePassConfig.required_progress_to_rank.get();
				bp.rank++;
			}
			while (allowDemotion && bp.rankProgress < 0) {
				bp.rank--;
				bp.rankProgress += BattlePassConfig.required_progress_to_rank.get();
			}
			putData(bp, ServerLifecycleHooks.getCurrentServer().overworld());
		}
	}
	//The get commands below will issue -999 if the uuid specified has no entry, to avoid crashing the server.
	public static int getRank(UUID uuid) {
		BattlePassManager bpm = ServerLifecycleHooks.getCurrentServer().overworld().getDataStorage().computeIfAbsent(BattlePassManager::new, BattlePassManager.NAME);
		if (bpm.DATA.containsKey(uuid) == false) {
			return -999;
		}
		return bpm.DATA.get(uuid).rank;
	}
	
	public static int getRankProgress(UUID uuid) {
		BattlePassManager bpm = ServerLifecycleHooks.getCurrentServer().overworld().getDataStorage().computeIfAbsent(BattlePassManager::new, BattlePassManager.NAME);
		if (bpm.DATA.containsKey(uuid) == false) {
			return -999;
		}
		return bpm.DATA.get(uuid).rankProgress;
	}
	
	public static int getClaimedRanks(UUID uuid) {
		BattlePassManager bpm = ServerLifecycleHooks.getCurrentServer().overworld().getDataStorage().computeIfAbsent(BattlePassManager::new, BattlePassManager.NAME);
		if (bpm.DATA.containsKey(uuid) == false) {
			return -999;
		}
		return bpm.DATA.get(uuid).claimedRanks;
	}
	
	public static boolean getSpamDisallowed(UUID uuid) {
		BattlePassManager bpm = ServerLifecycleHooks.getCurrentServer().overworld().getDataStorage().computeIfAbsent(BattlePassManager::new, BattlePassManager.NAME);
		if (bpm.DATA.containsKey(uuid) == false) {
			return false;
		}
		return bpm.DATA.get(uuid).dontSpam;
	}
	
	public static UUID[] getClaimedUUIDS(UUID uuid) {
		BattlePassManager bpm = ServerLifecycleHooks.getCurrentServer().overworld().getDataStorage().computeIfAbsent(BattlePassManager::new, BattlePassManager.NAME);
		if (bpm.DATA.containsKey(uuid) == false) {
			return new UUID[] {};
		}
		return bpm.DATA.get(uuid).claimedPacks;
	}
	
	//This will appear on the player's chatbox.
	public static void sendPlayerInfoToChat(ServerPlayerEntity player, int pointsGained, String reason) {
		if (getSpamDisallowed(player.getUUID())) {
			//send to sleep bar instead
			sendPlayerInfo(player, pointsGained, reason);
		}
		else {
			//send to chat
			String plus = "";
			if (pointsGained != 0) {
				plus += " +" + pointsGained;
			}
			player.sendMessage(new StringTextComponent(
					reason + plus + " | Rank " + BattlePassManager.getRank(player.getUUID()) + 
					": " + BattlePassManager.getRankProgress(player.getUUID()) + "/" + BattlePassConfig.required_progress_to_rank.get()), player.getUUID());
		}
		
	}
	
	//This will appear above the player's toolbar for a brief period of time.
	public static void sendPlayerInfo(ServerPlayerEntity player, int pointsGained, String reason) {
		String plus = "";
		if (pointsGained != 0) {
			plus += " +" + pointsGained;
		}
		player.displayClientMessage(new StringTextComponent(
				reason + plus + " | Rank " + BattlePassManager.getRank(player.getUUID()) + 
				": " + BattlePassManager.getRankProgress(player.getUUID()) + "/" + BattlePassConfig.required_progress_to_rank.get()), true);
	}
	
	public static void sendPlayerInfo(ServerPlayerEntity player) {
		player.displayClientMessage(new StringTextComponent(
				"Rank " + BattlePassManager.getRank(player.getUUID()) + 
				": " + BattlePassManager.getRankProgress(player.getUUID()) + "/" + BattlePassConfig.required_progress_to_rank.get()), true);
	}
	
	public static void claimRewards(ServerPlayerEntity player) {
		int MAX_CLAIMS_ALLOWED = 512; //prevent flooding the world with thousands of items.
		UUID uuid = player.getUUID();
		int claimed = getClaimedRanks(uuid);
		int rank = getRank(uuid);
		int amount = rank - claimed;
		
		
		
		if (amount > MAX_CLAIMS_ALLOWED) {
			amount = MAX_CLAIMS_ALLOWED;
		}
		
		for (int i = 0; i < amount; i++) {
			claimed++;
			ItemStack stack = new ItemStack(PixelmonItems.xl_exp_candy);
			ItemHandlerHelper.giveItemToPlayer(player, stack);
			}
		player.sendMessage(new StringTextComponent(
				"Claimed " + amount + " ranks."), player.getUUID());
		putData(uuid, rank, getRankProgress(uuid), claimed);
	}
	
	public static void toggleChatMessages(ServerPlayerEntity player) {
		ServerWorld world = ServerLifecycleHooks.getCurrentServer().overworld();
		BattlePassManager bpm = world.getDataStorage().computeIfAbsent(BattlePassManager::new, BattlePassManager.NAME);
		BattlePass bp = bpm.DATA.get(player.getUUID());
		bp.dontSpam = !bp.dontSpam;
		putData(bp, world);
		player.sendMessage(new StringTextComponent("Don't receive progress messages in chat: " + bp.dontSpam), player.getUUID());
	}
	
	public static void updateClaimedPacks(ServerPlayerEntity player, UUID[] claimedPacks) {
		ServerWorld world = ServerLifecycleHooks.getCurrentServer().overworld();
		BattlePassManager bpm = world.getDataStorage().computeIfAbsent(BattlePassManager::new, BattlePassManager.NAME);
		BattlePass bp = bpm.DATA.get(player.getUUID());
		bp.claimedPacks = claimedPacks;
		putData(bp, world);
	}
	
	public static void updateClaimedPacks(ServerPlayerEntity player, List<UUID> claimedPacks) {
		ServerWorld world = ServerLifecycleHooks.getCurrentServer().overworld();
		BattlePassManager bpm = world.getDataStorage().computeIfAbsent(BattlePassManager::new, BattlePassManager.NAME);
		BattlePass bp = bpm.DATA.get(player.getUUID());
		Logger.getLogger("BattlePassPixelmon").info("Filling new array of size: " + claimedPacks.size());
		UUID[] array = new UUID[claimedPacks.size()];
		int y = 0;
		for (UUID uuid : claimedPacks) {
			array[y++] = uuid;
			Logger.getLogger("BattlePassPixelmon").info(uuid.toString());
		}
		bp.claimedPacks = array;
		putData(bp, world);
	}
	
	static class BattlePass {
		UUID uuid;
		int rank;
		int rankProgress;
		int claimedRanks;
		boolean dontSpam;
		UUID[] claimedPacks;
		
		public BattlePass(UUID _uuid, int _rank, int _rankProgress, int _claimedRanks, boolean _dontSpam, UUID[] _claimedPacks) {
			uuid = _uuid;
			rank = _rank;
			rankProgress = _rankProgress;
			claimedRanks = _claimedRanks;
			dontSpam = _dontSpam;
			claimedPacks = _claimedPacks;
		}
		
		//Save data to an NBT
		public CompoundNBT serialize() {
			CompoundNBT nbt = new CompoundNBT();
			nbt.putUUID("uuid", uuid);
			nbt.putInt("rank", rank);
			nbt.putInt("rankProgress", rankProgress);
			nbt.putInt("claimedRanks", claimedRanks);
			nbt.putBoolean("dontSpam", dontSpam);
			int i = 0;
			for (UUID packuuid : claimedPacks) {
				nbt.putUUID("packuuid" + i++, packuuid);
			}
			
			return nbt;
		}
		//Create a BattlePass from NBT
		public static BattlePass deserialize(CompoundNBT nbt) {
			List<UUID> packuuids = new ArrayList<UUID>();
			int i = 0;
			while (nbt.contains("packuuid" + i)) {
				packuuids.add(nbt.getUUID("packuuid" + i++));
			}
			UUID[] array = new UUID[packuuids.size()];
			int y = 0;
			for (UUID uuid : packuuids) {
				array[y++] = uuid;
			}
			return new BattlePass(nbt.getUUID("uuid"), nbt.getInt("rank"), nbt.getInt("rankProgress"), nbt.getInt("claimedRanks"), nbt.getBoolean("dontSpam"), array);
		}
	}
}
