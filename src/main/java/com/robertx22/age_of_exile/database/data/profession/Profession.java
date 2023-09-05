package com.robertx22.age_of_exile.database.data.profession;

import com.robertx22.age_of_exile.database.registry.ExileRegistryTypes;
import com.robertx22.age_of_exile.mmorpg.SlashRef;
import com.robertx22.age_of_exile.uncommon.MathHelper;
import com.robertx22.age_of_exile.uncommon.datasaving.Load;
import com.robertx22.age_of_exile.uncommon.interfaces.IAutoLocName;
import com.robertx22.age_of_exile.uncommon.utilityclasses.LevelUtils;
import com.robertx22.age_of_exile.uncommon.utilityclasses.StringUTIL;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.library_of_exile.registry.IAutoGson;
import com.robertx22.library_of_exile.registry.IWeighted;
import com.robertx22.library_of_exile.registry.JsonExileRegistry;
import com.robertx22.library_of_exile.utils.RandomUtils;
import com.robertx22.library_of_exile.vanilla_util.main.VanillaUTIL;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Profession implements JsonExileRegistry<Profession>, IAutoGson<Profession>, IAutoLocName {
    public static Profession SERIALIZER = new Profession();

    public String id = "";

    // blocks/entities that give exp

    public ExpSources exp_sources = new ExpSources();

    public List<ChancedDrop> chance_drops = new ArrayList<>();


    public List<ItemStack> getAllDrops(int lvl, int recipelvl, float dropChanceMulti) {
        List<ItemStack> list = new ArrayList<>();

        ProfessionRecipe.RecipeDifficulty diff = ProfessionRecipe.RecipeDifficulty.get(lvl, recipelvl);

        float lvlmulti = LevelUtils.getMaxLevelMultiplier(lvl);

        for (ChancedDrop chancedDrop : this.chance_drops) {
            float chance = dropChanceMulti * chancedDrop.chance;

            if (RandomUtils.roll(chance)) {
                ProfessionDrop drop = RandomUtils.weightedRandom(chancedDrop.drops.stream().filter(x -> lvlmulti >= x.min_lvl).collect(Collectors.toList()));
                if (drop != null) {
                    ItemStack stack = drop.get();
                    list.add(stack);
                }
            }
        }

        if (RandomUtils.roll(diff.doubleDropChance)) {
            for (ItemStack stack : list) {
                stack.setCount(MathHelper.clamp(stack.getCount() * 2, 1, stack.getMaxStackSize()));
            }
        }
        return list;
    }


    public static class ChancedDrop {

        public List<ProfessionDrop> drops = new ArrayList<>();

        public float chance = 0;

        public ChancedDrop(List<ProfessionDrop> drops, float chance) {
            this.drops = drops;
            this.chance = chance;
        }
    }

    public static class ProfessionDrop implements IWeighted {

        public String item_id = "";
        public int num = 1;
        public int weight = 1000;
        public float min_lvl = 0;


        public ProfessionDrop(String item_id, int num, int weight, float min_lvl) {
            this.item_id = item_id;
            this.num = num;
            this.weight = weight;
            this.min_lvl = min_lvl;
        }

        public ItemStack get() {
            return new ItemStack(VanillaUTIL.REGISTRY.items().get(new ResourceLocation(item_id)), num);
        }

        @Override
        public int Weight() {
            return weight;
        }
    }

    @Override
    public AutoLocGroup locNameGroup() {
        return AutoLocGroup.Misc;
    }

    @Override
    public String locNameLangFileGUID() {
        return SlashRef.MODID + ".profession." + GUID();
    }

    @Override
    public String locNameForLangFile() {
        return StringUTIL.capitalise(id);
    }

    public enum Type {
        BLOCK, ENTITY, OTHER
    }


    public void onKill(Player p, Entity en) {

        ExpSources.ExpData data = this.exp_sources.getData(en.getType());

        if (data.exp > 0) {
            data.giveExp(p, this);
        }

    }


    public List<ItemStack> onMineGetBonusDrops(Player p, List<ItemStack> drops, BlockState state) {
        var data = this.exp_sources.getData(state.getBlock());

        if (data == null) {
            for (TagKey<Block> drop : state.getTags().collect(Collectors.toList())) {
                if (exp_sources.getData(drop) != null) {
                    data = exp_sources.getData(drop);
                    break;
                }
            }
        }
        if (data == null) {
            for (ItemStack drop : drops) {
                if (exp_sources.getData(drop.getItem()) != null) {
                    data = exp_sources.getData(drop.getItem());
                    break;
                }
            }
        }

        if (data != null) {
            if (data.exp > 0) {
                data.giveExp(p, this);

                float chance = data.getLootChanceMulti(p, this);
                return this.getAllDrops(Load.player(p).professions.getLevel(this.GUID()), data.getLevelOfMastery(), chance);
            }
        }
        return Arrays.asList();

    }


    @Override
    public ExileRegistryType getExileRegistryType() {
        return ExileRegistryTypes.PROFESSION;
    }

    @Override
    public String GUID() {
        return id;
    }

    @Override
    public int Weight() {
        return 1000;
    }

    @Override
    public Class<Profession> getClassForSerialization() {
        return Profession.class;
    }
}
