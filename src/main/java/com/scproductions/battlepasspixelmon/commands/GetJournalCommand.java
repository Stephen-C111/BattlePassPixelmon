package com.scproductions.battlepasspixelmon.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.scproductions.battlepasspixelmon.bounties.gui.BountyGUIHandler;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class GetJournalCommand {
	public GetJournalCommand(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("getjournal").executes((command) -> {
			return getJournal(command.getSource());
		}));
	}
	
	private int getJournal(CommandSource source) throws CommandSyntaxException {
		ServerPlayerEntity player = source.getPlayerOrException();
		BountyGUIHandler.bgui.updateBountyJournal(player, true);
		player.sendMessage(new StringTextComponent("You have received a Bounty Journal. Right click it and left click any of the bounties within to start working on one. "
				+ "It should refresh automatically ONLY in your inventory, but if it doesn't, simply re-run this command. This item can be safely discarded."), player.getUUID());
		return 1;
	}
}
