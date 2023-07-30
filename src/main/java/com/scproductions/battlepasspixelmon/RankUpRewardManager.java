package com.scproductions.battlepasspixelmon;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class RankUpRewardManager {
	public static RankUpRewardManager rrm = new RankUpRewardManager();
	private static final Logger LOGGER = LogManager.getLogger("BattlePassPixelmon");
	private final List<WeightedItemPair> DATA = new ArrayList<WeightedItemPair>();
	private final Map<Integer, WeightedItemPair> WEIGHTED_DATA = new HashMap<Integer, WeightedItemPair>();
	private Random random = new Random();
	private int maxKey;
	
	public void saveRewardsJson() throws IOException {
		String fileName = Main.RANKUPREWARDCONFIGLOCATION;
		File f = new File(fileName);
		FileWriter fw = new FileWriter(f);
		Gson gson = new Gson();
		
		for (WeightedItemPair pair : DATA) {
			String jsonLine = gson.toJson(pair, WeightedItemPair.class) + "\n";
			//LOGGER.info("Appending to " + fileName);
			fw.append(jsonLine);
		}
		
		fw.close();
	}
	
	public void loadRewardsJson() throws IOException {
		String fileName = Main.RANKUPREWARDCONFIGLOCATION;
		File f = new File(fileName);
		if (f.exists()) {
			Scanner fileInput = new Scanner(f);
			Gson gson = new Gson();
			DATA.clear();
			while (fileInput.hasNextLine()) {
				String jsonLine = fileInput.nextLine();
				WeightedItemPair pair = gson.fromJson(jsonLine, WeightedItemPair.class);
				DATA.add(pair);
			}
			int key = 0;
			for (WeightedItemPair pair : DATA) {
				for (int i = 0; i < pair.weight; i++) {
					WEIGHTED_DATA.put(key++, pair);
				}
			}
			maxKey = key;
			fileInput.close();
		}
		else {
			
			createDefaultRewards();
		}
	}
	
	private void createDefaultRewards() throws IOException {
		DATA.add(new WeightedItemPair(new ItemPair("pixelmon:xl_exp_candy", 1), 10));
		DATA.add(new WeightedItemPair(new ItemPair("pixelmon:dream_ball", 64), 10));
		DATA.add(new WeightedItemPair(new ItemPair("pixelmon:master_ball", 1), 4));
		DATA.add(new WeightedItemPair(new ItemPair("pixelmon:ultra_ball", 1), 10));
		DATA.add(new WeightedItemPair(new ItemPair("pixelmon:dusk_ball", 64), 10));
		saveRewardsJson();
		loadRewardsJson();
	}
	
	public static void createNewRandomReward(int weight, ItemPair pair) throws IOException {
		WeightedItemPair wpair = new WeightedItemPair(pair, weight);
		rrm.DATA.add(wpair);
		rrm.saveRewardsJson();
	}
	
	public static String rollRandomReward(ServerPlayerEntity player) throws CommandSyntaxException {
		int roll = rrm.random.nextInt(rrm.maxKey);
		ItemPair pair = rrm.WEIGHTED_DATA.get(roll).pair;
		if (ForgeRegistries.ITEMS.containsKey(new ResourceLocation(pair.itemID))) {
			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(pair.itemID));
			ItemStack stack = new ItemStack(item, pair.amount);
				CompoundNBT nbt = JsonToNBT.parseTag(pair.nbt);
				stack.setTag(nbt);
			ItemHandlerHelper.giveItemToPlayer(player, stack);
			if (pair.amount > 1) {
				return "Bundle(s) of " + pair.amount + " " + stack.getHoverName().getString();
			}
			return stack.getHoverName().getString();
		}
		else {
			LOGGER.info("Random Reward Key not found: " + pair.itemID);
			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:paper"));
			ItemStack stack = new ItemStack(item, pair.amount);
			CompoundNBT nbt = JsonToNBT.parseTag(
					"{display:{Name:'[{\"text\":\"Misconfigured Random Reward Receipt\",\"color\":\"red\"}]',Lore:['[{\"text\":\"This item was created with the misconfigured id of: "
			+ pair.itemID + 
			"\",\"italic\":false,\"color\":\"red\"}]','[{\"text\":\"Please reach out to a server administrator/operator for help resolving this issue. \",\"italic\":false,\"color\":\"red\"}]']}}");
				stack.setTag(nbt);
			ItemHandlerHelper.giveItemToPlayer(player, stack);
			player.sendMessage(new StringTextComponent(
					"Could not claim item with id of: " + pair.itemID + 
					". Please reach out to a server administrator so they can resolve this issue inside " + Main.RANKUPREWARDCONFIGLOCATION), player.getUUID());
			return "Misconfigured Reward";
		}
	}

	public static class WeightedItemPair {
		ItemPair pair;
		int weight;
		WeightedItemPair(ItemPair _pair, int _weight){
			pair = _pair;
			weight = _weight;
		}
	}
}
