package com.scproductions.battlepasspixelmon.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.scproductions.battlepasspixelmon.BattlePassManager;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;

public class GiveSelfProgressCommand {
	public GiveSelfProgressCommand(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
				Commands.literal("giveselfprogress")
				.requires((commandSource) -> {return commandSource.hasPermission(2);})
						.then(Commands.argument("amount", IntegerArgumentType.integer(1))
								.executes((command) -> {return giveProgress(command.getSource(), IntegerArgumentType.getInteger(command, "amount"));})));
	}
	
	private int giveProgress(CommandSource source, int amount) throws CommandSyntaxException {
		
		ServerPlayerEntity player = source.getPlayerOrException();
		BattlePassManager.grantProgress(player.getUUID(), amount, false);
		BattlePassManager.sendPlayerInfoToChat(player, amount, "Points given by an admin:");
		
		
		
		return 1;
	}
}
