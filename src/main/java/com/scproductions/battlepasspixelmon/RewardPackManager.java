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
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.scproductions.battlepasspixelmon.RewardPackManager.RewardPack.ItemPair;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
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
	
	public void createDefaultPacks() throws IOException {
		RewardPack[] rps = {
				new RewardPack(0, new ItemPair[] { new ItemPair("pixelmon:ancient_poke_ball", 32) }),
				new RewardPack(1, new ItemPair[] { new ItemPair("pixelmon:green_pc", 1), new ItemPair("pixelmon:green_healer", 1) }),
				new RewardPack(2, new ItemPair[] { new ItemPair("pixelmon:curry_burger", 8), new ItemPair("pixelmon:potion", 16), new ItemPair("pixelmon:starf_berry", 16),
						new ItemPair("pixelmon:cooking_pot", 1), new ItemPair("minecraft:flint_and_steel", 1) }),
				new RewardPack(3, new ItemPair[] { new ItemPair("pixelmon:green_poke_bag", 1), new ItemPair("pixelmon:ancient_great_ball", 64), new ItemPair("pixelmon:ancient_ultra_ball", 32) }),
				new RewardPack(4, new ItemPair[] { new ItemPair("minecraft:bookshelf", 5), new ItemPair("minecraft:experience_bottle", 16) }),
				new RewardPack(5, new ItemPair[] { new ItemPair("minecraft:experience_bottle", 32) }),
				new RewardPack(6, new ItemPair[] { new ItemPair("pixelmon:curry_apple", 8), new ItemPair("pixelmon:starf_berry", 24) }),
				new RewardPack(7, new ItemPair[] { new ItemPair("pixelmon:hyper_potion", 16) }),
				new RewardPack(8, new ItemPair[] { new ItemPair("pixelmon:curry_cheese", 8), new ItemPair("pixelmon:starf_berry", 24) }),
				new RewardPack(9, new ItemPair[] { new ItemPair("pixelmon:revive", 8) }),
				new RewardPack(10, new ItemPair[] { new ItemPair("pixelmon:thunder_stone", 4), new ItemPair("pixelmon:fire_stone", 4), }),
				new RewardPack(20, new ItemPair[] { new ItemPair("pixelmon:thunder_stone", 6), new ItemPair("pixelmon:fire_stone", 6), }),
				new RewardPack(30, new ItemPair[] { new ItemPair("pixelmon:thunder_stone", 8), new ItemPair("pixelmon:fire_stone", 8), }),
				new RewardPack(40, new ItemPair[] { new ItemPair("pixelmon:thunder_stone", 10), new ItemPair("pixelmon:fire_stone", 10), }),
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
		savePacksJson();
	}
	
	public void savePacksJson() throws IOException {
		String fileName = Main.REWARDPACKCONFIGLOCATION;
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
	
	public void loadPacksJson() throws IOException {
		String fileName = Main.REWARDPACKCONFIGLOCATION;
		File f = new File(fileName);
		if (f.exists()) {
			LOGGER.info("RewardPackConfig.json File Found, loading data in...");
			Scanner fileInput = new Scanner(f);
			Gson gson = new Gson();
			DATA.clear();
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
	
	public void claimRewardPacks(ServerPlayerEntity player) throws CommandSyntaxException {
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
		
		if (packs.size() == 0) {
			player.sendMessage(new StringTextComponent(
					"There are no eligible Reward Packs for you to claim at the moment. Check back when you rank up!"), player.getUUID());
			return;
		}
		
		//Grant player rewards.
		for (RewardPack rp : packs) {
			//LOGGER.info("Evaluating pack");
			for (ItemPair pair : rp.pairs) {
				//LOGGER.info("Grabbing Items");
				if (ForgeRegistries.ITEMS.containsKey(new ResourceLocation(pair.itemID))) {
					Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(pair.itemID));
					ItemStack stack = new ItemStack(item, pair.amount);
						CompoundNBT nbt = JsonToNBT.parseTag(pair.nbt);
						stack.setTag(nbt);
					ItemHandlerHelper.giveItemToPlayer(player, stack);
					player.sendMessage(new StringTextComponent(
							"Claimed " + stack.getHoverName().getString() + ", for reaching rank " + rp.rank), player.getUUID());
				}
				else {
					LOGGER.info("Key not found: " + pair.itemID);
					Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:paper"));
					ItemStack stack = new ItemStack(item, pair.amount);
						CompoundNBT nbt = JsonToNBT.parseTag(
								"{display:{Name:'{\"text\":\"Misconfigured Reward\",\"color\":\"red\",...}',Lore:['{\"text\":\"This item was created with the misconfigured id of: "
						+ pair.itemID + "\"}','{\"text\":\"Please reach out to a server administrator/operator for help resolving this issue.\"}',color:3949738]}}");
						stack.setTag(nbt);
					ItemHandlerHelper.giveItemToPlayer(player, stack);
					player.sendMessage(new StringTextComponent(
							"Could not claim item with id of: " + pair.itemID + 
							". Please reach out to a server administrator so they can resolve this issue inside" + Main.REWARDPACKCONFIGLOCATION), player.getUUID());
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
		rpm.savePacksJson();
	}
	
	public void createNewRewardPack(RewardPack rp) throws IOException {
		DATA.put(rp.rank, rp);
		Path path = FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath());
		rpm.savePacksJson();
	}
	
	public static class RewardPack{
		public int rank;
		public UUID uuid;
		ItemPair[] pairs;
		public static class ItemPair{
			String itemID;
			int amount;
			String nbt;
			public ItemPair(String _itemID, int _amount){
				itemID = _itemID;
				amount = _amount;
				nbt = "{}";
			}
			public ItemPair(String _itemID, int _amount, CompoundNBT _nbt){
				itemID = _itemID;
				amount = _amount;
				nbt = _nbt.toString();
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
