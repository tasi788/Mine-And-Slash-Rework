package com.robertx22.mine_and_slash.uncommon.interfaces.data_items;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Rarity;

public class VanillaRarities {

    public static Rarity LEGENDARY_ITEM = Rarity.create("LEGENDARY_ITEM", ChatFormatting.GOLD);
    public static Rarity MYTHIC_ITEM = Rarity.create("MYTHIC_ITEM", ChatFormatting.DARK_PURPLE);
    public static Rarity UNIQUE_ITEM = Rarity.create("UNIQUE_ITEM", ChatFormatting.RED);
    public static Rarity RUNED_ITEM = Rarity.create("RUNED_ITEM", ChatFormatting.YELLOW);
    public static Rarity UNCOMMON_ITEM = Rarity.create("UNCOMMON_ITEM", ChatFormatting.GREEN);

    public static void init() {


    }

}
