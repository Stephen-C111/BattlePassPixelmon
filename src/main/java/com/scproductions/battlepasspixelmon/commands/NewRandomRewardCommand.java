package com.scproductions.battlepasspixelmon.commands;

import java.io.IOException;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.scproductions.battlepasspixelmon.RankUpRewardManager;
import com.scproductions.battlepasspixelmon.RewardPackManager.RewardPack.ItemPair;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.command.arguments.ItemInput;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;

public class NewRandomRewardCommand {
	public NewRandomRewardCommand(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
				Commands.literal("newrandomreward")
				.requires((commandSource) -> {return commandSource.hasPermission(2);})
					.then(Commands.argument("weight", IntegerArgumentType.integer(0))
						.then(Commands.argument("item", ItemArgument.item())
							.then(Commands.argument("amount", IntegerArgumentType.integer(1))
							.executes((command) -> 
							{
								try {
									createRandomReward(command.getSource(), IntegerArgumentType.getInteger(command, "weight"), ItemArgument.getItem(command, "item"), IntegerArgumentType.getInteger(command, "amount"));
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							return 0;})
							))));
	}
	
	private int createRandomReward(CommandSource source, int weight, ItemInput item, int amount) throws IOException, CommandSyntaxException {
		String itemID = item.getItem().getRegistryName().toString();
		ItemStack stack = item.createItemStack(amount, false);
		if (stack.hasTag()) {
			CompoundNBT nbtData = stack.getTag();
			RankUpRewardManager.createNewRandomReward(weight, new ItemPair(itemID, amount, nbtData));
		}
		else {
			RankUpRewardManager.createNewRandomReward(weight, new ItemPair(itemID, amount));
		}
		
		try {
			source.getPlayerOrException().sendMessage(new StringTextComponent(
					"Created a new random rollable reward accessible by players with weight: " + weight + ", containing: " + itemID + " : " + amount), source.getPlayerOrException().getUUID() );
		}
		catch (CommandSyntaxException e){
			return 1;
		}
		return 1;
	}
}
