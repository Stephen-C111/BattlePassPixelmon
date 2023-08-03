package com.scproductions.battlepasspixelmon.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.scproductions.battlepasspixelmon.bounties.BountyManager;
import com.scproductions.battlepasspixelmon.bounties.gui.BountyGUIHandler;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;

public class AcceptBountyCommand {
	public AcceptBountyCommand(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
				Commands.literal("acceptbounty")
						.then(Commands.argument("number", IntegerArgumentType.integer(0))
								.executes((command) -> {return acceptBounty(command.getSource(), IntegerArgumentType.getInteger(command, "number"));})));
	}
	
	private int acceptBounty(CommandSource source, int number) throws CommandSyntaxException {
		
		ServerPlayerEntity player = source.getPlayerOrException();
		BountyManager.acceptBounty(player.getUUID(), BountyManager.getActiveBounties().get(number).uuid);
		BountyGUIHandler.bgui.updateBountyJournal(player, false);
		return 1;
	}
}
