package com.scproductions.battlepasspixelmon;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pixelmonmod.pixelmon.api.events.ApricornEvent;
import com.pixelmonmod.pixelmon.api.events.BeatWildPixelmonEvent;
import com.pixelmonmod.pixelmon.api.events.EggHatchEvent;
import com.pixelmonmod.pixelmon.api.events.EvolveEvent;
import com.pixelmonmod.pixelmon.api.events.FishingEvent;
import com.pixelmonmod.pixelmon.api.events.PokemonReceivedEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.controller.participants.WildPixelmonParticipant;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PXListeners {
	
	private static final Logger LOGGER = LogManager.getLogger("BattlePassPixelmon");
	
	boolean hatchSwitch = false;
	
	Random random = new Random();
	
	@SubscribeEvent //confirmed working
	public void onPokemonCatch(PokemonReceivedEvent event) {
		ServerPlayerEntity player =  event.getPlayer();
		if (event.getCause() == "PokeBall") {
			UUID uuid = player.getUUID();
			Pokemon poke = (Pokemon) event.getPokemon();
			//Check legend status, exclude ditto, meltan, melmetal
			if (poke.isLegendary() && poke.getSpecies().getDex() != 132 /*ditto*/ && poke.getSpecies().getDex() != 808 /*meltan*/ && poke.getSpecies().getDex() != 809 /*melmetal*/) {
				int baseValue = BattlePassConfig.pokemon_catch_legendary_base_reward.get();
				int realValue = baseValue;
				BattlePassManager.grantProgress(uuid, realValue, false);
				BattlePassManager.sendPlayerInfo(player, realValue, "Caught a Legendary Pokemon:");
			}
			else if (poke.isUltraBeast()) {
				int baseValue = BattlePassConfig.pokemon_catch_legendary_base_reward.get();
				int realValue = baseValue / 2;
				BattlePassManager.grantProgress(uuid, realValue, false);
				BattlePassManager.sendPlayerInfo(player, realValue, "Caught an Ultra Beast:");
			}
			else {
				int baseValue = BattlePassConfig.pokemon_catch_base_reward.get();
				int realValue = baseValue * (poke.getPokemonLevel() / 25 + 1);
				BattlePassManager.grantProgress(uuid, realValue, false);
				BattlePassManager.sendPlayerInfo(player, realValue, "Caught a Pokemon:");
			}
		}
	}
	
	@SubscribeEvent //confirmed working
	public void onPokemonDefeat(BeatWildPixelmonEvent event) {
		ServerPlayerEntity player = event.player;
		UUID uuid = player.getUUID();
		WildPixelmonParticipant wpp = event.wpp;
		Pokemon poke = wpp.getFaintedPokemon().pokemon;
		if (poke.isLegendary()) {
			int baseValue = BattlePassConfig.pokemon_defeat_legendary_base_reward.get();
			int realValue = baseValue;
			BattlePassManager.grantProgress(uuid, realValue, false);
			BattlePassManager.sendPlayerInfoToChat(player, realValue, "Defeated a Legendary Pokemon:");
		}
		else if (poke.isUltraBeast()) {
			int baseValue = BattlePassConfig.pokemon_defeat_legendary_base_reward.get();
			int realValue = baseValue / 2;
			BattlePassManager.grantProgress(uuid, realValue, false);
			BattlePassManager.sendPlayerInfoToChat(player, realValue, "Defeated an Ultra Beast:");
		}
		else {
			int baseValue = BattlePassConfig.pokemon_defeat_base_reward.get();
			int realValue = baseValue * (poke.getPokemonLevel() / 25 + 1);
			BattlePassManager.grantProgress(uuid, realValue, false);
			BattlePassManager.sendPlayerInfoToChat(player, realValue, "Defeated a Pokemon:");
		}
	}
	
	@SubscribeEvent //confirmed working
	public void onApricornPick(ApricornEvent.Pick event) {
		ServerPlayerEntity player = event.getPlayer();
		UUID uuid = player.getUUID();
		int baseValue = BattlePassConfig.apricorn_pick_base_reward.get();
		
		int chance = random.nextInt(101);
		if (chance == 100) {
			BattlePassManager.grantProgress(uuid, baseValue * 2, false);
			BattlePassManager.sendPlayerInfo(player, baseValue * 2, "Super Lucky Apricorn Pick:");
		}
		else if (chance > 92) {
			BattlePassManager.grantProgress(uuid, baseValue, false);
			BattlePassManager.sendPlayerInfo(player, baseValue, "Lucky Apricorn Pick:");
		}
	}
	
	@SubscribeEvent //confirmed working
	public void onEggHatch(EggHatchEvent.Pre event) {
		ServerPlayerEntity player = event.getPlayer();
		UUID uuid = player.getUUID();
		int baseValue = BattlePassConfig.egg_hatch_base_reward.get();
		Pokemon poke = event.getPokemon();
		
		if (hatchSwitch && BattlePassConfig.use_hatch_dupe_fix.get()) { //prevent double firing of hatch event for progress.
			hatchSwitch = false;
			return;
		}
		else {
			hatchSwitch = true;
		}
		
		int[] ivs = poke.getIVs().getArray();
		int perfectIVs = 1;
		for (int i = 0; i < 6; i++) {
			if (ivs[i] == 31) {
				perfectIVs++;
			}
		}
		int realValue = baseValue * perfectIVs;
		BattlePassManager.grantProgress(uuid, realValue, false);
		BattlePassManager.sendPlayerInfoToChat(player, realValue, perfectIVs - 1 + " IV Egg hatched:");
	}
	
	@SubscribeEvent //confirmed working
	public void onEvolve(EvolveEvent.Post event) {
		ServerPlayerEntity player = event.getPlayer();
		UUID uuid = player.getUUID();
		int baseValue = BattlePassConfig.pokemon_evolve_base_reward.get();
		Pokemon poke = event.getPokemon();
		if (poke.isLegendary(true)) {
			BattlePassManager.grantProgress(uuid, baseValue * 10, false);
			BattlePassManager.sendPlayerInfoToChat(player, baseValue, "Evolved a Legendary Pokemon:");
		}
		else {
			BattlePassManager.grantProgress(uuid, baseValue, false);
			BattlePassManager.sendPlayerInfoToChat(player, baseValue, "Evolved Pokemon:");
		}
		
	}
	
	@SubscribeEvent
	public void onReelIn(FishingEvent.Reel event) {
		LOGGER.info(event.fishHook);
		
		if (event.optEntity.isPresent()) {
			UUID uuid = event.player.getUUID();
			int baseValue = BattlePassConfig.pixelmon_fish_base_reward.get();
			BattlePassManager.grantProgress(uuid, baseValue, false);
			BattlePassManager.sendPlayerInfoToChat(event.player, baseValue, "Caught something with a rod:");
		}
	}
}
