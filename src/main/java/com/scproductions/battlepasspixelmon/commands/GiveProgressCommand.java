package com.scproductions.battlepasspixelmon.commands;

import java.util.Collection;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.scproductions.battlepasspixelmon.BattlePassManager;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;

public class GiveProgressCommand {
	public GiveProgressCommand(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
				Commands.literal("giveprogress")
				.requires((commandSource) -> {return commandSource.hasPermission(2);})
					.then(Commands.argument("players", EntityArgument.players())
						.then(Commands.argument("amount", IntegerArgumentType.integer(1))
							.executes((command) -> {return giveProgress(command.getSource(), EntityArgument.getPlayers(command, "players"), IntegerArgumentType.getInteger(command, "amount"));})
							)));
	}
	
	private int giveProgress(CommandSource source, Collection<ServerPlayerEntity> players, int amount) throws CommandSyntaxException {
		
		for(ServerPlayerEntity player : players) {
			BattlePassManager.grantProgress(player.getUUID(), amount, false);
			BattlePassManager.sendPlayerInfoToChat(player, amount, "Points given by an admin:");
		}
		
		
		return 1;
	}
}
