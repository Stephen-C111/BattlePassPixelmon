package com.scproductions.battlepasspixelmon.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.scproductions.battlepasspixelmon.RewardPackManager;
import com.scproductions.battlepasspixelmon.RewardPackManager.RewardPack.ItemPair;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.command.arguments.ItemInput;
import net.minecraft.util.text.StringTextComponent;

public class NewRewardPackCommand {
	public NewRewardPackCommand(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
				Commands.literal("newrewardpack")
				.requires((commandSource) -> {return commandSource.hasPermission(2);})
					.then(Commands.argument("rank", IntegerArgumentType.integer(0))
						.then(Commands.argument("item", ItemArgument.item()))
							.then(Commands.argument("amount", IntegerArgumentType.integer(1))
							.executes((command) -> 
							{
								createRewardPack(command.getSource(), IntegerArgumentType.getInteger(command, "rank"), ItemArgument.getItem(command, "item"), IntegerArgumentType.getInteger(command, "amount"));
							return 0;})
							)));
	}
	
	private int createRewardPack(CommandSource source, int rank, ItemInput item, int amount) {
		String itemID = item.getItem().getRegistryName().toString();
		RewardPackManager.rpm.createNewRewardPack(rank, new ItemPair[] { new ItemPair(itemID, amount) });
		try {
			source.getPlayerOrException().sendMessage(new StringTextComponent(
					"Created a new rewardpack accessible by players with rank: " + rank + ", containing: " + itemID + " : " + amount), source.getPlayerOrException().getUUID() );
		}
		catch (CommandSyntaxException e){
			return 1;
		}
		return 1;
	}
}
