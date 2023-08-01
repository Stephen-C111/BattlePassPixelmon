package com.scproductions.battlepasspixelmon.bounties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.pixelmonmod.pixelmon.api.pokemon.Element;

import net.minecraft.nbt.CompoundNBT;

public class BountyManager {
	public static BountyManager bm = new BountyManager();
	public static Random random = new Random();
	
	private final Map<UUID, Bounty> DATA = new HashMap<UUID, Bounty>();
	private final List<UUID> ACTIVE_BOUNTIES = new ArrayList<UUID>();
	private final Multimap<UUID, UUID> ACCEPTED_BOUNTIES = LinkedHashMultimap.create(); //UUID playerUUID points to multiple UUID bountyUUID
	private final Multimap<UUID, UUID> COMPLETED_BOUNTIES = LinkedHashMultimap.create(); //UUID playerUUID points to multiple UUID bountyUUID
	
	public void loadData() {
		
	}
	
	public void loadAcceptedData() {
		
	}
	
	public void loadCompletedData() {
		
	}
	
	public Bounty getBounty(UUID uuid) {
		return DATA.get(uuid);
	}
	
	public List<Bounty> getActiveBounties(){
		List<Bounty> list = new ArrayList<Bounty>();
		for (UUID uuid : ACTIVE_BOUNTIES) {
			list.add(DATA.get(uuid));
		}
		return list;
	}
	
	public void refreshBoard() {
		//Clear the active board.
		ACTIVE_BOUNTIES.clear();
		//Clear old, unused bounties.
		List<UUID> removeOldList = new ArrayList<UUID>();
		for (Entry bountyEntry : DATA.entrySet()) {
			Bounty b = (Bounty) bountyEntry.getValue();
			if (b.players == 0) {
				//No one is working on this bounty anymore, delete it.
				removeOldList.add(b.uuid);
			}
		}
		for (UUID uuid : removeOldList) {
			DATA.remove(uuid);
		}
		//Create X bounties and populate the active board.
		for (int i = 0; i < 12; i++) {
			Bounty newBounty = Bounty.createRandomBounty();
			DATA.put(newBounty.uuid, newBounty);
			ACTIVE_BOUNTIES.add(newBounty.uuid);
		}
	}
	
	public List<Bounty> getPlayerAcceptedBounties(UUID playerUUID) {
		if (ACCEPTED_BOUNTIES.containsKey(playerUUID) == false) {
			return null;
		}
		List<Bounty> bounties = new ArrayList<Bounty>();
		for (UUID uuid : ACCEPTED_BOUNTIES.get(playerUUID)) {
			bounties.add(DATA.get(uuid));
		}
		return bounties;
	}
	
	public List<UUID> getPlayerCompletedBounties(UUID playerUUID) {
		if (COMPLETED_BOUNTIES.containsKey(playerUUID) == false) {
			return null;
		}
		List<UUID> uuids = (List<UUID>) COMPLETED_BOUNTIES.get(playerUUID);
		return uuids;
	}
	
	public static class Bounty{
		UUID uuid;
		int players;
		List<Element> elements;
		List<ObjectiveTag> tags;
		int progress;
		int goal;
		int reward;
		
		//Blank Bounty constructor for new Bounties
		public Bounty(){
			elements = new ArrayList<Element>();
			tags = new ArrayList<ObjectiveTag>();
			uuid = UUID.randomUUID();
			players = 0;
			progress = 0;
			goal = 0;
			reward = 0;
		}
		
		//Bounty constructor for loading in Bounties
		public Bounty(UUID _uuid, int _players, List<Element> _elements, List<ObjectiveTag> _tags, int _progress, int _goal, int _reward){
			elements = _elements;
			tags = _tags;
			uuid = _uuid;
			players = _players;
			progress = _progress;
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
			float multiplier = random.nextFloat(1f);
			bounty.goal += Math.min(1, tag.baseAmount * (multiplier + 0.8f));
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
					nbt.putInt("progress", progress);
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
					while (nbt.contains("element" + i)) {
						String s = nbt.getString("element" + i++);
						elements.add(Element.parseOrNull(s));
					}
					return new Bounty(nbt.getUUID("uuid"), nbt.getInt("players"), elements, tags,  nbt.getInt("progress"),  nbt.getInt("goal"),  nbt.getInt("reward"));
				}
		
		public enum ObjectiveTag {
			//NAME (baseReward, baseAmount),
			KILL (5, 10),
			CATCH (10, 5),
			LEGEND (500, 1),
			TRAINER (100, 1),
			BOSS (100, 1),
			FISH (5, 1);
			
			int baseReward;
			int baseAmount;
			ObjectiveTag(int _baseReward, int _baseAmount){
				baseReward = _baseReward;
				baseAmount = _baseAmount;
			}
		}
	}
}
