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
    
    public static final ForgeConfigSpec.ConfigValue<Integer> minutes_to_refresh_board;
    public static final ForgeConfigSpec.ConfigValue<Integer> available_bounties_at_once;
    
    public static final ForgeConfigSpec.ConfigValue<Integer> bounty_fish_amount;
    public static final ForgeConfigSpec.ConfigValue<Integer> bounty_fish_reward;
    
    public static final ForgeConfigSpec.ConfigValue<Integer> bounty_catch_amount;
    public static final ForgeConfigSpec.ConfigValue<Integer> bounty_catch_reward;
    
    public static final ForgeConfigSpec.ConfigValue<Integer> bounty_kill_amount;
    public static final ForgeConfigSpec.ConfigValue<Integer> bounty_kill_reward;
    
    public static final ForgeConfigSpec.ConfigValue<Integer> bounty_boss_amount;
    public static final ForgeConfigSpec.ConfigValue<Integer> bounty_boss_reward;
    
    public static final ForgeConfigSpec.ConfigValue<Integer> bounty_legend_amount;
    public static final ForgeConfigSpec.ConfigValue<Integer> bounty_legend_reward;
    
    public static final ForgeConfigSpec.ConfigValue<Integer> bounty_trainer_amount;
    public static final ForgeConfigSpec.ConfigValue<Integer> bounty_trainer_reward;
    
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
    	
    	minutes_to_refresh_board = BUILDER.comment("Time it takes for the bounty board to refresh in minutes.").define("MinutesToRefreshBoard", 30);
    	available_bounties_at_once = BUILDER.comment("The available bounties in the journal at once, default of 12.").define("MaxBountiesOnBoard", 12);
    	
    	bounty_fish_amount = BUILDER.comment("Base amount of bounties with tag: FISH").define("BountyFishAmount", 25);
    	bounty_fish_reward = BUILDER.comment("Base reward per amount in bounties with tag: FISH").define("BountyFishReward", 3);
    	
    	bounty_catch_amount = BUILDER.comment("Base amount of bounties with tag: CATCH").define("BountyCatchAmount", 10);
    	bounty_catch_reward = BUILDER.comment("Base reward per amount in bounties with tag: CATCH").define("BountyCatchReward", 12);
    	
    	bounty_kill_amount = BUILDER.comment("Base amount of bounties with tag: KILL").define("BountyKillAmount", 20);
    	bounty_kill_reward = BUILDER.comment("Base reward per amount in bounties with tag: KILL").define("BountyKillReward", 8);
    	
    	bounty_boss_amount = BUILDER.comment("Base amount of bounties with tag: BOSS").define("BountyBossAmount", 3);
    	bounty_boss_reward = BUILDER.comment("Base reward per amount in bounties with tag: BOSS").define("BountyBossReward", 80);
    	
    	bounty_legend_amount = BUILDER.comment("Base amount of bounties with tag: LEGEND").define("BountyLegendAmount", 1);
    	bounty_legend_reward = BUILDER.comment("Base reward per amount in bounties with tag: LEGEND").define("BountyLegendReward", 500);
    	
    	bounty_trainer_amount = BUILDER.comment("Base amount of bounties with tag: TRAINER").define("BountyTrainerAmount", 3);
    	bounty_trainer_reward = BUILDER.comment("Base reward per amount in bounties with tag: TRAINER").define("BountyTrainerReward", 100);
    	
    	BUILDER.pop();
    	SPEC = BUILDER.build();
    }
    
}
