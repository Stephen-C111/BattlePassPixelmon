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
    public static final ForgeConfigSpec.ConfigValue<Integer> trainer_defeat_base_reward;
    public static final ForgeConfigSpec.ConfigValue<Integer> pokemon_evolve_base_reward;
    public static final ForgeConfigSpec.ConfigValue<Integer> apricorn_pick_base_reward;
    public static final ForgeConfigSpec.ConfigValue<Integer> egg_hatch_base_reward;
    public static final ForgeConfigSpec.ConfigValue<Integer> vanilla_fish_base_reward;
    public static final ForgeConfigSpec.ConfigValue<Integer> pixelmon_fish_base_reward;
    
    
    public static final ForgeConfigSpec.ConfigValue<Integer> required_progress_to_rank;
    public static final ForgeConfigSpec.ConfigValue<Integer> max_rank;
    
    public static final ForgeConfigSpec.ConfigValue<Boolean> use_hatch_dupe_fix;
    
    public static final ForgeConfigSpec.ConfigValue<Boolean> use_boss_bar_by_default;
    public static final ForgeConfigSpec.ConfigValue<String> boss_bar_default_title;
    
    static {
    	BUILDER.push("Config for BattlePassPixelmon");
    	
    	pokemon_catch_base_reward = BUILDER.comment("The base payout for catching a pokemon before calculating pokemon level.").define("PokemonCatchBaseReward", 2);
    	pokemon_catch_legendary_base_reward = BUILDER.comment("The base payout for catching a legendary pokemon.").define("PokemonCatchLegendaryBaseReward", 200);
    	pokemon_defeat_base_reward = BUILDER.comment("The base payout for defeating a pokemon before calculating pokemon level.").define("PokemonDefeatBaseReward", 1);
    	pokemon_defeat_legendary_base_reward = BUILDER.comment("The base payout for defeating a legendary pokemon").define("PokemonDefeatLegendaryBaseReward", 50);
    	trainer_defeat_base_reward = BUILDER.comment("The base payout for defeating a trainer. Scales with trainer level and boss type.").define("TrainerDefeatBaseReward", 4);
    	pokemon_evolve_base_reward = BUILDER.comment("The base payout for evolving a pokemon.").define("PokemonEvolveBaseReward", 10);
    	apricorn_pick_base_reward = BUILDER.comment("There's a 7% chance to get this reward, and a 1% chance to get double this reward.").define("ApricornPickBaseReward", 1);
    	egg_hatch_base_reward = BUILDER.comment("The base reward is multiplied by the number of perfect (31) ivs on the hatched pokemon + 1. 6IV with base of 10 = 70 points.").define("EggHatchBaseReward", 10);
    	vanilla_fish_base_reward = BUILDER.comment("The base reward for vanilla fishing. There are no pokemon to defeat meaning this should be higher than pixelmon fishing.").define("VanillaFishBaseReward", 2);
    	pixelmon_fish_base_reward = BUILDER.comment("The base reward for pixelmon fishing. Defeating pokemon naturally makes pixelmon fishing more lucrative.").define("PixelmonFishBaseReward", 1);
    	
    	required_progress_to_rank = BUILDER.comment("The required amount of progress to rank up.").define("ProgressNeededForRankUp", 1000);
    	max_rank = BUILDER.comment("The maximum rank a player can achieve.").define("MaxRank", 999999);
    	
    	use_hatch_dupe_fix = BUILDER.comment("Use the boolean switch method to prevent double firing of the hatch event. Change to false if hatching grants no progress.")
    			.define("UseHatchDupeFix", true);
    	
    	use_boss_bar_by_default = BUILDER.comment("Use the boss bar to display player rank information by default. Set to false to disable this behavior.")
    			.define("UseBossBarByDefault", true);
    	boss_bar_default_title = BUILDER.comment("Change the phrase on the boss bar, default is \'Battle Pass | \'").define("BossBarDefaultTitle", "Battle Pass | ");
    	
    	BUILDER.pop();
    	SPEC = BUILDER.build();
    }
    
}
