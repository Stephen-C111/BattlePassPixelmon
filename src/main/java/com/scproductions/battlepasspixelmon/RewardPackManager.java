package com.scproductions.battlepasspixelmon;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
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
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLPaths;
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
				new RewardPack(0, new ItemPair[] { new ItemPair("pixelmon:ancient_poke_ball", 32) }),
				new RewardPack(1, new ItemPair[] { new ItemPair("pixelmon:green_pc", 1), new ItemPair("pixelmon:green_healer", 1) }),
				new RewardPack(2, new ItemPair[] { new ItemPair("pixelmon:curry_burger", 8), new ItemPair("pixelmon:potion", 16)  }),
				new RewardPack(3, new ItemPair[] { new ItemPair("pixelmon:green_poke_bag", 1) }),
				new RewardPack(4, new ItemPair[] { new ItemPair("minecraft:bookshelf", 5), new ItemPair("minecraft:experience_bottle", 16) }),
				new RewardPack(5, new ItemPair[] { new ItemPair("minecraft:experience_bottle", 32) }),
				new RewardPack(6, new ItemPair[] { new ItemPair("pixelmon:curry_apple", 8) }),
				new RewardPack(7, new ItemPair[] { new ItemPair("pixelmon:hyper_potion", 16) }),
				new RewardPack(8, new ItemPair[] { new ItemPair("pixelmon:curry_cheese", 8) }),
				new RewardPack(9, new ItemPair[] { new ItemPair("pixelmon:revive", 8) }),
				new RewardPack(10, new ItemPair[] { new ItemPair("pixelmon:thunder_stone", 4), new ItemPair("pixelmon:fire_stone", 4), }),
				new RewardPack(20, new ItemPair[] { new ItemPair("pixelmon:thunder_stone", 6), new ItemPair("pixelmon:fire_stone", 6), }),
				new RewardPack(50, new ItemPair[] { new ItemPair("pixelmon:master_ball", 1) }),
				new RewardPack(100, new ItemPair[] { new ItemPair("pixelmon:master_ball", 1) }),
				new RewardPack(200, new ItemPair[] { new ItemPair("pixelmon:park_ball", 1) }),
				new RewardPack(300, new ItemPair[] { new ItemPair("pixelmon:origin_ball", 1) }),
				new RewardPack(400, new ItemPair[] { new ItemPair("pixelmon:master_ball", 2) }),
				new RewardPack(500, new ItemPair[] { new ItemPair("pixelmon:park_ball", 2) })
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
			//LOGGER.info("Grabbing RewardPack");
			RewardPack rp = rpE.getValue();
			//LOGGER.info("rp grabbed, converting to Json");
			String jsonLine = gson.toJson(rp, RewardPackManager.RewardPack.class) + "\n";
			//LOGGER.info("Appending to " + fileName);
			fw.append(jsonLine);
		}
		LOGGER.info("Closing FileWriter.");
		fw.close();
	}
	
	public void loadPacksJson(String fileName) throws IOException {
		File f = new File(fileName);
		if (f.exists()) {
			LOGGER.info("RewardPackConfig.json File Found, loading data in...");
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
			LOGGER.info("RewardPackConfig.json File NOT Found, creating a default version.");
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
					//LOGGER.info(rp.pairs[0].itemID + "|" + rp.rank);
				}
			}
		}
		
		//Grant player rewards.
		for (RewardPack rp : packs) {
			//LOGGER.info("Evaluating pack");
			for (ItemPair pair : rp.pairs) {
				//LOGGER.info("Grabbing Items");
				if (ForgeRegistries.ITEMS.containsKey(new ResourceLocation(pair.itemID))) {
					Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(pair.itemID));
					ItemStack stack = new ItemStack(item, pair.amount);
					ItemHandlerHelper.giveItemToPlayer(player, stack);
					player.sendMessage(new StringTextComponent(
							"Claimed " + stack.getHoverName().getString() + ", for reaching rank " + rp.rank), player.getUUID());
				}
				else {
					LOGGER.info("Key not found: " + pair.itemID);
					player.sendMessage(new StringTextComponent(
							"Could not claim item with id of: " + pair.itemID + 
							". Please reach out to a server administrator so they can resolve this issue inside defaultconfigs\\RewardPackConfig.json"), player.getUUID());
				}
				
			}
			//Add UUIDs from rp.uuid to battlepass.claimedPacks
			claimedPacks.add(rp.uuid);
		}
		BattlePassManager.updateClaimedPacks(player, claimedPacks);
	}
	
	public void createNewRewardPack(int rank, ItemPair[] items) throws IOException {
		RewardPack rp = new RewardPack(rank, items);
		DATA.put(rank, rp);
		Path path = FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath());
		rpm.savePacksJson(path.getFileName().toString() + "\\RewardPackConfig.json");
	}
	
	public void createNewRewardPack(RewardPack rp) throws IOException {
		DATA.put(rp.rank, rp);
		Path path = FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath());
		rpm.savePacksJson(path.getFileName().toString() + "\\RewardPackConfig.json");
	}
	
	public static class RewardPack{
		public int rank;
		public UUID uuid;
		ItemPair[] pairs;
		public static class ItemPair{
			String itemID;
			int amount;
			public ItemPair(String _itemID, int _amount){
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
