package com.scproductions.battlepasspixelmon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.CustomServerBossInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.BossInfo.Color;
import net.minecraft.world.BossInfo.Overlay;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class BossBarHandler {
	private Map<UUID, CustomServerBossInfo> DATA = new HashMap<UUID, CustomServerBossInfo>();
	public static BossBarHandler bpm = new BossBarHandler();
	
	public String getBarString(UUID uuid) {
		int claimableRanks = (BattlePassManager.getRank(uuid) - BattlePassManager.getClaimedRanks(uuid));
		String claimable = "";
		if (claimableRanks > 0) {
			claimable = " | " + claimableRanks + " available rewards!";
		}
		return BattlePassConfig.boss_bar_default_title.get() + "Rank " + 
				BattlePassManager.getRank(uuid) + " | " + BattlePassManager.getRankProgress(uuid) + "/" + BattlePassConfig.required_progress_to_rank.get() + claimable;
	}
	
	public void createNewBossBar(ServerPlayerEntity player) {
		UUID uuid = player.getUUID();
		ServerWorld world = ServerLifecycleHooks.getCurrentServer().overworld();
		CustomServerBossInfo info = new CustomServerBossInfo(new ResourceLocation("battlepasspixelmon_" + uuid.toString()), new StringTextComponent(getBarString(uuid)));
		DATA.put(uuid, info);
		updateBossBar(player.getUUID());
	}
	
	public void updateBossBar(UUID uuid) {
		ServerWorld world = ServerLifecycleHooks.getCurrentServer().overworld();
		ServerPlayerEntity player = (ServerPlayerEntity) world.getEntity(uuid);
		if (!DATA.containsKey(uuid)) {
			createNewBossBar(player);
			return;
		}
		CustomServerBossInfo info = DATA.get(uuid);
		info.setName(new StringTextComponent(getBarString(uuid)));
		info.setValue(BattlePassManager.getRankProgress(uuid));
		info.setMax(BattlePassConfig.required_progress_to_rank.get());
		info.setColor(Color.GREEN);
		info.setOverlay(Overlay.NOTCHED_20);
		List<ServerPlayerEntity> players = new ArrayList<ServerPlayerEntity>();
		players.add(player);
		info.setPlayers(players);
		info.setVisible(BattlePassManager.getAllowBossBar(uuid));
	}
	
}
