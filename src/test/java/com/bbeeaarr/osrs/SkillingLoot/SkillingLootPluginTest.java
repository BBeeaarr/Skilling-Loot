package com.bbeeaarr.osrs.SkillingLoot;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class SkillingLootPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(SkillingLootPlugin.class);
		RuneLite.main(args);
	}
}