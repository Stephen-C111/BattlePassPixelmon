package com.scproductions.battlepasspixelmon;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class BattlePassManager extends WorldSavedData {
	
	public static final String NAME = Main.MOD_ID + "_BattlePassManager";
	
	private final Map<UUID, BattlePass> DATA = new HashMap<UUID, BattlePass>();
	
	public static final int MAXPROGRESS = 1000;
	public static final int MAXRANK = 999999;
	
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
		BattlePassManager.putData(new BattlePass(uuid, rank, rankProgress, claimedRanks), world);
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
			if (bp.rankProgress >= MAXPROGRESS) {
				bp.rankProgress -= MAXPROGRESS;
				bp.rank++;
			}
			else if (allowDemotion && bp.rankProgress < 0) {
				bp.rank--;
				bp.rankProgress += MAXPROGRESS;
			}
			putData(bp, ServerLifecycleHooks.getCurrentServer().overworld());
		}
	}
	
	public static int getRank(UUID uuid) {
		BattlePassManager bpm = ServerLifecycleHooks.getCurrentServer().overworld().getDataStorage().computeIfAbsent(BattlePassManager::new, BattlePassManager.NAME);
		return bpm.DATA.get(uuid).rank;
	}
	
	public static int getRankProgress(UUID uuid) {
		BattlePassManager bpm = ServerLifecycleHooks.getCurrentServer().overworld().getDataStorage().computeIfAbsent(BattlePassManager::new, BattlePassManager.NAME);
		return bpm.DATA.get(uuid).rankProgress;
	}
	
	public static int getClaimedRanks(UUID uuid) {
		BattlePassManager bpm = ServerLifecycleHooks.getCurrentServer().overworld().getDataStorage().computeIfAbsent(BattlePassManager::new, BattlePassManager.NAME);
		return bpm.DATA.get(uuid).claimedRanks;
	}
	
	public static void sendPlayerInfo(ServerPlayerEntity player, int pointsGained) {
		player.displayClientMessage(new StringTextComponent(
				"+" + pointsGained + " | Rank " + BattlePassManager.getRank(player.getUUID()) + 
				": " + BattlePassManager.getRankProgress(player.getUUID()) + "/1000"), true);
	}
	
	public static void sendPlayerInfo(ServerPlayerEntity player) {
		player.displayClientMessage(new StringTextComponent(
				"Rank " + BattlePassManager.getRank(player.getUUID()) + 
				": " + BattlePassManager.getRankProgress(player.getUUID()) + "/1000"), true);
	}
	
	static class BattlePass {
		UUID uuid;
		int rank;
		int rankProgress;
		int claimedRanks;
		
		public BattlePass(UUID _uuid, int _rank, int _rankProgress, int _claimedRanks) {
			uuid = _uuid;
			rank = _rank;
			rankProgress = _rankProgress;
			claimedRanks = _claimedRanks;
		}
		
		//Save data to an NBT
		public CompoundNBT serialize() {
			CompoundNBT nbt = new CompoundNBT();
			nbt.putUUID("uuid", uuid);
			nbt.putInt("rank", rank);
			nbt.putInt("rankProgress", rankProgress);
			nbt.putInt("claimedRanks", claimedRanks);
			return nbt;
		}
		//Create a PlayerData from NBT
		public static BattlePass deserialize(CompoundNBT nbt) {
			return new BattlePass(nbt.getUUID("uuid"), nbt.getInt("rank"), nbt.getInt("rankProgress"), nbt.getInt("claimedRanks"));
		}
	}
}
