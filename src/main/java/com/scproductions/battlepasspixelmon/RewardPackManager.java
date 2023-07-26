package com.scproductions.battlepasspixelmon;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.scproductions.battlepasspixelmon.RewardPackManager.RewardPack.ItemPair;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RewardPackManager {
	
	
	public static RewardPackManager rpm = new RewardPackManager();
	
	private static final Logger LOGGER = LogManager.getLogger("BattlePassPixelmon");
	
	public static final String NAME = Main.MOD_ID + "_RewardPackManager";
	
	private final Multimap<Integer, RewardPack> DATA = LinkedHashMultimap.create();
	
	public void createDefaultPacks() {
		RewardPack[] rps = {
				new RewardPack(0, new ItemPair[] { new ItemPair("minecraft:door", 1) })
							};
		for (RewardPack rp : rps) {
			DATA.put(rp.rank, rp);
		}
		LOGGER.info(checkData());
	}
	
	public void savePacksJson(String fileName) throws IOException {
		File f = new File(fileName);
		FileWriter fw = new FileWriter(f);
		Gson gson = new Gson();
		LOGGER.info("Attempting to write to " + fileName);
		for (Entry<Integer, RewardPack> rpE : DATA.entries()) {
			LOGGER.info("Grabbing RewardPack");
			RewardPack rp = rpE.getValue();
			LOGGER.info("rp grabbed, converting to Json");
			String jsonLine = gson.toJson(rp, RewardPackManager.RewardPack.class) + "\n";
			LOGGER.info("Appending to " + fileName);
			fw.append(jsonLine);
		}
		LOGGER.info("Closing FileWriter.");
		fw.close();
	}
	
	public void loadPacksJson(String fileName) throws IOException {
		File f = new File(fileName);
		if (f.exists()) {
			LOGGER.info("File Found");
			Scanner fileInput = new Scanner(f);
			Gson gson = new Gson();
			while (fileInput.hasNextLine()) {
				String jsonLine = fileInput.nextLine();
				RewardPack rp = gson.fromJson(jsonLine, RewardPackManager.RewardPack.class);
				DATA.put(rp.rank, rp);
			}
			fileInput.close();
		}
		else {
			LOGGER.info("File NOT Found");
			createDefaultPacks();
			savePacksJson(fileName);
		}
	}
	
	public String checkData() {
		String s = "";
		s += DATA.entries().size();
		return s;
	}
	
	public List<RewardPack> getRewardPacksForRank(int rank) {
		List<RewardPack> packs = new ArrayList<RewardPack>();
		if (DATA.containsKey(rank)) {
			for (RewardPack rp : DATA.get(rank)) {
				packs.add(rp);
			}
		}
		return packs;
	}
	
	public void claimRewardPacks(ServerPlayerEntity player) {
		List<RewardPack> packs = new ArrayList<RewardPack>();
		List<UUID> claimedPacks = new ArrayList<>();
		for (UUID uuid : BattlePassManager.getClaimedUUIDS(player.getUUID())) {
			claimedPacks.add(uuid);
		}
		int rank = BattlePassManager.getRank(player.getUUID());
		for (int i = 0; i <= rank; i++) {
			//grab all packs that should be checked.
			List<RewardPack> list = getRewardPacksForRank(i);
			for (RewardPack rp : list) {
				if (claimedPacks.contains(rp.uuid)) {
					continue;
				}
				else {
					packs.add(rp);
					LOGGER.info(rp.pairs[0].itemID + "|" + rp.rank);
				}
			}
		}
		
		//Grant player rewards.
		for (RewardPack rp : packs) {
			LOGGER.info("Evaluating pack");
			for (ItemPair pair : rp.pairs) {
				LOGGER.info("Grabbing Items");
				if (ForgeRegistries.ITEMS.containsKey(new ResourceLocation(pair.itemID))) {
					Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(pair.itemID));
					ItemStack stack = new ItemStack(item, pair.amount);
					ItemHandlerHelper.giveItemToPlayer(player, stack);
					player.sendMessage(new StringTextComponent(
							"Claimed " + stack.getDisplayName() + ", for reaching rank " + rp.rank), player.getUUID());
				}
				else {
					LOGGER.info("Key not found: " + pair.itemID);
				}
				
			}
			//Add UUIDs from rp.uuid to battlepass.claimedPacks
			claimedPacks.add(rp.uuid);
		}
		BattlePassManager.updateClaimedPacks(player, claimedPacks);
	}
	
	public void createNewRewardPack(int rank, ItemPair[] items) {
		RewardPack rp = new RewardPack(rank, items);
		DATA.put(rank, rp);
	}
	
	public void createNewRewardPack(RewardPack rp) {
		DATA.put(rp.rank, rp);
	}
	
	static class RewardPack{
		public int rank;
		public UUID uuid;
		ItemPair[] pairs;
		static class ItemPair{
			String itemID;
			int amount;
			ItemPair(String _itemID, int _amount){
				itemID = _itemID;
				amount = _amount;
			}
		}
		
		
		RewardPack(int _rank, ItemPair[] _pairs){
			rank = _rank;
			uuid = UUID.randomUUID();
			pairs = _pairs;
		}
		
		RewardPack(int _rank, ItemPair[] _pairs, UUID _uuid){
			rank = _rank;
			pairs = _pairs;
			uuid = _uuid;
		}
		
		
	}
}
