package com.scproductions.battlepasspixelmon.commands;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.UUID;
import java.util.logging.Logger;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.scproductions.battlepasspixelmon.BattlePassManager;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLPaths;

public class ResetRankCommand {
	public ResetRankCommand(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
				Commands.literal("resetrank")
				.requires((commandSource) -> {return commandSource.hasPermission(2);})
					.then(Commands.argument("players", EntityArgument.players())
						.then(Commands.literal("--iamawareiwilldeletethisplayersprogress")
							.then(Commands.literal("--iamverysureiwanttoperformthisaction")
							.executes((command) -> 
							{
								Logger.getLogger("BattlePassPixelmon").info("Attempting to delete...");
								try {
									return deleteProgress(command.getSource(), EntityArgument.getPlayers(command, "players"));
							} 	catch (IOException e) {
								e.printStackTrace();
							}
							return 0;})
							))));
	}
	
	private int deleteProgress(CommandSource source, Collection<ServerPlayerEntity> players) throws CommandSyntaxException, IOException {
		
		for(ServerPlayerEntity player : players) {
			Path path = FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath());
	        Logger.getLogger("BattlePassPixelmon").info(path.getFileName().toString() + "\\DELETED_DATA_" + player.getName().getString() + "_" + UUID.randomUUID() + ".txt");
			File f = new File(path.getFileName().toString() + "\\DELETED_DATA_" + player.getName().getString() + "_" + UUID.randomUUID() + ".txt");
			FileWriter fw = new FileWriter(f);
			String s = "PLAYER UUID: " + player.getUUID() + "| RANK: " + BattlePassManager.getRank(player.getUUID()) + "| PROGRESS: " + BattlePassManager.getRankProgress(player.getUUID()) + "| CLAIMED RANKS: " + BattlePassManager.getClaimedRanks(player.getUUID());
			s += "\nClaimed UUIDS:\n";
			for (UUID uuid : BattlePassManager.getClaimedUUIDS(player.getUUID())) {
				s += uuid + "\n";
			}
			fw.append(s);
			fw.close();
			BattlePassManager.updateClaimedPacks(player, new UUID[] {});
			BattlePassManager.putData(player.getUUID(), 0, 0, 0);
		}
		try {
			source.getPlayerOrException().sendMessage(new StringTextComponent(
					"Reset passes for: " + players.toString() + ". Backup data can be located in \\DefaultConfigs. "), source.getPlayerOrException().getUUID() );
		}
		catch (CommandSyntaxException e){
			return 1;
		}
		
		
		return 1;
	}
}
