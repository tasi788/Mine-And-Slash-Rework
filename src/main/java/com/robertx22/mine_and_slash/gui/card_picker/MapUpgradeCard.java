package com.robertx22.mine_and_slash.gui.card_picker;

import com.robertx22.library_of_exile.main.Packets;
import com.robertx22.mine_and_slash.mmorpg.SlashRef;
import com.robertx22.mine_and_slash.mmorpg.registers.common.items.SlashItems;
import com.robertx22.mine_and_slash.uncommon.datasaving.Load;
import com.robertx22.mine_and_slash.uncommon.localization.Words;
import com.robertx22.mine_and_slash.uncommon.utilityclasses.PlayerUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class MapUpgradeCard implements ICard {


    public enum MapOption {
        UPGRADE("upgrade", Words.UPGRADE_MAP, Words.UPGRADE_MAP_DESC) {
            @Override
            public boolean canPick(Player p) {
                var map = Load.player(p).map;
                if (map.map == null) {
                    return false;
                }
                return map.map.getRarity().hasHigherRarity() && Load.player(p).map.killed_boss;
            }

            @Override
            public void onPick(Player p) {
                var stack = SlashItems.MAP.get().getDefaultInstance();
                var map = Load.player(p).map.map;
                map.setRarityAndRerollNeeded(map.getRarity().getHigherRarity());
                map.saveToStack(stack);

                PlayerUtils.giveItem(stack, p);
                Load.player(p).map.clearMapAfterUpgrading();
            }
        },

        DOWNGRADE("downgrade", Words.DOWNGRADE_MAP, Words.DOWNGRADE_MAP_DESC) {
            @Override
            public boolean canPick(Player p) {
                var map = Load.player(p).map;
                if (map.map == null) {
                    return false;
                }
                return true;
            }

            @Override
            public void onPick(Player p) {
                var stack = SlashItems.MAP.get().getDefaultInstance();
                var map = Load.player(p).map.map;

                var rar = map.getRarity();
                if (rar.getLowerRarity().isPresent()) {
                    rar = rar.getLowerRarity().get();
                }

                map.setRarityAndRerollNeeded(rar);
                map.saveToStack(stack);

                PlayerUtils.giveItem(stack, p);
                Load.player(p).map.clearMapAfterUpgrading();
            }
        },

        KEEP_RARITY_AND_REROLL("reroll", Words.REROLL_MAP, Words.REROLL_MAP_DESC) {
            @Override
            public boolean canPick(Player p) {
                var map = Load.player(p).map;
                if (map.map == null) {
                    return false;
                }
                return Load.player(p).map.killed_boss;
            }

            @Override
            public void onPick(Player p) {
                var stack = SlashItems.MAP.get().getDefaultInstance();
                var map = Load.player(p).map.map;
                map.setRarityAndRerollNeeded(map.getRarity());
                map.saveToStack(stack);

                PlayerUtils.giveItem(stack, p);
                Load.player(p).map.clearMapAfterUpgrading();
            }
        };

        MapOption(String id, Words name, Words desc) {
            this.id = id;
            this.name = name;
            this.desc = desc;
        }

        public String id;
        public Words name;
        public Words desc;

        public abstract void onPick(Player p);


        public abstract boolean canPick(Player p);
    }

    MapOption option;

    public MapUpgradeCard(MapOption option) {
        this.option = option;
    }

    @Override
    public ResourceLocation getIcon() {
        return SlashRef.guiId("map_upgrade/" + option.id);
    }

    @Override
    public void onClick(Player p) {
        Packets.sendToServer(new MapUpgradePacket(option));
    }

    @Override
    public List<MutableComponent> getTooltip(Player p) {
        List<MutableComponent> list = new ArrayList<>();
        return list;
    }

    @Override
    public List<MutableComponent> getScreenText(Player p) {
        List<MutableComponent> list = new ArrayList<>();
        list.add(option.desc.locName());

        if (!option.canPick(p)) {
            list.add(Component.empty());
            list.add(Words.OPTION_LOCKED.locName().withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
        }

        return list;
    }

    @Override
    public MutableComponent getName() {
        return option.name.locName();
    }
}