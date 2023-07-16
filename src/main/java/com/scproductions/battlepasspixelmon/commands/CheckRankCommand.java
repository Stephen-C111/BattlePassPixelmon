package com.scproductions.battlepasspixelmon.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.scproductions.battlepasspixelmon.BattlePassManager;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;

public class CheckRankCommand {
	public CheckRankCommand(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("checkrank").executes((command) -> {
			return checkRank(command.getSource());
		}));
	}
	
	private int checkRank(CommandSource source) throws CommandSyntaxException {
		ServerPlayerEntity player = source.getPlayerOrException();
		int unclaimedRanks = BattlePassManager.getRank(player.getUUID()) - BattlePassManager.getClaimedRanks(player.getUUID());
		BattlePassManager.sendPlayerInfoToChat(player, 0, "You have " + unclaimedRanks + " unclaimed ranks.");;
		return 1;
	}
}
