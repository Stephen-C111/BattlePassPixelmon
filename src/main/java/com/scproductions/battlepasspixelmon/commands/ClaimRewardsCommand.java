package com.scproductions.battlepasspixelmon.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.scproductions.battlepasspixelmon.BattlePassManager;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;

public class ClaimRewardsCommand {

	public ClaimRewardsCommand(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("claimranks").then(Commands.literal("all").executes((command) -> {
			return claimRewards(command.getSource());
		})));
	}
	
	private int claimRewards(CommandSource source) throws CommandSyntaxException {
		ServerPlayerEntity player = source.getPlayerOrException();
		BattlePassManager.claimRewards(player);
		return 1;
	}
}
