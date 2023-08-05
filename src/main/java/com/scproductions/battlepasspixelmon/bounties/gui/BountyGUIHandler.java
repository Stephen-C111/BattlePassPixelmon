package com.scproductions.battlepasspixelmon.bounties.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.scproductions.battlepasspixelmon.bounties.BountyManager;
import com.scproductions.battlepasspixelmon.bounties.BountyManager.Bounty;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreCriteria.RenderType;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.items.ItemHandlerHelper;

public class BountyGUIHandler {
	
	public static BountyGUIHandler bgui = new BountyGUIHandler();
	private Map<String, Scoreboard> DATA = new HashMap<String, Scoreboard>();
	
	public void updateRankScoreboard() {
		ServerWorld world = ServerLifecycleHooks.getCurrentServer().overworld();
		Scoreboard scoreboard = world.getScoreboard();
		scoreboard.addObjective("Rank", ScoreCriteria.DUMMY, new StringTextComponent("Rank"), RenderType.INTEGER);
		scoreboard.setDisplayObjective(1, scoreboard.getObjective("Rank"));
		for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
			//Add player ranks to scoreboard which should display on the player list.
		}
	}
	
	public void openBountyBoard(ServerPlayerEntity player) {
		ServerWorld world = ServerLifecycleHooks.getCurrentServer().overworld();
	
		Scoreboard scoreboard = world.getScoreboard();
		scoreboard.addObjective("PostedBounties", ScoreCriteria.DUMMY, new StringTextComponent("Posted Bounties"), RenderType.INTEGER);
		scoreboard.addPlayerTeam(player.getName().getString());
		
		
		int i = 0;
		for (Bounty bounty : BountyManager.getActiveBounties()) {
			scoreboard.addPlayerToTeam(bounty.getFormattedString(), scoreboard.getPlayerTeam(player.getName().getString()));
		}
	}
	
	public void updateBountyJournal(ServerPlayerEntity player, boolean giveNewBook) throws CommandSyntaxException {
		boolean found = false;
		Item book = Items.WRITTEN_BOOK;
		ItemStack stack = new ItemStack(book, 1);
		for (ItemStack item : player.inventory.items) { //search for outdated journal and remove it.
			if (item.getHoverName().getString().equals("Bounty Journal")) {
				//Logger.getLogger("BattlePassPixelmon").info("Found book");
				stack = item;
				found = true;
			}
		}
		
		if (!found && !giveNewBook) {
			return;
		}
		
		int numOnPage = 0;
		int i = 0;
		String string = "{pages:['[\"\"";
		for (Bounty bounty : BountyManager.getActiveBounties()) {
			if (BountyManager.getPlayerAcceptedBounties(player.getUUID()).contains(bounty)) {
				i++; //increment but do not add to the book.
				continue;
			}
			if (numOnPage == 4) {
				numOnPage = 0;
				string += "]','[\"\"";
			}
			String command = "/acceptbounty " + i++;
			string += ",{\"text\":\"" + bounty.getFormattedString() + "\",\"underlined\":true,\"color\":\"blue\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + command + "\"}}";
			string += ",{\"text\":\"\\\\n\\\\n\",\"color\":\"reset\"}";
			numOnPage++;
		}
		string += "]'],title:\"Bounty Journal\",author:BattlePassPixelmon,display:{Lore:[\"Right click to open your bounty journal, and click on different bounties to claim them and start working on them!\"]}}";
		CompoundNBT nbt = JsonToNBT.parseTag(string);
		//Logger.getLogger("BattlePassPixelmon").info(string);
		stack.setTag(nbt);
		if (!found) {
			ItemHandlerHelper.giveItemToPlayer(player, stack);
		}
	}
}
