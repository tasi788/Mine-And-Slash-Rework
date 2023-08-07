package com.robertx22.age_of_exile.database.data.currency;

import com.robertx22.age_of_exile.capability.player.container.SkillGemsMenu;
import com.robertx22.age_of_exile.database.data.currency.base.CurrencyItem;
import com.robertx22.age_of_exile.database.data.currency.base.ICurrencyItemEffect;
import com.robertx22.age_of_exile.database.data.currency.base.IShapedRecipe;
import com.robertx22.age_of_exile.database.data.currency.loc_reqs.BaseLocRequirement;
import com.robertx22.age_of_exile.database.data.currency.loc_reqs.LocReqContext;
import com.robertx22.age_of_exile.database.data.currency.loc_reqs.SimpleGearLocReq;
import com.robertx22.age_of_exile.database.data.currency.loc_reqs.item_types.GearReq;
import com.robertx22.age_of_exile.mmorpg.SlashRef;
import com.robertx22.age_of_exile.mmorpg.registers.common.items.CurrencyItems;
import com.robertx22.age_of_exile.mmorpg.registers.common.items.SlashItems;
import com.robertx22.age_of_exile.uncommon.datasaving.Load;
import com.robertx22.age_of_exile.uncommon.interfaces.data_items.IRarity;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class OrbOfTransmutationItem extends CurrencyItem implements ICurrencyItemEffect, IShapedRecipe {

    @Override
    public String GUID() {
        return "currency/orb_of_transmutation";
    }

    public static final String ID = SlashRef.MODID + ":currency/orb_of_transmutation";

    @Override
    public int getWeight() {
        return 3000;
    }

    public OrbOfTransmutationItem() {

        super(ID);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);

        if (!pLevel.isClientSide()) {
            // todo this is just for testing
            pPlayer.openMenu(new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.empty();
                }

                @Nullable
                @Override
                public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
                    return new SkillGemsMenu(Load.playerRPGData(pPlayer), pContainerId, pPlayerInventory);
                }
            });
        }
        return InteractionResultHolder.pass(pPlayer.getItemInHand(pUsedHand));

    }

    @Override
    public int getTier() {
        return 1;
    }

    @Override
    public ItemStack internalModifyMethod(LocReqContext ctx, ItemStack stack, ItemStack Currency) {

        /*
        GearItemData gear = Gear.Load(stack);
        gear.upgradeToHigherRarity();
        ItemStack result = stack.copy();
        Gear.Save(result, gear);

         */
// todo remove later
        return null; // todo
    }

    @Override
    public List<BaseLocRequirement> requirements() {
        return Arrays.asList(GearReq.INSTANCE, SimpleGearLocReq.HAS_HIGHER_RARITY, SimpleGearLocReq.IS_COMMON);
    }

    @Override
    public String getRarityRank() {
        return IRarity.COMMON_ID;
    }

    @Override
    public String locNameForLangFile() {
        return nameColor + "Orb Of Transmutation";
    }

    @Override
    public String locDescForLangFile() {
        return "Transform common item into higher rarity.";
    }

    @Override
    public ShapedRecipeBuilder getRecipe() {
        return shaped(CurrencyItems.ORB_OF_TRANSMUTATION.get())
                .define('t', Items.REDSTONE)
                .define('v', Items.COAL)
                .define('o', SlashItems.T0_DUST())
                .pattern("ovo")
                .pattern("vtv")
                .pattern("ovo")
                .unlockedBy("player_level", trigger());
    }

}