package com.bbeeaarr.osrs.SkillingLoot;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("SkillingLoot")
public interface SkillingLootConfig extends Config
{
	String CONFIG_GROUP = "skilling loot";
	@ConfigItem(
		keyName = "trackFish",
		name = "Track Fish Caught",
		description = "Posts fish caught as loot to the loot tracker"
	)
	default boolean trackFish()
	{
		return true;
	}
}
