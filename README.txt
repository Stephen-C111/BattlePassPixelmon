This is a BattlePass mod for use on Pixelmon servers.

It responds to events such as catching and fighting pokemon, as well as other vanilla events to encourage playing the game how the player sees fit.
Each player has a copy of a BattlePass that tracks their rank, progress to the next rank, and how many rewards they are eligible to claim.
Players receive rewards for specific ranks via the RewardPack System explained below. For now, an Exp Candy XL is rewarded for every rank up as a generic reward.

Commands:
  OP required:
/giveprogress [player] [amount] : Grant a player rank progress.
/giveselfprogress [amount] : Grant yourself rank progress.
/resetrank [player] [--confirmationargument1] [--confirmationargument2] : Delete a player's progress. The deleted data will be saved in defaultconfigs in case this was an accident.
/newrewardpack [rank] [item] [amount] : Create a new RewardPack at runtime.
/reloadrewardpacks : Reload the RewardPacksConfig.json file without restarting the server. Useful if you manually edited the config file during runtime.

  Unprivileged:
/checkrank : Shows the player their rank and progress towards the next rank.
/claimranks all : Grants the player rewards for each unclaimed generic rank reward.
/claimrewardpacks : Checks to see if there are any new RewardPacks the player has not claimed yet, and supplies the player with the rewards if so.
/togglebattlepassmessages : Disallows sending messages to the chat, if the player is annoyed by the messages.

Tracked activities:
Caught pokemon (Legendary bonus included)
Defeated pokemon (Legendary bonus included)
Defeated trainer (number of pokemon, level, and boss tier included)
Evolved pokemon (Legendary bonus included, for typenull to silvally)
Hatched pokemon (perfect (31) IV bonus included. Base reward is multiplied by number of perfect IVs)
Successful Pixelmon and vanilla rod reel in
7% chance to gain points for apricorn picking, 1% chance for a double point reward.

RewardPack System:
  Basic Details:
The RewardPack system allows server operators to define new rewards for reaching a specified rank.
Each RewardPack can only be claimed once, unless the player's pass is reset, or the UUID of the RewardPack is modified.

  Creating your own RewardPacks:
Server operators can run the /newrewardpack command at runtime to insert a new line into the defaultconfigs\RewardPacksConfig.json file.
Users who can access the server files can modify this file directly. Ensure you stick to the conventions in the file.
Importantly, you MUST separate any rewardpacks into new lines denoted by a number on your IDE of choice, usually. For an example line see:

{"rank":4,"uuid":"0ac0b6e7-c466-46d2-b02e-359aa5d36579","pairs":[{"itemID":"minecraft:bookshelf","amount":5},{"itemID":"minecraft:experience_bottle","amount":16,"nbt":"{}"}]}
This line will give any player who has reached rank 4 the following items: 5 bookshelves, and 16 experience bottles.
"rank" defines the rank.
"uuid" defines the UUID that is checked to ensure no duplicate rewards are claimed. DO NOT modify this unless you intend for players to be eligible to claim this pack again.
You can generate a unique UUID online or create RewardPacks from the given command to get a random one.
"pairs" defines a very simple structure called an ItemPair. This consists of an itemID (pixelmon:poke_ball or minecraft:oak_door) for example, and an amount of the item to be given.
"nbt" contains nbt data if specified upon rewardpack creation.
As of 0.4.1, nbt data can and will be saved if any is defined.
If you've never worked with json files before, be aware that if you mistype one of these lines, or forget to use a comma, parenthesis, etc. your RewardPack will be unreadable.
Please stick to using the supplied command in-game if you do not know what you're doing.
You can delete the RewardPackConfig to re-generate the default packs when the server restarts. Be careful not to lose any custom packs you've defined.
Finally, know that you are safe to delete any default entries without concern, and you must restart the server for any external changes to take effect.
