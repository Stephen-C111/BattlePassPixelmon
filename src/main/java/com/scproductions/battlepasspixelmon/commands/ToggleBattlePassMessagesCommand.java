package com.scproductions.battlepasspixelmon.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.scproductions.battlepasspixelmon.BattlePassManager;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;

public class ToggleBattlePassMessagesCommand {
	public ToggleBattlePassMessagesCommand(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("togglebattlepassmessages").executes((command) -> {
			return toggleSpam(command.getSource());
		}));
	}
	
	private int toggleSpam(CommandSource source) throws CommandSyntaxException {
		ServerPlayerEntity player = source.getPlayerOrException();
		BattlePassManager.toggleChatMessages(player);
		return 1;
	}
}
