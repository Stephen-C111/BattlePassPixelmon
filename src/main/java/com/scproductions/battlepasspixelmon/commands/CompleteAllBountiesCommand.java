package com.scproductions.battlepasspixelmon.commands;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.scproductions.battlepasspixelmon.bounties.BountyManager;
import com.scproductions.battlepasspixelmon.bounties.BountyManager.Bounty;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class CompleteAllBountiesCommand {
	public CompleteAllBountiesCommand(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
				Commands.literal("completeallbounties")
				.requires((commandSource) -> {return commandSource.hasPermission(2);})
					.then(Commands.argument("players", EntityArgument.players())
						.then(Commands.literal("--irreversiblecompletion")
							.executes((command) -> 
							{
								Logger.getLogger("BattlePassPixelmon").info("Cheat Completing bounties...");
								try {
									return completeBounties(command.getSource(), EntityArgument.getPlayers(command, "players"));
							} 	catch (IOException e) {
								e.printStackTrace();
							}
							return 0;})
							)));
	}
	
	private int completeBounties(CommandSource source, Collection<ServerPlayerEntity> players) throws CommandSyntaxException, IOException {
		
		
		for(ServerPlayerEntity player : players) {
			source.getPlayerOrException().sendMessage(new StringTextComponent("Completed all bounties for " + player.getDisplayName().getString() + "."), source.getPlayerOrException().getUUID());
			for (Bounty bounty : BountyManager.getPlayerAcceptedBounties(player.getUUID())) {
				BountyManager.completeBounty(player, bounty.uuid);
			}
		}
		
		
		
		return 1;
	}
}
