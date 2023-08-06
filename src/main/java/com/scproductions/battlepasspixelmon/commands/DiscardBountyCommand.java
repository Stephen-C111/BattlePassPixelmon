package com.scproductions.battlepasspixelmon.commands;

import java.util.List;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.scproductions.battlepasspixelmon.bounties.BountyManager;
import com.scproductions.battlepasspixelmon.bounties.BountyManager.Bounty;
import com.scproductions.battlepasspixelmon.bounties.gui.BountyGUIHandler;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class DiscardBountyCommand {
	public DiscardBountyCommand(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
				Commands.literal("discardbounty")
						.then(Commands.argument("number", IntegerArgumentType.integer(0))
							.then(Commands.literal("--taskprogresswillbereset")
								.executes((command) -> {return discardBounty(command.getSource(), IntegerArgumentType.getInteger(command, "number"));}))));
	}
	
	private int discardBounty(CommandSource source, int number) throws CommandSyntaxException {
		
		ServerPlayerEntity player = source.getPlayerOrException();
		List<Bounty> bounties = BountyManager.getPlayerAcceptedBounties(player.getUUID());
		if (bounties.size() <= 0) {
			player.sendMessage(new StringTextComponent("You have no bounties to discard."), player.getUUID());
			return -1;
		}
		if (number >= bounties.size()) {
			player.sendMessage(new StringTextComponent("Invalid number received. Remember to use the number by the bounty in /acceptedbounties."), player.getUUID());
		}
		Bounty bounty = bounties.get(number);
		BountyManager.discardBounty(player.getUUID(), bounty.uuid);
		player.sendMessage(new StringTextComponent("Discarded the following bounty: " + bounty.getFormattedString() + " You can pick it back up if it's still active, with reset progress."), player.getUUID());
		BountyGUIHandler.bgui.updateBountyJournal(player, false);
		
		return 1;
	}
}
