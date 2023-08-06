package com.scproductions.battlepasspixelmon;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.scproductions.battlepasspixelmon.bounties.BountyManager;
import com.scproductions.battlepasspixelmon.bounties.BountyManager.Bounty;
import com.scproductions.battlepasspixelmon.bounties.BountyManagerListeners;

import net.minecraft.block.Block;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("battlepasspixelmon")
public class Main
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger("BattlePassPixelmon");
    public static final String MOD_ID = "battlepasspixelmon";
    public static final String REWARDPACKCONFIGLOCATION = FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath()).getFileName().toString() + "\\RewardPackConfig-0.4.1.json";
    public static final String RANKUPREWARDCONFIGLOCATION = FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath()).getFileName().toString() + "\\RankupRewardConfig.json";
    public static final String ACCEPTEDBOUNTIESCONFIGLOCATION = FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath()).getFileName().toString() + "\\AcceptedBounties.json";
    public static final String COMPLETEDBOUNTIESCONFIGLOCATION = FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath()).getFileName().toString() + "\\CompletedBounties.json";
    
    public static Timer timer = new Timer();
    
    public Main() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        
        ModLoadingContext.get().registerConfig(Type.COMMON, BattlePassConfig.SPEC, MOD_ID + "-commonconfig-0.3.1.toml");
        
        
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        PXListeners px = new PXListeners();
        BountyManagerListeners bml = new BountyManagerListeners();
        Pixelmon.EVENT_BUS.register(this);
        Pixelmon.EVENT_BUS.register(px);
        Pixelmon.EVENT_BUS.register(bml);
        
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) throws IOException {
        // do something when the server starts
        LOGGER.info("BattlePassPixelmon is installed.");
        LOGGER.info("Loading RewardPacks.");
        
        try {
			RewardPackManager.rpm.loadPacksJson();
			RankUpRewardManager.rrm.loadRewardsJson();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //Set up a recurring task to refresh every x minutes starting from server launch.
        LocalDateTime localdate = LocalDateTime.now();
        Date date = Date.from(localdate.atZone(ZoneId.systemDefault()).toInstant());
        timer.scheduleAtFixedRate(new BountyScheduleTask(), date, 60000 * BattlePassConfig.minutes_to_refresh_board.get());
        
        LOGGER.info("Finished Loading RewardPacks.");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            //LOGGER.info("HELLO from Register Block");
        }
    }
    
    class BountyScheduleTask extends TimerTask {
		@Override
		public void run() {
			LOGGER.info("Refreshing the Bounty Board");
			try {
				BountyManager.refreshBoard();
			} catch (CommandSyntaxException e) {
				// TODO Auto-generated catch block
				LOGGER.info("Could not refresh the board!");
				e.printStackTrace();
			}
			for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()){
				player.sendMessage(new StringTextComponent("The Bounty Board has refreshed, type /getjournal to see the new bounties!"), player.getUUID());
			}
			//for (Bounty bounty : BountyManager.getActiveBounties()) {
	        //	LOGGER.info(bounty.getFormattedString());
	        //}
		}
    	
    }
}
