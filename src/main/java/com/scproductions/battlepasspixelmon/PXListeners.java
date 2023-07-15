package com.scproductions.battlepasspixelmon;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pixelmonmod.pixelmon.api.events.PokemonReceivedEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
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
			player.sendMessage(new StringTextComponent(""), uuid);
			
		}
	}
}
