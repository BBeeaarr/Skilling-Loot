package com.bbeeaarr.osrs.SkillingLoot;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.FishingSpot;
import net.runelite.client.game.ItemStack;
import net.runelite.client.game.LootManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.loottracker.LootTrackerPlugin;
import net.runelite.client.plugins.loottracker.PluginLootReceived;
import net.runelite.http.api.loottracker.LootRecordType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
	private LootManager lootManager;
	private SkillingLootAccumulator pendingLoot;
	private int pendingLootTicksRemaining;
	private boolean pendingClue;
	private int inventoryId = -1;
	private Multiset<Integer> inventorySnapshot;
	private InvChangeCallback inventorySnapshotCb;
	private int inventoryTimeout;
	@Inject
	private Client client;

	@Inject
	private SkillingLootConfig config;

	@Override
	protected void startUp() throws Exception
	{
		resetPendingLoot();
		log.debug("Example started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		resetPendingLoot();
		log.debug("Example stopped!");
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

		if (config.trackFish())
		{
			final Matcher m =  FISHING_CATCH_REGEX.matcher(message);
			if (m.find())
			{
				String itemText = m.group("itemText");
				Integer itemQuantity = m.group("quantityOverride") != null? Integer.parseInt(m.group("quantityOverride")) : 1;
				if (itemText == null)
					return;
				if (itemText == "clue bottle")
				{
					log.debug("clue bottle drop");
					onInvChange(InventoryID.INV, collectInvItems(LootRecordType.EVENT, "Unsired"), 10);
					return;
				}
				ChatItemMapping mapping = ChatItemMapping.lookup(itemText);
				if (mapping == null)
					return;

				int itemID = mapping.getItemId();
				String skill = mapping.getSkill().getName();

				pendingLoot = new SkillingLootAccumulator(this, skill, new ItemStack(itemID, itemQuantity));
				pendingLootTicksRemaining = 1;

				log.debug("Loot Initialized ItemID={} ItemQuantity={}",itemID, itemQuantity);
			}
			else if (message.contains("The spirit flakes enabled you to catch an extra fish"))
			{
				pendingLoot.addBonusBaseItem();
			}
			else if(message.contains("Rada's blessing enabled you to catch an extra fish"))
			{
				pendingLoot.addBonusBaseItem();
			}
			if(message.contains("clue bottle"))
			{
				log.debug("clue bottle drop");
				onInvChange(InventoryID.INV, collectInvItems(LootRecordType.EVENT, "Unsired"), 10);
			}

		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if(pendingLoot != null && --pendingLootTicksRemaining == 0)
		{
			eventBus.post(pendingLoot.build());
			resetPendingLoot();
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{

		if (inventoryId == -1 || event.getContainerId() != inventoryId)
		{
			return;
		}

		final ItemContainer inventoryContainer = event.getItemContainer();
		Multiset<Integer> currentInventory = HashMultiset.create();
		Arrays.stream(inventoryContainer.getItems())
				.forEach(item -> currentInventory.add(item.getId(), item.getQuantity()));
		WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
		final Collection<ItemStack> groundItems = lootManager.getItemSpawns(playerLocation);

		final Multiset<Integer> diff = Multisets.difference(currentInventory, inventorySnapshot);
		final Multiset<Integer> diffr = Multisets.difference(inventorySnapshot, currentInventory);

		final List<ItemStack> items = diff.entrySet().stream()
				.map(e -> new ItemStack(e.getElement(), e.getCount()))
				.collect(Collectors.toList());
		log.debug("Inv change: {} Ground items: {}", items, groundItems);

		if (inventorySnapshotCb != null)
		{
			inventorySnapshotCb.accept(items, groundItems, diffr);
		}

		resetEvent();
	}

	@Subscribe
	public void onPostClientTick(PostClientTick postClientTick)
	{
		if (inventoryTimeout > 0)
		{
			if (--inventoryTimeout == 0)
			{
				log.debug("Inventory snapshot: Loot timeout");
				resetEvent();
			}
		}
	}

	public void resetPendingLoot()
	{
		pendingLoot = null;
		pendingLootTicksRemaining = 0;
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

	@FunctionalInterface
	interface InvChangeCallback
	{
		void accept(Collection<ItemStack> invItems, Collection<ItemStack> groundItems, Multiset<Integer> removedItems);
	}

	private InvChangeCallback collectInvItems(LootRecordType type, String event)
	{
		return collectInvItems(type, event, null);
	}

	private InvChangeCallback collectInvItems(LootRecordType type, String event, Object metadata)
	{
		return (invItems, groundItems, removedItems) ->
		{
			invItems.forEach(item -> pendingLoot.addBonusOtherItem(item));
		};
	}

	private void onInvChange(int inv, InvChangeCallback cb, int timeout)
	{
		inventoryId = inv;
		inventorySnapshot = HashMultiset.create();
		inventorySnapshotCb = cb;
		inventoryTimeout = timeout * Constants.GAME_TICK_LENGTH / Constants.CLIENT_TICK_LENGTH;

		final ItemContainer itemContainer = client.getItemContainer(inv);
		if (itemContainer != null)
		{
			Arrays.stream(itemContainer.getItems())
					.forEach(item -> inventorySnapshot.add(item.getId(), item.getQuantity()));
		}
	}

	private void resetEvent()
	{
		inventoryId = -1;
		inventorySnapshot = null;
		inventorySnapshotCb = null;
		inventoryTimeout = 0;
	}
}
