package com.bbeeaarr.osrs.SkillingLoot;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;

import java.util.Map;

import net.runelite.api.ItemID;
import net.runelite.api.Skill;

@Getter
public enum ChatItemMapping {
    SHARK(Skill.FISHING, ItemID.RAW_SHARK, "shark"),
    RAW_COD(Skill.FISHING, ItemID.RAW_COD, "raw cod"),
    RAW_TUNA(Skill.FISHING, ItemID.RAW_TUNA, "raw tuna"),
    RAW_MACKEREL(Skill.FISHING, ItemID.RAW_MACKEREL, "raw mackerel"),
    RAW_BASS(Skill.FISHING, ItemID.RAW_BASS, "raw bass"),
    OYSTER_SHELL(Skill.FISHING, ItemID.OYSTER, "oyster shell"),
    BOOTS(Skill.FISHING, ItemID.LEATHER_BOOTS, "boots"),
    GLOVES(Skill.FISHING, ItemID.LEATHER_GLOVES, "gloves"),
    LOBSTER(Skill.FISHING, ItemID.RAW_LOBSTER, "lobster"),
    SEAWEED(Skill.FISHING, ItemID.SEAWEED, "seaweed"),
    CASKET(Skill.FISHING, ItemID.CASKET, "casket");

    private static final Map<String, ChatItemMapping> LOOKUP;

    static {
        ImmutableMap.Builder<String, ChatItemMapping> builder = ImmutableMap.builder();

        for (ChatItemMapping e : values()) {
            builder.put(e.itemText, e);
        }

        LOOKUP = builder.build();
    }

    private final Skill skill;
    private final int itemId;
    private final String itemText;

    ChatItemMapping(Skill skill, int itemId, String itemText) {
        this.skill = skill;
        this.itemId = itemId;
        this.itemText = normalize(itemText);
    }

    public static ChatItemMapping lookup(String itemTextFromChat)
    {
        return LOOKUP.get(normalize(itemTextFromChat));
    }

    private static String normalize(String s) {
        return s.trim().toLowerCase();
    }
}
