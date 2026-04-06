package com.bbeeaarr.osrs.SkillingLoot;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.FishingSpot;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.loottracker.PluginLootReceived;
import net.runelite.http.api.loottracker.LootRecordType;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@PluginDescriptor(
	name = "SkillingLoot"
)
public class SkillingLootPlugin extends Plugin
{
	@Inject
	private EventBus eventBus;
	private static final Pattern FISHING_CATCH_REGEX = Pattern.compile(
			"You catch (?:(?:a|an|some)\\s+)?(?:(?<quantityOverride>\\d+)\\s+)?(?<itemText>[^.!?\\r\\n]+)(?:[.!?])?(?:\\s|$)"
					+ "|Your cormorant returns with its catch\\."
	);
	@Inject
	private Client client;

	@Inject
	private SkillingLootConfig config;

	@Override
	protected void startUp() throws Exception
	{
		log.debug("Example started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.debug("Example stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says " + config.greeting(), null);
		}
	}

	@Provides
	SkillingLootConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SkillingLootConfig.class);
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (event.getType() != ChatMessageType.SPAM)
		{
			return;
		}
		var message = event.getMessage();

		final Matcher m =  FISHING_CATCH_REGEX.matcher(message);
		if (m.find())
		{
			String itemText = m.group("itemText") != null ? m.group("itemText") : m.group("itemText2");
			if (itemText == null)
				return;
			ChatItemMapping mapping = ChatItemMapping.lookup(itemText);
			if (mapping == null)
				return;

			// Advertise the loot
			var lootEvent = PluginLootReceived.builder()
					.source(this)
					.name(mapping.getSkill().getName())
					.type(LootRecordType.EVENT)
					.items(Collections.singletonList(new ItemStack(mapping.getItemId(), 1)))
					.build();
			eventBus.post(lootEvent);
		}

	}

	@Subscribe
	public void onInteractingChanged(InteractingChanged event)
	{
		if (event.getSource() != client.getLocalPlayer())
		{
			return;
		}

		final Actor target = event.getTarget();

		if (!(target instanceof NPC))
		{
			return;
		}

		final NPC npc = (NPC) target;
		FishingSpot spot = FishingSpot.findSpot(npc.getId());

		if (spot == null)
		{
			return;
		}

		log.debug("spot: name={} Ids={} fishSpriteId={}", spot.getName(), spot.getIds(), spot.getFishSpriteId());
	}
}
