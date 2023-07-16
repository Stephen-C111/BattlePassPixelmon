package com.scproductions.battlepasspixelmon;

import net.minecraftforge.common.ForgeConfigSpec;

public class BattlePassConfig {
	
	//Config Variables
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    
    public static final ForgeConfigSpec.ConfigValue<Integer> pokemon_catch_base_reward;
    public static final ForgeConfigSpec.ConfigValue<Integer> pokemon_defeat_base_reward;
    public static final ForgeConfigSpec.ConfigValue<Integer> pokemon_catch_legendary_base_reward;
    public static final ForgeConfigSpec.ConfigValue<Integer> pokemon_defeat_legendary_base_reward;
    
    static {
    	BUILDER.push("Config for BattlePassPixelmon");
    	
    	pokemon_catch_base_reward = BUILDER.comment("The base payout for catching a pokemon before calculating pokemon level.").define("PokemonCatchBaseReward", 2);
    	pokemon_catch_legendary_base_reward = BUILDER.comment("The base payout for catching a legendary pokemon.").define("PokemonDefeatBaseReward", 200);
    	pokemon_defeat_base_reward = BUILDER.comment("The base payout for defeating a pokemon before calculating pokemon level.").define("PokemonCatchLegendaryBaseReward", 1);
    	pokemon_defeat_legendary_base_reward = BUILDER.comment("The base payout for defeating a legendary pokemon").define("PokemonDefeatLegendaryBaseReward", 50);
    	
    	BUILDER.pop();
    	SPEC = BUILDER.build();
    }
    
}
