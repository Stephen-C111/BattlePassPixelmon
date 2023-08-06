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
