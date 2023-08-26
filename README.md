# BattlePassPixelmon
This is a BattlePass mod for use on Pixelmon servers.

It responds to events such as catching and fighting pokemon, as well as other vanilla events to encourage playing the game how the player sees fit.
Each player has a copy of a BattlePass that tracks their rank, progress to the next rank, and how many rewards they are eligible to claim.
Players receive rewards for specific ranks via the RewardPack System explained below. Players can claim random rewards for each rank they've climbed.

In addition to the tracked activities, players can accept bounties that require specific objectives to be completed for a larger rank progress reward.

The mod is highly configurable, most values can be changed with the config file to suit your server's needs.

![javaw_YRIcSBm5Dx](https://github.com/Stephen-C111/BattlePassPixelmon/assets/30613192/06e74a4b-ebf1-4ece-b9a7-5cc31206b241)

## Operator Commands:
- /giveprogress [player] [amount] : Grant a player rank progress.
- /giveselfprogress [amount] : Grant yourself rank progress.
- /resetrank [player] [--confirmationargument1] [--confirmationargument2] : Delete a player's progress. The deleted data will be saved in defaultconfigs in case this was an accident.
- /newrewardpack [rank] [item] [amount] : Create a new RewardPack at runtime.
- /newrandomreward [weight] [item] [amount] : Create a new random reward which is rolled when a player does /claimranks. Higher weights mean higher chances to get it. Check RankupRewardConfig.json to see what has been added to this list, and decide if you want to manually add or remove rewards from there.
- /reloadrewardpacks : Reload the RewardPacksConfig.json file without restarting the server. Useful if you manually edited the config file during runtime.
- /completeallbounties [player] [--confirmationargument] : Cheat complete all bounties on a player.

##   Unprivileged Commands:
- /checkrank : Shows the player their rank and progress towards the next rank.
- /claimranks : Grants the player rewards for each unclaimed random rank reward.
- /claimrewardpacks : Checks to see if there are any new RewardPacks the player has not claimed yet, and supplies the player with the rewards if so.
- /togglebattlepassmessages : Toggle allowing sending messages to the chat, if the player is annoyed by the messages.
- /togglebattlepassbossbar : Toggle whether the boss bar should display BattlePass information.
- /getjournal : Get the journal that shows all active claimable bounties.
- /acceptbounty [number] : Used by the journal to claim an active claimable bounty. Not necessary for a player to use.
- /acceptedbounties : Show all bounties claimed by the player who uses it.
- /discardbounty [number as shown from /acceptedbounties] [--confirmationargument] : Discard the specified bounty denoted by the number when using /acceptedbounties

## Tracked activities:
- Caught pokemon (Legendary bonus and level included)
- Defeated pokemon (Legendary bonus included, level, and boss tier included)
- Defeated trainer (number of pokemon, level, and boss tier included)
- Evolved pokemon (Legendary bonus included, for typenull to silvally)
- Hatched pokemon (perfect (31) IV bonus included. Base reward is multiplied by number of perfect IVs)
- Successful Pixelmon and vanilla rod reel in
- 7% chance to gain points for apricorn picking, 1% chance for a double point reward.
- Mining blocks that drop experience (Coal, Diamond, Quartz, Gold Nugget Ores)

## RewardPack System:
  Basic Details:
The RewardPack system allows server operators to define new rewards for reaching a specified rank.
Each RewardPack can only be claimed once, unless the player's pass is reset, or the UUID of the RewardPack is modified.

###  Creating your own RewardPacks:
Server operators can run the /newrewardpack command at runtime to insert a new line into the defaultconfigs\RewardPacksConfig.json file.
Users who can access the server files can modify this file directly. Ensure you stick to the conventions in the file.
Importantly, you MUST separate any rewardpacks into new lines denoted by a number on your IDE of choice, usually. For an example line see:

- {"rank":4,"uuid":"0ac0b6e7-c466-46d2-b02e-359aa5d36579","pairs":[{"itemID":"minecraft:bookshelf","amount":5},{"itemID":"minecraft:experience_bottle","amount":16,"nbt":"{}"}]}
This line will give any player who has reached rank 4 the following items: 5 bookshelves, and 16 experience bottles.
- "rank" defines the rank.
- "uuid" defines the UUID that is checked to ensure no duplicate rewards are claimed. DO NOT modify this unless you intend for players to be eligible to claim this pack again.
You can generate a unique UUID online or create RewardPacks from the given command to get a random one.
- "pairs" defines a very simple structure called an ItemPair. This consists of an itemID (pixelmon:poke_ball or minecraft:oak_door) for example, and an amount of the item to be given.
- "nbt" contains nbt data if specified upon rewardpack creation.
As of 0.4.1, nbt data can and will be saved if any is defined.
- If you've never worked with json files before, be aware that if you mistype one of these lines, or forget to use a comma, parenthesis, etc. your RewardPack will be unreadable.
Please stick to using the supplied command in-game if you do not know what you're doing.
You can delete the RewardPackConfig to re-generate the default packs when the server restarts. Be careful not to lose any custom packs you've defined.
Finally, know that you are safe to delete any default entries without concern, and you must restart the server for any external changes to take effect.

## Bounty System
The bounty system allows players to pick up a bounty, which contains an objective that has its progress tracked. Upon completion of the bounty, it is automatically claimed and the point reward towards the battle pass is rewarded to the player.
The bounties players can choose from will reset every 30 (by default) minutes, and players can choose the exact same bounties within that timeframe. Any bounties players are still working on will be tracked, but the rest will be removed to save space.

### Possible Bounties:
- Examples of bounties:
- Catch / Defeat x of up to 3 specific types, or any type Pokemon.
- Catch / Defeat x Legendary Pokemon.
- Defeat x Trainers.
- Defeat x Boss Pokemon.
- Reel in x Times While Fishing.
  
![javaw_zi69WaHhfG](https://github.com/Stephen-C111/BattlePassPixelmon/assets/30613192/0bff721a-cd47-48c0-9a7a-6a9f327bd8a2)

Players can use /acceptedbounties to see which bounties they own. They can discard bounties that are too difficult, as well.

![javaw_9SPKYb0wke](https://github.com/Stephen-C111/BattlePassPixelmon/assets/30613192/e3ecba34-eb75-4360-9da2-922f2b18bb0b)

## Getting Started - Operators
If you've installed the mod, you're probably going to want to know how to validate that it's working properly before launching it on your server. Here's some first steps to get you comfortable with the mod.
### In game
- Start by making sure the mod is in the 'mods' folder, and not the 'plugins' folder.
- Launch your server (preferably a test server, but I'll go over how to undo any changes you may have made in a moment.)
- Type /getjournal to see the active list of bounties, and just start clicking some that are easy enough for you to do.
- Type /acceptedbounties to see your personal list of accepted bounties, and re-type it to validate that you are gaining progress for engaging with the activity specified.
- Type /completeallbounties [Your player name] --irreversiblecompletion (For --arguments like this, simply press tab. I put these in place as warnings for actions that are not easy to revert from.)
- Type /newrandomreward [any positive number - higher means it's more likely to be this reward] [item - minecraft:apple for example] [amount]
- Type /newrewardpack [any positive number - try rank 0 for a starter kit functionality] [item] [amount]
- Type /giveprogress <Your player name> <any positive number - I recommend 10000>.
- Type /claimranks and /claimrewardpacks
- Note how the rewards you added before should have been given to you if you met the criteria (and got lucky with your random roll.)
- Type /resetrank <Your player name> followed by the 2 --arguments (Press TAB). This will delete the rank progress you built up during testing. (Note that there's a minor bug that causes the bossbar to disappear until restart if you do this command.)
### In server files
Now that you've tested out the commands in game, you should know how to configure the mod from outside the game environment, particularly if you need to remove rewardpacks or randomrewards, or need to reset progress on the mod.
- Navigate to your server files
- In \config, you will find a file named similarly to 'battlepasspixelmon-commonconfig-0.x.x.toml'
- Open this file and change any settings you like, one of interest is the BossBarDefaultTitle, which allows you to change the display of the bossbar to say whatever you like before the rank information.
- In \defaultconfigs you will find 4 different .json files. You will also find a .txt preceded with 'DELETED_DATA_' which details the deleted player data when you perform /resetrank.
- For RewardPackConfig and RankupRewardConfig, read the above section titled "Creating your own rewardpacks" to understand how these files work. Feel free to remove any rewards that seem unfair for your server environment.
- Next, I'll show you how to reset all of the data for the mod, excluding the changes you may have made to rewards.
- In \defaultconfigs, you can delete AcceptedBounties.json and CompletedBounties.json
- In \world\data, you can delete battlepasspixelmon_BattlePassManager.dat and battlepasspixelmon_BountyManager.dat.
- These files will regenerate as needed, without player intervention.

This should be all you need to be familiar with the mod as an operator, read through the commands and decide how you would like to inform your players about the commands available to them.

## Getting Started - Normal Players
If you're not concerned with admin commands, here are some first steps you can take as a player to experience this mod.
- Type /getjournal and select a bounty from the book you'd like to complete. You can hold up to 4 by default.
- Type /acceptedbounties to check up on your progress with your bounties.
- Type /claimranks and /claimrewardpacks to claim any valid rewards.
- Type /togglebattlepassbossbar to turn the boss bar off, if you prefer it off. Type /checkrank to check your rank if you do this.
- Type /togglebattlepassmessages to turn off permanent chat messages, if you feel the mod sends you too much spam to your chatbox.
- Type /discardbounty [number] : The number in question will be either 0, 1, 2, or 3. These correspond to the bounties seen when you type /acceptedbounties.

Those should be all the commands you need to know to use the mod as a non-operator player.
