package com.scproductions.battlepasspixelmon.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.scproductions.battlepasspixelmon.bounties.gui.BountyGUIHandler;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;

public class GetJournalCommand {
	public GetJournalCommand(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("getjournal").executes((command) -> {
			return getJournal(command.getSource());
		}));
	}
	
	private int getJournal(CommandSource source) throws CommandSyntaxException {
		ServerPlayerEntity player = source.getPlayerOrException();
		BountyGUIHandler.bgui.updateBountyJournal(player, true);
		return 1;
	}
}
