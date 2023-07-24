package com.scproductions.battlepasspixelmon;

import com.scproductions.battlepasspixelmon.commands.CheckRankCommand;
import com.scproductions.battlepasspixelmon.commands.ClaimRewardsCommand;
import com.scproductions.battlepasspixelmon.commands.GiveProgressCommand;
import com.scproductions.battlepasspixelmon.commands.GiveSelfProgressCommand;
import com.scproductions.battlepasspixelmon.commands.ToggleBattlePassMessagesCommand;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

@Mod.EventBusSubscriber(modid = Main.MOD_ID)
public class ForgeListeners {

	@SubscribeEvent //Place all commands to be registered in this block.
	public static void onCommandsRegister(RegisterCommandsEvent event) {
		//Add new command classes below
		new ClaimRewardsCommand(event.getDispatcher());
		new CheckRankCommand(event.getDispatcher());
		new GiveProgressCommand(event.getDispatcher());
		new GiveSelfProgressCommand(event.getDispatcher());
		new ToggleBattlePassMessagesCommand(event.getDispatcher());
		
		ConfigCommand.register(event.getDispatcher());
	}
	
	@SubscribeEvent
	public static void onVanillaFish(ItemFishedEvent event) {
		ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
		int baseValue = BattlePassConfig.vanilla_fish_base_reward.get();
		BattlePassManager.grantProgress(player.getUUID(), baseValue, false);
		BattlePassManager.sendPlayerInfo(player, baseValue, "Caught something with a normal rod:");
	}
}
