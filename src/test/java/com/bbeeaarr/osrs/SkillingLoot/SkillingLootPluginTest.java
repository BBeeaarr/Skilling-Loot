package com.bbeeaarr.osrs.SkillingLoot;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;
import javax.inject.Inject;

public class SkillingLootPluginTest
{
	@Inject
	private SkillingLootPlugin skillingLootPlugin;
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(SkillingLootPlugin.class);
		RuneLite.main(args);
	}
}