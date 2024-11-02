package com.robertx22.mine_and_slash.database.data.rarities;

import com.robertx22.mine_and_slash.database.registry.ExileDB;
import com.robertx22.mine_and_slash.maps.MapData;
import net.minecraft.resources.ResourceLocation;

import java.util.Comparator;

public class MapRarityRewardData {

    public int perc_to_unlock = 0;
    public ResourceLocation loot_table = new ResourceLocation("");
    public int reward_chests = 0; // todo rework later to chest amount
    public float loot_multi = 1;

    public MapRarityRewardData(int perc_to_unlock, ResourceLocation loot_table, int reward_chests, float loot_multi) {
        this.perc_to_unlock = perc_to_unlock;
        this.loot_table = loot_table;
        this.reward_chests = reward_chests;
        this.loot_multi = loot_multi;
    }

    public static int getMapCompletePercent(MapData map) {
        int mobs = map.rooms.mobs.getPercentDone();
        int chests = map.rooms.chests.getPercentDone();
        return (mobs + chests) / 2;
    }


    public static void updateMapCompletionRarity(MapData map) {

        int perc = getMapCompletePercent(map);

        var rar = ExileDB.GearRarities().getFilterWrapped(x -> x.map_reward != null && perc > x.map_reward.perc_to_unlock).list.stream().max(Comparator.comparing(x -> x.item_tier)).get();

        map.completion_rarity = rar.GUID();

    }
}
