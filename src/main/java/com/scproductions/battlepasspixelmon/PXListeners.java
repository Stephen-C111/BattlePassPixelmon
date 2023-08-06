package com.scproductions.battlepasspixelmon;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pixelmonmod.pixelmon.api.events.ApricornEvent;
import com.pixelmonmod.pixelmon.api.events.BeatTrainerEvent;
import com.pixelmonmod.pixelmon.api.events.BeatWildPixelmonEvent;
import com.pixelmonmod.pixelmon.api.events.EggHatchEvent;
import com.pixelmonmod.pixelmon.api.events.EvolveEvent;
import com.pixelmonmod.pixelmon.api.events.FishingEvent;
import com.pixelmonmod.pixelmon.api.events.PokemonReceivedEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Element;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.boss.BossTier;
import com.pixelmonmod.pixelmon.api.pokemon.boss.BossTiers;
import com.pixelmonmod.pixelmon.battles.controller.participants.WildPixelmonParticipant;
import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.scproductions.battlepasspixelmon.bounties.BountyManager;
import com.scproductions.battlepasspixelmon.bounties.BountyManager.Bounty;
import com.scproductions.battlepasspixelmon.bounties.BountyManager.Bounty.ObjectiveTag;

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
				//BountyManager listening in
				for (Bounty bounty : BountyManager.getPlayerAcceptedBounties(uuid)) {
					if (bounty.tags.contains(ObjectiveTag.LEGEND)){
						BountyManager.grantProgress(event.getPlayer(), bounty.uuid, 1);
					}
				}
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
			//BountyManager listening in
			for (Bounty bounty : BountyManager.getPlayerAcceptedBounties(uuid)) {
				if (bounty.tags.contains(ObjectiveTag.CATCH)){
					if (bounty.elements.size() <= 0) {
						BountyManager.grantProgress(event.getPlayer(), bounty.uuid, 1);
					}
					else {
						for (Element element : poke.getForm().getTypes()) {
							if (bounty.elements.contains(element)) {
								BountyManager.grantProgress(event.getPlayer(), bounty.uuid, 1);
								break;
							}
						}
						
					}
					
				}
			}
		}
	}
	
	@SubscribeEvent //confirmed working
	public void onPokemonDefeat(BeatWildPixelmonEvent event) {
		ServerPlayerEntity player = event.player;
		UUID uuid = player.getUUID();
		WildPixelmonParticipant wpp = event.wpp;
		Pokemon poke = wpp.getFaintedPokemon().pokemon;
		Optional<PixelmonEntity> entity = poke.getPixelmonEntity();
		//LOGGER.info(entity.get().isBossPokemon() + " | " + entity.get().getBossTier().getID());
		double realValueMultiplier = 1;
		String bossString = "";
		BossTier tier = null;
		if (entity.get().isBossPokemon()) { //As far as I can tell, getting the boss tier of a single pokemon is not possible.
			
			//BountyManager listening in
			for (Bounty bounty : BountyManager.getPlayerAcceptedBounties(uuid)) {
				if (bounty.tags.contains(ObjectiveTag.BOSS)){
					BountyManager.grantProgress(event.player, bounty.uuid, 1);
				}
			}
			
			tier = entity.get().getBossTier();
			bossString += tier.getID() + " ";
			switch (entity.get().getBossTier().getID()) {
				case "common": realValueMultiplier = 20;
				break;
				case "uncommon": realValueMultiplier = 30;
				break;
				case "rare": realValueMultiplier = 50;
				break;
				case "epic": realValueMultiplier = 70;
				break;
				case "legendary": realValueMultiplier = 90;
				break;
				case "ultimate": realValueMultiplier = 150;
				break;
				case "drowned": realValueMultiplier = 20;
				break;
				case "equal": realValueMultiplier = 20;
				break;
				case "haunted": realValueMultiplier = 20;
				break;
				default: realValueMultiplier = 1;
				break;
				
				
			}
		}
		if (poke.isLegendary()) {
			int baseValue = BattlePassConfig.pokemon_defeat_legendary_base_reward.get();
			int realValue = (int) (baseValue * realValueMultiplier);
			BattlePassManager.grantProgress(uuid, realValue, false);
			BattlePassManager.sendPlayerInfoToChat(player, realValue, "Defeated a " + bossString + "Legendary Pokemon:");
			//BountyManager listening in
			for (Bounty bounty : BountyManager.getPlayerAcceptedBounties(uuid)) {
				if (bounty.tags.contains(ObjectiveTag.LEGEND)){
					BountyManager.grantProgress(event.player, bounty.uuid, 1);
				}
			}
		}
		else if (poke.isUltraBeast()) {
			int baseValue = BattlePassConfig.pokemon_defeat_legendary_base_reward.get();
			int realValue = (int) ((baseValue / 2) * realValueMultiplier);
			BattlePassManager.grantProgress(uuid, realValue, false);
			BattlePassManager.sendPlayerInfoToChat(player, realValue, "Defeated an " + bossString + "Ultra Beast:");
		}
		else {
			int baseValue = BattlePassConfig.pokemon_defeat_base_reward.get();
			int realValue = 0;
			if (tier == null) {
				realValue = (int) ((baseValue * (poke.getPokemonLevel() / 25 + 1)));
			}
			else {
				realValue = (int) (baseValue * realValueMultiplier);
			}
			
			BattlePassManager.grantProgress(uuid, realValue, false);
			BattlePassManager.sendPlayerInfoToChat(player, realValue, "Defeated a " + bossString + "Pokemon:");
		}
		//BountyManager listening in
		for (Bounty bounty : BountyManager.getPlayerAcceptedBounties(uuid)) {
			if (bounty.tags.contains(ObjectiveTag.KILL)){
				if (bounty.elements.size() <= 0) {
					BountyManager.grantProgress(player, bounty.uuid, 1);
				}
				else {
					for (Element element : poke.getForm().getTypes()) {
						if (bounty.elements.contains(element)) {
							BountyManager.grantProgress(player, bounty.uuid, 1);
							break;
						}
					}
					
				}
				
			}
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
		if (event.optEntity.isPresent()) {
			//BattlePassManager listening in
			UUID uuid = event.player.getUUID();
			int baseValue = BattlePassConfig.pixelmon_fish_base_reward.get();
			BattlePassManager.grantProgress(uuid, baseValue, false);
			BattlePassManager.sendPlayerInfoToChat(event.player, baseValue, "Caught something with a rod:");
			
			//BountyManager listening in
			for (Bounty bounty : BountyManager.getPlayerAcceptedBounties(uuid)) {
				if (bounty.tags.contains(ObjectiveTag.FISH)){
					BountyManager.grantProgress(event.player, bounty.uuid, 1);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onTrainerDefeat(BeatTrainerEvent event) {
		UUID uuid = event.player.getUUID();
		NPCTrainer trainer = event.trainer;
		int perPokeBase = BattlePassConfig.pokemon_defeat_base_reward.get() * 2;
		int baseValue = BattlePassConfig.trainer_defeat_base_reward.get();
		int realValue = baseValue;
		String extraInfo = "";
		
		int level = trainer.getTrainerLevel();
		int numPokes = trainer.getPokemonStorage().countAll();
		
		if (trainer.getBossTier().isBoss()) {
			//LOGGER.info(trainer.getBossTier().getID() + "|" + trainer.getBossTier().toString() + "|" + trainer.getBossTier());
			extraInfo += trainer.getBossTier().getID() + " ";
			realValue *= 1 + 10;
			switch (trainer.getBossTier().getID()) {
				case BossTiers.COMMON: realValue += perPokeBase * 3 * numPokes;
				break;
				case BossTiers.UNCOMMON: realValue += perPokeBase * 6 * numPokes;
				break;
				case BossTiers.RARE: realValue += perPokeBase * 10 * numPokes;
				break;
				case BossTiers.EPIC: realValue += perPokeBase * 15 * numPokes;
				break;
				case BossTiers.LEGENDARY: realValue += perPokeBase * 30 * numPokes;
				break;
				case BossTiers.ULTIMATE: realValue += perPokeBase * 60 * numPokes;
				break;
				case BossTiers.DROWNED: realValue += perPokeBase * 10 * numPokes;
				break;
				case BossTiers.EQUAL: realValue += perPokeBase * 3 * numPokes;
				break;
				case BossTiers.HAUNTED: realValue += perPokeBase * 10 * numPokes;
				break;
				default: break;
			}
		}
		else {
			realValue *= (1 + (level / 75)) * numPokes;
			extraInfo += "Level " + level;
		}
		
		BattlePassManager.grantProgress(uuid, realValue, false);
		BattlePassManager.sendPlayerInfoToChat(event.player, realValue, "Beat a " + extraInfo + " trainer:");
		
		//BountyManager listening in
		for (Bounty bounty : BountyManager.getPlayerAcceptedBounties(uuid)) {
			if (bounty.tags.contains(ObjectiveTag.TRAINER)){
				BountyManager.grantProgress(event.player, bounty.uuid, 1);
			}
		}
	}
}
