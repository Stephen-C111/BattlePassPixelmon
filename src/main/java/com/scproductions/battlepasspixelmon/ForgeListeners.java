package com.scproductions.battlepasspixelmon;

import com.scproductions.battlepasspixelmon.bounties.BountyManager;
import com.scproductions.battlepasspixelmon.bounties.BountyManager.Bounty;
import com.scproductions.battlepasspixelmon.bounties.BountyManager.Bounty.ObjectiveTag;
import com.scproductions.battlepasspixelmon.commands.AcceptBountyCommand;
import com.scproductions.battlepasspixelmon.commands.CheckRankCommand;
import com.scproductions.battlepasspixelmon.commands.ClaimRewardPacksCommand;
import com.scproductions.battlepasspixelmon.commands.ClaimRewardsCommand;
import com.scproductions.battlepasspixelmon.commands.DiscardBountyCommand;
import com.scproductions.battlepasspixelmon.commands.GetJournalCommand;
import com.scproductions.battlepasspixelmon.commands.GiveProgressCommand;
import com.scproductions.battlepasspixelmon.commands.GiveSelfProgressCommand;
import com.scproductions.battlepasspixelmon.commands.NewRandomRewardCommand;
import com.scproductions.battlepasspixelmon.commands.NewRewardPackCommand;
import com.scproductions.battlepasspixelmon.commands.ReloadRewardPacksCommand;
import com.scproductions.battlepasspixelmon.commands.ResetRankCommand;
import com.scproductions.battlepasspixelmon.commands.ToggleBattlePassBossBarCommand;
import com.scproductions.battlepasspixelmon.commands.ToggleBattlePassMessagesCommand;
import com.scproductions.battlepasspixelmon.commands.ViewAcceptedBountiesCommand;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.brewing.PlayerBrewedPotionEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.world.BlockEvent;
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
		new ToggleBattlePassBossBarCommand(event.getDispatcher());
		new ClaimRewardPacksCommand(event.getDispatcher());
		new ResetRankCommand(event.getDispatcher());
		new NewRewardPackCommand(event.getDispatcher());
		new ReloadRewardPacksCommand(event.getDispatcher());
		new NewRandomRewardCommand(event.getDispatcher());
		new GetJournalCommand(event.getDispatcher());
		new AcceptBountyCommand(event.getDispatcher());
		new ViewAcceptedBountiesCommand(event.getDispatcher());
		new DiscardBountyCommand(event.getDispatcher());
		
		ConfigCommand.register(event.getDispatcher());
	}
	
	@SubscribeEvent
	public static void onAddReloadListeners(AddReloadListenerEvent event)
	{
		
	}
	
	@SubscribeEvent
	public static void onVanillaFish(ItemFishedEvent event) {
		//BattlePassManager listening in
		ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
		int baseValue = BattlePassConfig.vanilla_fish_base_reward.get();
		BattlePassManager.grantProgress(player.getUUID(), baseValue, false);
		BattlePassManager.sendPlayerInfo(player, baseValue, "Caught something with a normal rod:");
		
		//BountyManager listening in
		for (Bounty bounty : BountyManager.getPlayerAcceptedBounties(player.getUUID())) {
			if (bounty.tags.contains(ObjectiveTag.FISH)){
				BountyManager.grantProgress(player, bounty.uuid, 1);
			}
		}
	}
	
	@SubscribeEvent
	public static void onPlayerJoin(EntityJoinWorldEvent event) {
		//Check to see if joined player has a battlepass or not.
		if (event.getEntity() instanceof ServerPlayerEntity) {
			if (BattlePassManager.getRank(event.getEntity().getUUID()) == -999) {
				BattlePassManager.putData(event.getEntity().getUUID(), 0, 0, 0);
			}
			//BountyGUIHandler.bgui.openBountyBoard((ServerPlayerEntity) event.getEntity());
		}
	}
	
	@SubscribeEvent
	public static void onPlayerHarvest(BlockEvent.BreakEvent event) {
		PlayerEntity player = event.getPlayer();
		int xp = event.getExpToDrop();
		if (xp > 0) {
			BattlePassManager.grantProgress(player.getUUID(), 1, false);
			BattlePassManager.sendPlayerInfo((ServerPlayerEntity) player, 1, "Mined a valuable block:");
		}
		
	}
	
}
