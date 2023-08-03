package com.scproductions.battlepasspixelmon.commands;

import java.util.List;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.scproductions.battlepasspixelmon.bounties.BountyManager;
import com.scproductions.battlepasspixelmon.bounties.BountyManager.Bounty;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class ViewAcceptedBountiesCommand {
	public ViewAcceptedBountiesCommand(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("acceptedbounties").executes((command) -> {
			return viewAcceptedBounties(command.getSource());
		}));
	}
	
	private int viewAcceptedBounties(CommandSource source) throws CommandSyntaxException {
		ServerPlayerEntity player = source.getPlayerOrException();
		List<Bounty> bounties = BountyManager.getPlayerAcceptedBounties(player.getUUID());
		int i = 0;
		for (Bounty bounty : bounties) {
			player.sendMessage(new StringTextComponent(i++ + ": " + bounty.getFormattedString()), player.getUUID());
		}

		return 1;
	}
}
