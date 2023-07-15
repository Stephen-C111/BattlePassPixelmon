package com.scproductions.battlepasspixelmon;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import com.scproductions.helloworld.PlayerDataHandler.PlayerData;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

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
		BattlePassManager.putData(new BattlePass(uuid, rank, rankProgress, claimedRanks), world);
	}
	
	public static BattlePassManager getDataHandler(ServerWorld world) {
		return world.getDataStorage().get(BattlePassManager::new, BattlePassManager.NAME);
	}
	
	public static void grantProgress(int amount, boolean allowDemotion) {
		
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
