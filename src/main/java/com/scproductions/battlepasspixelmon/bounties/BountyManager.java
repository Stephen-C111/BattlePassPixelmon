package com.scproductions.battlepasspixelmon.bounties;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pixelmonmod.pixelmon.api.pokemon.Element;
import com.scproductions.battlepasspixelmon.BattlePassManager;
import com.scproductions.battlepasspixelmon.Main;
import com.scproductions.battlepasspixelmon.bounties.gui.BountyGUIHandler;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class BountyManager extends WorldSavedData{
	
	public static final String NAME = Main.MOD_ID + "_BountyManager";
	
	public static boolean refreshing = false;
	
	public BountyManager(String p_i2141_1_) {
		super(p_i2141_1_);
	}
	
	public BountyManager() {
		this(NAME);
	}
	
	public static BountyManager getDataHandler() {
		ServerWorld world = ServerLifecycleHooks.getCurrentServer().overworld();
		return world.getDataStorage().computeIfAbsent(BountyManager::new, BountyManager.NAME);
	}
	
	public static Random random = new Random();
	private static final Logger LOGGER = LogManager.getLogger("BattlePassPixelmon");
	
	private final Map<UUID, Bounty> DATA = new HashMap<UUID, Bounty>();
	private final List<UUID> ACTIVE_BOUNTIES = new ArrayList<UUID>();
	private final Multimap<UUID, BountyProgressTracker> ACCEPTED_BOUNTIES = LinkedHashMultimap.create(); //UUID playerUUID points to multiple UUID bountyUUID
	private final Multimap<UUID, BountyProgressTracker> COMPLETED_BOUNTIES = LinkedHashMultimap.create(); //UUID playerUUID points to multiple UUID bountyUUID
	
	@Override
	public void load(CompoundNBT nbt) {
		LOGGER.info("Loading bounties...");
		CompoundNBT saveData = nbt.getCompound("savedatabounties");
		//Load each data entry into the HashMap for runtime usage.
		for (int i = 0; saveData.contains("data"+i); i++) {
			Bounty bounty = Bounty.deserialize(saveData.getCompound("data"+i));
			LOGGER.info("Loading bounty: " + bounty.uuid + " : " + bounty.getFormattedString());
			DATA.put(bounty.uuid, bounty);
		}
		try {
			loadAcceptedData();
			loadCompletedData();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		LOGGER.info("Saving bounties...");
		CompoundNBT saveData = new CompoundNBT();
		int i = 0;
		//Save each HashMap entry as "data1", "data2"... "datai". For data storage.
		for(Iterator<Map.Entry<UUID, Bounty>> iterator = DATA.entrySet().iterator(); iterator.hasNext();) {
			saveData.put("data" + i++, iterator.next().getValue().serialize());
		}
		nbt.put("savedatabounties", saveData);
		//Return nbt to the WorldSavedData class
		try {
			LOGGER.info("Try Saving Data.");
			saveAcceptedData();
			saveCompletedData();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR SAVING DATA.");
			e.printStackTrace();
		}
		
		return nbt;
	}
	
	public void loadAcceptedData() throws FileNotFoundException {
		String fileName = Main.ACCEPTEDBOUNTIESCONFIGLOCATION;
		File f = new File(fileName);
		if (f.exists()) {
			Scanner fileInput = new Scanner(f);
			Gson gson = new Gson();
			ACCEPTED_BOUNTIES.clear();
			while (fileInput.hasNextLine()) {
				LOGGER.info("Parsing Accepted Line. ");
				String jsonLine = fileInput.nextLine();
				UUIDEntries entries = null;
				try {
					entries = gson.fromJson(jsonLine, UUIDEntries.class);
				}
				catch (Exception e) {
					LOGGER.info("Misconfigured line in AcceptedBounties, please be careful when formatting the configs. ");
					continue;
				}
				
				try {
					for (BountyProgressTracker bountyUUID : entries.trackers) {
						LOGGER.info("Putting " + entries.uuid + " : " + bountyUUID.uuid + " : " + bountyUUID.progress);
						ACCEPTED_BOUNTIES.put(entries.uuid, bountyUUID);
					}
				}
				catch (Exception e){
					LOGGER.info("Empty or misconfigured line in AcceptedBounties, please be careful when formatting the configs. ");
				}
				
			}
			fileInput.close();
		}
		else {
			LOGGER.info("AcceptedBounties.json File NOT Found, one will be made once there is relevant data to be saved.");
		}
	}
	
	public void saveAcceptedData() throws IOException {
		String fileName = Main.ACCEPTEDBOUNTIESCONFIGLOCATION;
		File f = new File(fileName);
		FileWriter fw = new FileWriter(f);
		Gson gson = new Gson();
		for (UUID uuidE : ACCEPTED_BOUNTIES.keySet()) {
			LOGGER.info("Try Saving Data ACCEPTED_BOUNTIES: " + uuidE);
			UUIDEntries entry = new UUIDEntries(uuidE, ACCEPTED_BOUNTIES.get(uuidE));
			String jsonLine = gson.toJson(entry, UUIDEntries.class) + "\n";
			//LOGGER.info("Appending to " + fileName);
			fw.append(jsonLine);
		}
		fw.close();
	}
	
	public void loadCompletedData() throws FileNotFoundException {
		String fileName = Main.COMPLETEDBOUNTIESCONFIGLOCATION;
		File f = new File(fileName);
		if (f.exists()) {
			Scanner fileInput = new Scanner(f);
			Gson gson = new Gson();
			COMPLETED_BOUNTIES.clear();
			while (fileInput.hasNextLine()) {
				String jsonLine = fileInput.nextLine();
				UUIDEntries entries = null;
				try {
					entries = gson.fromJson(jsonLine, UUIDEntries.class);
				}
				catch (Exception e) {
					LOGGER.info("Misconfigured line in CompletedBounties, please be careful when formatting the configs. ");
					continue;
				}
				
				try {
					for (BountyProgressTracker bountyUUID : entries.trackers) {
						COMPLETED_BOUNTIES.put(entries.uuid, new BountyProgressTracker(bountyUUID.uuid));
					}
				}
				catch (Exception e){
					LOGGER.info("Empty or misconfigured line in CompletedBounties, please be careful when formatting the configs. ");
				}
				
			}
			fileInput.close();
		}
		else {
			LOGGER.info("CompletedBounties.json File NOT Found, one will be made once there is relevant data to be saved.");
		}
	}
	
	public void saveCompletedData() throws IOException {
		String fileName = Main.COMPLETEDBOUNTIESCONFIGLOCATION;
		File f = new File(fileName);
		FileWriter fw = new FileWriter(f);
		Gson gson = new Gson();
		for (UUID uuidE : COMPLETED_BOUNTIES.keySet()) {
			LOGGER.info("Try Saving Data COMPLETED_BOUNTIES: " + uuidE);
			UUIDEntries entry = new UUIDEntries(uuidE, COMPLETED_BOUNTIES.get(uuidE));
			String jsonLine = gson.toJson(entry, UUIDEntries.class) + "\n";
			//LOGGER.info("Appending to " + fileName);
			fw.append(jsonLine);
		}
		fw.close();
	}
	
	public static Bounty getBounty(UUID uuid) {
		ServerWorld world = ServerLifecycleHooks.getCurrentServer().overworld();
		BountyManager bm = world.getDataStorage().computeIfAbsent(BountyManager::new, BountyManager.NAME);
		return bm.DATA.get(uuid);
	}
	
	public static  List<Bounty> getActiveBounties(){
		ServerWorld world = ServerLifecycleHooks.getCurrentServer().overworld();
		BountyManager bm = world.getDataStorage().computeIfAbsent(BountyManager::new, BountyManager.NAME);
		List<Bounty> list = new ArrayList<Bounty>();
		for (UUID uuid : bm.ACTIVE_BOUNTIES) {
			list.add(bm.DATA.get(uuid));
		}
		return list;
	}
	
	public static boolean refreshBoard() throws CommandSyntaxException {
		BountyManager bm = getDataHandler();
		if (refreshing) {
			//do not allow race condition
			return false;
		}
		refreshing = true; //acts as a lock to prevent a race condition.
		//Clear the active board.
		bm.ACTIVE_BOUNTIES.clear();
		//Clear old, unused bounties.
		LOGGER.info("Removing Old Entries...");
		List<UUID> removeOldList = new ArrayList<UUID>();
		for (Entry bountyEntry : bm.DATA.entrySet()) {
			LOGGER.info("Casting entryvalue to bounty...");
			Bounty b = (Bounty) bountyEntry.getValue();
			LOGGER.info("Testing bounty: " + b.getFormattedString());
			if (b.players < 1) {
				//No one is working on this bounty anymore, delete it.
				removeOldList.add(b.uuid);
			}
		}
		if (removeOldList.size() > 0) {
			bm.setDirty();
			for (UUID uuid : removeOldList) {
				bm.DATA.remove(uuid);
			}
		}
		
		//Create X bounties and populate the active board.
		for (int i = 0; i < 12; i++) {
			Bounty newBounty = Bounty.createRandomBounty();
			bm.DATA.put(newBounty.uuid, newBounty);
			bm.ACTIVE_BOUNTIES.add(newBounty.uuid);
		}
		for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
			BountyGUIHandler.bgui.updateBountyJournal(player, false);
		}
		refreshing = false; //unlock the board.
		return true;
	}
	
	public static  List<Bounty> getPlayerAcceptedBounties(UUID playerUUID) {
		BountyManager bm = getDataHandler();
		if (bm.ACCEPTED_BOUNTIES.containsKey(playerUUID) == false) {
			return new ArrayList<Bounty>();
		}
		List<Bounty> bounties = new ArrayList<Bounty>();
		for (BountyProgressTracker tracker : bm.ACCEPTED_BOUNTIES.get(playerUUID)) {
			bounties.add(getBounty(tracker.uuid));
		}
		return bounties;
	}
	
	public static  List<UUID> getPlayerCompletedBounties(UUID playerUUID) {
		BountyManager bm = getDataHandler();
		if (bm.COMPLETED_BOUNTIES.containsKey(playerUUID) == false) {
			return null;
		}
		List<BountyProgressTracker> trackers = (List<BountyProgressTracker>) bm.COMPLETED_BOUNTIES.get(playerUUID);
		List<UUID> uuids = new ArrayList<UUID>();
		for (BountyProgressTracker t : trackers) {
			uuids.add(t.uuid);
		}
		return uuids;
	}
	
	public static boolean acceptBounty(UUID playerUUID, UUID bountyUUID) {
		BountyManager bm = getDataHandler();
		Collection<BountyProgressTracker> tempList = bm.ACCEPTED_BOUNTIES.get(playerUUID);
		for (BountyProgressTracker bpt : tempList) {
			if (bpt.uuid == bountyUUID) {
				return false;
			}
		}
		tempList = bm.COMPLETED_BOUNTIES.get(playerUUID);
		for (BountyProgressTracker bpt : tempList) {
			if (bpt.uuid == bountyUUID) {
				return false;
			}
		}
		
		bm.ACCEPTED_BOUNTIES.put(playerUUID, new BountyProgressTracker(bountyUUID));
		bm.DATA.get(bountyUUID).players++;
		for (BountyProgressTracker bpt : bm.ACCEPTED_BOUNTIES.get(playerUUID)) {
			LOGGER.info(bpt.uuid + " progress: " + bpt.progress);
		}
		bm.setDirty();
		return true;
	}
	
	public static  boolean discardBounty(UUID playerUUID, UUID bountyUUID) {
		BountyManager bm = getDataHandler();
		if (bm.ACCEPTED_BOUNTIES.containsEntry(playerUUID, bountyUUID)) {
			bm.ACCEPTED_BOUNTIES.remove(playerUUID, bountyUUID);
			bm.DATA.get(bountyUUID).players--;
			bm.setDirty();
			return true;
		}
		return false;
	}
	
	public static boolean completeBounty(ServerPlayerEntity player, UUID bountyUUID) {
		BountyManager bm = getDataHandler();
		UUID playerUUID = player.getUUID();
		if (bm.ACCEPTED_BOUNTIES.containsEntry(playerUUID, bountyUUID)) {
			//Dispense reward
			BattlePassManager.grantProgress(playerUUID, bm.DATA.get(bountyUUID).reward, false);
			//Move from ACCEPTED to COMPLETED
			bm.ACCEPTED_BOUNTIES.remove(playerUUID, bountyUUID);
			bm.COMPLETED_BOUNTIES.put(playerUUID, new BountyProgressTracker(bountyUUID));
			bm.DATA.get(bountyUUID).players--;
			BattlePassManager.sendPlayerInfo(player, bm.DATA.get(bountyUUID).reward, "Completed a Bounty:");
			bm.setDirty();
			return true;
		}
		return false;
	}
	
	public static class Bounty{
		public UUID uuid;
		int players;
		List<Element> elements;
		List<ObjectiveTag> tags;
		int goal;
		int reward;
		
		//Blank Bounty constructor for new Bounties
		public Bounty(){
			elements = new ArrayList<Element>();
			tags = new ArrayList<ObjectiveTag>();
			uuid = UUID.randomUUID();
			players = 0;
			goal = 0;
			reward = 0;
		}
		
		//Bounty constructor for loading in Bounties
		public Bounty(UUID _uuid, int _players, List<Element> _elements, List<ObjectiveTag> _tags, int _goal, int _reward){
			elements = _elements;
			tags = _tags;
			uuid = _uuid;
			players = _players;
			goal = _goal;
			reward = _reward;
		}
		
		public boolean addTag(ObjectiveTag _tag) {
			//Don't combine these tags unless they are KILL and CATCH
			switch (_tag) {
			case CATCH:
				if (tags.size() > 0 && !tags.contains(ObjectiveTag.KILL)) {
					return false;
				}
				break;
			case KILL:
				if (tags.size() > 0 && !tags.contains(ObjectiveTag.CATCH)) {
					return false;
				}
				break;
			case LEGEND:
				if (tags.size() > 0 ) {
					return false;
				}
				break;
			case TRAINER:
				if (tags.size() > 0 ) {
					return false;
				}
				break;
			case BOSS:
				if (tags.size() > 0 ) {
					return false;
				}
				break;
			case FISH:
				if (tags.size() > 0 ) {
					return false;
				}
				break;
			default:
				break;
			}
			tags.add(_tag);
			return true;
		}
		
		public void addElement(Element _element) {
			if (elements.contains(_element)) {
				return;
			}
			elements.add(_element);
		}
		
		public String getFormattedString() {
			String s = "";
			boolean hasCatch = tags.contains(ObjectiveTag.CATCH);
			boolean hasKill = tags.contains(ObjectiveTag.KILL);
			if (hasCatch || hasKill) {
				if (hasCatch && hasKill) {
					s += "Catch or Defeat ";
				}
				else if (hasCatch) {
					s += "Catch ";
				}
				else {
					s += "Defeat ";
				}
				
				s += goal + " ";
				
				for (Element element : elements) {
					s += element.toString().toLowerCase() + " ";
				}
				s += "Pokemon.";
				//Example: Catch or Defeat 50 fire water grass Pokemon.
			}
			if (tags.contains(ObjectiveTag.TRAINER)) {
				s += "Defeat " + goal + " Trainer(s).";
			}
			if (tags.contains(ObjectiveTag.LEGEND)) {
				s += "Catch or Defeat " + goal + " Legendary Pokemon.";
			}
			if (tags.contains(ObjectiveTag.BOSS)) {
				s += "Defeat " + goal + " Boss Pokemon";
			}
			if (tags.contains(ObjectiveTag.FISH)) {
				s += "Reel in While Fishing " + goal + " times.";
			}
			return s;
		}
		
		public static Bounty createRandomBounty() {
			Bounty bounty = new Bounty();
			
			//Add objectives
			int pick = BountyManager.random.nextInt(ObjectiveTag.values().length);
			ObjectiveTag tag = ObjectiveTag.values()[pick];
			bounty.addTag(tag);
			float multiplier = (random.nextFloat() * 5) + 1f;
			if (tag == ObjectiveTag.LEGEND) {
				multiplier = 1;
			}
			bounty.goal += Math.max(1, tag.baseAmount * (multiplier));
			bounty.reward += bounty.goal * tag.baseReward;
			
			//Add random pokemon elements to be used with CATCH/KILL objectives.
			if (bounty.tags.contains(ObjectiveTag.CATCH) || bounty.tags.contains(ObjectiveTag.KILL)) {
				int elementAmount = random.nextInt(4);
				int[] elements = new int[] {-1, -1, -1};
				for (int i = 0; i < elementAmount; i++) {
					int element = random.nextInt(Element.values().length);
					while (elements[0] == element || elements[1] == element) {
						element++;
						if (element > Element.values().length) {
							element = 0;
						}
					}
					elements[i] = element;
				}
				for (int i : elements) {
					if (i == -1) {
						continue;
					}
					bounty.addElement(Element.values()[i]);
				}
				
				if (elementAmount > 0) {
					bounty.reward *= (1 + (3 / elementAmount));
				}
				
				
			}
			
			return bounty;
		}
		
		//Save data to an NBT
				public CompoundNBT serialize() {
					CompoundNBT nbt = new CompoundNBT();
					nbt.putUUID("uuid", uuid);
					nbt.putInt("players", players);
					nbt.putInt("goal", goal);
					nbt.putInt("reward", reward);
					int i = 0;
					for (Element element : elements) {
						nbt.putString("element" + i++, element.toString());
					}
					i = 0;
					for (ObjectiveTag tag : tags) {
						nbt.putString("tag" + i++, tag.toString());
					}
					
					return nbt;
				}
				
				//Create a Bounty from NBT
				public static Bounty deserialize(CompoundNBT nbt) {
					List<Element> elements = new ArrayList<Element>();
					int i = 0;
					while (nbt.contains("element" + i)) {
						String s = nbt.getString("element" + i++);
						elements.add(Element.parseOrNull(s));
					}
					List<ObjectiveTag> tags = new ArrayList<ObjectiveTag>();
					i = 0;
					while (nbt.contains("tag" + i)) {
						String s = nbt.getString("tag" + i++);
						tags.add(ObjectiveTag.parseOrNull(s));
					}
					return new Bounty(nbt.getUUID("uuid"), nbt.getInt("players"), elements, tags,  nbt.getInt("goal"),  nbt.getInt("reward"));
				}
		
		public enum ObjectiveTag {
			//NAME (baseReward, baseAmount),
			KILL (5, 10),
			CATCH (10, 5),
			LEGEND (500, 1),
			TRAINER (100, 1),
			BOSS (100, 1),
			FISH (50, 10);
			
			int baseReward;
			int baseAmount;
			ObjectiveTag(int _baseReward, int _baseAmount){
				baseReward = _baseReward;
				baseAmount = _baseAmount;
			}
			static ObjectiveTag parseOrNull(String s) {
				switch(s.toLowerCase()) {
				case "kill":
					return ObjectiveTag.KILL;
				case "catch":
					return ObjectiveTag.CATCH;
				case "legend":
					return ObjectiveTag.LEGEND;
				case "trainer":
					return ObjectiveTag.TRAINER;
				case "boss":
					return ObjectiveTag.BOSS;
				case "fish":
					return ObjectiveTag.FISH;
				}
				return null;
			}
		}
	}
	public static class UUIDEntries {
		UUID uuid;
		BountyProgressTracker[] trackers;
		
		public UUIDEntries(UUID _uuid, Collection<BountyProgressTracker> _trackers) {
			uuid = _uuid;
			trackers = new BountyProgressTracker[_trackers.size()];
			int i = 0;
			for (BountyProgressTracker id : _trackers) {
				trackers[i++] = id;
			}
		}
	}
	
	public static class BountyProgressTracker {
		public UUID uuid;
		public int progress;
		
		public BountyProgressTracker(UUID _uuid) {
			uuid = _uuid;
			progress = 0;
		}
		
		public BountyProgressTracker(UUID _uuid, int _progress) {
			uuid = _uuid;
			progress = _progress;
		}
	}
	
}
