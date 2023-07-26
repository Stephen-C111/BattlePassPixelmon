package com.scproductions.battlepasspixelmon.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.scproductions.battlepasspixelmon.BattlePassManager;
import com.scproductions.battlepasspixelmon.RewardPackManager;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;

public class ClaimRewardPacksCommand {
	public ClaimRewardPacksCommand(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("claimrewardpacks").executes((command) -> {
			return checkRank(command.getSource());
		}));
	}
	
	private int checkRank(CommandSource source) throws CommandSyntaxException {
		ServerPlayerEntity player = source.getPlayerOrException();
		BattlePassManager.sendPlayerInfoToChat(player, 0, "Checking for any eligible reward packs...");;
		RewardPackManager.rpm.claimRewardPacks(player);
		return 1;
	}
}
