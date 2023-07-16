package com.scproductions.battlepasspixelmon;

import com.scproductions.battlepasspixelmon.commands.ClaimRewardsCommand;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

@Mod.EventBusSubscriber(modid = Main.MOD_ID)
public class ForgeListeners {

	@SubscribeEvent //Place all commands to be registered in this block.
	public static void onCommandsRegister(RegisterCommandsEvent event) {
		//Add new command classes below
		new ClaimRewardsCommand(event.getDispatcher());
		
		
		ConfigCommand.register(event.getDispatcher());
	}
}
