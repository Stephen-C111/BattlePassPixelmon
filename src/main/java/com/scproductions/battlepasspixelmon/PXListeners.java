package com.scproductions.battlepasspixelmon;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pixelmonmod.pixelmon.api.events.PokemonReceivedEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PXListeners {
	
	private static final Logger LOGGER = LogManager.getLogger("BattlePassPixelmon");
	
	@SubscribeEvent
	public void onPokemonCatch(PokemonReceivedEvent event) {
		
		
		
		LOGGER.info("Pokemon Caught!!! " + event.getCause());
		ServerPlayerEntity player =  event.getPlayer();
		if (event.getCause() == "PokeBall") {
			UUID uuid = player.getUUID();
			Pokemon poke = (Pokemon) event.getPokemon();
			//Check legend status, exclude ditto, meltan, melmetal
			if (poke.isLegendary() && poke.getSpecies().getDex() != 132 /*ditto*/ && poke.getSpecies().getDex() != 808 /*meltan*/ && poke.getSpecies().getDex() != 809 /*melmetal*/) {
				int baseValue = BattlePassConfig.pokemon_catch_legendary_base_reward.get();
				int realValue = baseValue;
				BattlePassManager.grantProgress(uuid, realValue, false);
				BattlePassManager.sendPlayerInfo(player, realValue);
			}
			else {
				int baseValue = BattlePassConfig.pokemon_catch_base_reward.get();
				int realValue = baseValue * (poke.getPokemonLevel() / 25 + 1);
				BattlePassManager.grantProgress(uuid, realValue, false);
				BattlePassManager.sendPlayerInfo(player, realValue);
			}
		}
	}
}
