package com.scproductions.battlepasspixelmon.commands;

import java.io.IOException;
import java.nio.file.Path;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.scproductions.battlepasspixelmon.RewardPackManager;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLPaths;

public class ReloadRewardPacksCommand {
	public ReloadRewardPacksCommand(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
				Commands.literal("reloadrewardpacks")
				.requires((commandSource) -> {return commandSource.hasPermission(2);})
							.executes((command) -> {return reloadPacks(command.getSource());}));
	}
	
	private int reloadPacks(CommandSource source) throws CommandSyntaxException {
		
		Path path = FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath());
        try {
			RewardPackManager.rpm.loadPacksJson();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return 1;
	}
}
