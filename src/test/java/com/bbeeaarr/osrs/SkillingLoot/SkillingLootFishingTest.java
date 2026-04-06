package com.bbeeaarr.osrs.SkillingLoot;

import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.loottracker.PluginLootReceived;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import net.runelite.client.eventbus.EventBus;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import net.runelite.api.Client;

import java.lang.reflect.Field;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

public class SkillingLootFishingTest
{
    @Mock
    @Bind
    private EventBus eventBus;
    @Mock
    @Bind
    private ConfigManager configManger;

    @Mock
    @Bind
    private SkillingLootConfig config;
    @Mock
    @Bind
    private Client client;
    @Inject
    private SkillingLootPlugin skillingLootPlugin;
    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.openMocks(this);
        Injector injector = Guice.createInjector(BoundFieldModule.of(this));
        skillingLootPlugin = injector.getInstance(SkillingLootPlugin.class);
    }

    @Test
    public void testFishingChatEvents()
    {
        when(config.trackFish()).thenReturn(true);
        final Map<Integer, String> fishToChatMsg = Map.ofEntries(
                Map.entry(ItemID.RAW_ANGLERFISH, "You catch an anglerfish."),
                Map.entry(ItemID.LEATHER_BOOTS, "You catch some boots."),
                Map.entry(ItemID.CASKET, "You catch a casket."),
                Map.entry(ItemID.GIANT_FROGSPAWN, "You catch some giant frogspawn."),
                Map.entry(ItemID.LEATHER_GLOVES, "You catch some gloves."),
                Map.entry(ItemID.INFERNAL_EEL, "You catch an infernal eel. It hardens as you handle it with your ice gloves."),
                Map.entry(ItemID.TBWT_RAW_KARAMBWAN, "You catch a Karambwan!"),
//                Map.entry(ItemID.TBWT_RAW_KARAMBWANJI, "You catch 16 Karambwanji."),
                Map.entry(ItemID.RAW_LAVA_EEL, "You catch a lava eel."),
                Map.entry(ItemID.BRUT_SPAWNING_SALMON, " You catch a leaping salmon."),
                Map.entry(ItemID.BRUT_STURGEON, "You catch a leaping sturgeon."),
                Map.entry(ItemID.BRUT_SPAWNING_TROUT, "You catch a leaping trout."),
                Map.entry(ItemID.RAW_LOBSTER, "You catch a lobster."),
                Map.entry(ItemID.RAW_MONKFISH, "You catch a monkfish."),
                Map.entry(ItemID.OYSTERSHELL, "You catch an oyster shell."),
                Map.entry(ItemID.RAW_PIKE, "You catch a pike."),
                Map.entry(ItemID.HUNTING_RAW_FISH_SPECIAL, "You catch a rainbow fish."),
                Map.entry(ItemID.RAW_ANCHOVIES, " You catch some raw anchovies."),
                Map.entry(ItemID.RAW_BASS, " You catch a raw bass."),
                Map.entry(ItemID.RAW_CATFISH, "You catch a Raw catfish."),
                Map.entry(ItemID.RAW_CAVEFISH, "You catch a Raw cavefish."),
                Map.entry(ItemID.RAW_CAVE_EEL, "You catch a raw cave eel."),
                Map.entry(ItemID.RAW_COD, "You catch a raw cod."),
                Map.entry(ItemID.RAW_GUPPY, "You catch a Raw guppy."),
                Map.entry(ItemID.RAW_HERRING, "You catch a raw herring."),
                Map.entry(ItemID.RAW_MACKEREL, "You catch a raw mackerel."),
                Map.entry(ItemID.RAW_SALMON, "You catch a raw salmon"),
                Map.entry(ItemID.RAW_SARDINE, "You catch a raw sardine."),
                Map.entry(ItemID.RAW_SHRIMP, "You catch some raw shrimps."),
                Map.entry(ItemID.MORT_SLIMEY_EEL, "You catch a raw slimy eel."),
                Map.entry(ItemID.RAW_SWORDFISH, "You catch a raw swordfish."),
                Map.entry(ItemID.RAW_TETRA, "You catch a Raw tetra."),
                Map.entry(ItemID.RAW_TROUT, "You catch a raw trout"),
                Map.entry(ItemID.RAW_TUNA, "You catch a raw tuna."),
                Map.entry(ItemID.SEAWEED, "You catch some seaweed."),
                Map.entry(ItemID.RAW_SHARK, " You catch a shark!")
        );
        int i = 0;
        for(Map.Entry<Integer, String> e : fishToChatMsg.entrySet())
        {
            i++;
            final ChatMessage chatMessage = new ChatMessage(null, ChatMessageType.SPAM, "", e.getValue(), "", 0);
            skillingLootPlugin.onChatMessage(chatMessage);
            ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
            verify(eventBus, times(i)).post(captor.capture());

            Object posted = captor.getValue();
            Assert.assertTrue(posted instanceof PluginLootReceived);
            PluginLootReceived loot = (PluginLootReceived) posted;
            List<ItemStack> items = new ArrayList<>(loot.getItems());

            Assert.assertEquals(e.getKey().intValue(), items.get(0).getId());
            Assert.assertEquals(1, items.get(0).getQuantity());
        }
    }
}