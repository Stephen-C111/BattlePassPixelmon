package com.scproductions.battlepasspixelmon.commands;

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
		Bounty bounty = BountyManager.getPlayerAcceptedBounties(player.getUUID()).get(number);
		BountyManager.discardBounty(player.getUUID(), bounty.uuid);
		player.sendMessage(new StringTextComponent("Discarded the following bounty: " + bounty.getFormattedString() + " You can pick it back up if it's still active, with reset progress."), player.getUUID());
		BountyGUIHandler.bgui.updateBountyJournal(player, false);
		
		return 1;
	}
}
