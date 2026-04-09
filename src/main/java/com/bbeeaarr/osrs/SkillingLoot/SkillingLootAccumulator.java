package com.bbeeaarr.osrs.SkillingLoot;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.loottracker.PluginLootReceived;
import net.runelite.http.api.loottracker.LootRecordType;
import net.runelite.client.game.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class SkillingLootAccumulator {
    private final Plugin source;
    private final String name;
    private int amount = 1;
    private Object metadata;

    private ItemStack base;
    private int baseMultiplier = 1;
    private final List<ItemStack> extras = new ArrayList<>();

    public SkillingLootAccumulator(Plugin source, String name, ItemStack baseItem)
    {
        this.name = name;
        this.base = baseItem;
        this.source = source;
    }

    public void addBonusBaseItem()
    {
        log.debug("Bonus Base Item Added");
        if (base == null)
            throw new IllegalStateException("No base item set");
        baseMultiplier++;
    }

    public void addBonusOtherItem(ItemStack bonus)
    {
        extras.add(bonus);
    }

    public PluginLootReceived build()
    {
        if (base == null)
            throw new IllegalStateException("Cannot build: base item not set");
        return PluginLootReceived.builder()
                .source(source)
                .name(name)
                .type(LootRecordType.EVENT)
                .items(buildFinalItemStack())
                .build();

    }

    private List<ItemStack> buildFinalItemStack()
    {
        final List<ItemStack> allItems = new ArrayList<>();
        allItems.add(new ItemStack(base.getId(), base.getQuantity() * baseMultiplier));
        extras.forEach(item -> allItems.add(new ItemStack(item.getId(), item.getQuantity())));
        return allItems;
    }
}
