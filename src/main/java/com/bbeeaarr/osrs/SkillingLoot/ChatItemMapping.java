package com.bbeeaarr.osrs.SkillingLoot;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;

import java.util.Map;

import net.runelite.api.ItemID;
import net.runelite.api.Skill;

@Getter
public enum ChatItemMapping {
    ANGLERFISH(Skill.FISHING, ItemID.RAW_ANGLERFISH, "anglerfish"),
    BOOTS(Skill.FISHING, ItemID.LEATHER_BOOTS, "boots"),
    CASKET(Skill.FISHING, ItemID.CASKET, "casket"),
    GIANT_FROGSPAWN(Skill.FISHING, ItemID.FROG_SPAWN, "giant frogspawn"),
    GLOVES(Skill.FISHING, ItemID.LEATHER_GLOVES, "gloves"),
    INFERNAL_EEL(Skill.FISHING, ItemID.INFERNAL_EEL, "infernal eel"),
    KARAMBWAN(Skill.FISHING, ItemID.RAW_KARAMBWAN, "karambwan"),
    KARAMBWANJI(Skill.FISHING, ItemID.RAW_KARAMBWANJI, "karambwanji"),
    LAVA_EEL(Skill.FISHING, ItemID.RAW_LAVA_EEL, "lava eel"),
    LEAPING_SALMON(Skill.FISHING, ItemID.LEAPING_SALMON, "leaping salmon"),
    LEAPING_STURGEON(Skill.FISHING, ItemID.LEAPING_STURGEON, "leaping sturgeon"),
    LEAPING_TROUT(Skill.FISHING, ItemID.LEAPING_TROUT, "leaping trout"),
    LOBSTER(Skill.FISHING, ItemID.RAW_LOBSTER, "lobster"),
    MONKFISH(Skill.FISHING, ItemID.RAW_MONKFISH, "monkfish"),
    OYSTER_SHELL(Skill.FISHING, ItemID.OYSTER, "oyster shell"),
    PIKE(Skill.FISHING, ItemID.RAW_PIKE, "pike"),
    RAINBOW_FISH(Skill.FISHING, ItemID.RAW_RAINBOW_FISH, "rainbow fish"),
    RAW_ANCHOVIES(Skill.FISHING, ItemID.RAW_ANCHOVIES, "raw anchovies"),
    RAW_BASS(Skill.FISHING, ItemID.RAW_BASS, "raw bass"),
    RAW_CATFISH(Skill.FISHING, ItemID.RAW_CATFISH, "raw catfish"),
    RAW_CAVEFISH(Skill.FISHING, ItemID.RAW_CAVEFISH, "raw cavefish"),
    RAW_CAVE_EEL(Skill.FISHING, ItemID.RAW_CAVE_EEL, "raw cave eel"),
    RAW_COD(Skill.FISHING, ItemID.RAW_COD, "raw cod"),
    RAW_GUPPY(Skill.FISHING, ItemID.RAW_GUPPY, "raw guppy"),
    RAW_HERRING(Skill.FISHING, ItemID.RAW_HERRING, "raw herring"),
    RAW_MACKEREL(Skill.FISHING, ItemID.RAW_MACKEREL, "raw mackerel"),
    RAW_SALMON(Skill.FISHING, ItemID.RAW_SALMON, "raw salmon"),
    RAW_SARDINE(Skill.FISHING, ItemID.RAW_SARDINE, "raw sardine"),
    RAW_SHRIMPS(Skill.FISHING, ItemID.RAW_SHRIMPS, "raw shrimps"),
    RAW_SLIMY_EEL(Skill.FISHING, ItemID.RAW_SLIMY_EEL, "raw slimy eel"),
    RAW_SWORDFISH(Skill.FISHING, ItemID.RAW_SWORDFISH, "raw swordfish"),
    RAW_TETRA(Skill.FISHING, ItemID.RAW_TETRA, "raw tetra"),
    RAW_TROUT(Skill.FISHING, ItemID.RAW_TROUT, "raw trout"),
    RAW_TUNA(Skill.FISHING, ItemID.RAW_TUNA, "raw tuna"),
    SEAWEED(Skill.FISHING, ItemID.SEAWEED, "seaweed"),
    SHARK(Skill.FISHING, ItemID.RAW_SHARK, "shark");


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
