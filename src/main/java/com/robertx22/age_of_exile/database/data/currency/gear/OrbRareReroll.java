package com.robertx22.age_of_exile.database.data.currency.gear;

import com.robertx22.age_of_exile.database.data.currency.base.GearCurrency;
import com.robertx22.age_of_exile.database.data.currency.base.GearOutcome;
import com.robertx22.age_of_exile.database.data.currency.loc_reqs.LocReqContext;
import com.robertx22.age_of_exile.database.data.profession.ExplainedResult;
import com.robertx22.age_of_exile.saveclasses.gearitem.gear_parts.AffixData;
import com.robertx22.age_of_exile.saveclasses.item_classes.GearItemData;
import com.robertx22.age_of_exile.uncommon.datasaving.StackSaving;
import com.robertx22.age_of_exile.uncommon.interfaces.data_items.IRarity;
import com.robertx22.age_of_exile.uncommon.localization.Chats;
import com.robertx22.age_of_exile.uncommon.localization.Words;
import com.robertx22.library_of_exile.utils.RandomUtils;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class OrbRareReroll extends GearCurrency {
    @Override
    public List<GearOutcome> getOutcomes() {

        return Arrays.asList(
                new GearOutcome() {
                    @Override
                    public Words getName() {
                        return Words.RerollAffix;
                    }

                    @Override
                    public OutcomeType getOutcomeType() {
                        return OutcomeType.GOOD;
                    }

                    @Override
                    public ItemStack modify(LocReqContext ctx, GearItemData gear, ItemStack stack) {
                        gear.affixes.randomize(gear);
                        StackSaving.GEARS.saveTo(stack, gear);
                        return stack;
                    }

                    @Override
                    public int Weight() {
                        return 1000;
                    }
                },
                new GearOutcome() {
                    @Override
                    public Words getName() {
                        return Words.Nothing;
                    }

                    @Override
                    public OutcomeType getOutcomeType() {
                        return OutcomeType.BAD;
                    }

                    @Override
                    public ItemStack modify(LocReqContext ctx, GearItemData gear, ItemStack stack) {
                        return stack;
                    }

                    @Override
                    public int Weight() {
                        return 1000;
                    }
                }

        );
    }


    @Override
    public int getPotentialLoss() {
        return 20;
    }

    @Override
    public ExplainedResult canBeModified(GearItemData data) {
        if (!Objects.equals(data.getRarity().guid, IRarity.COMMON_ID)
                && !Objects.equals(data.getRarity().guid, IRarity.UNCOMMON)
                && !Objects.equals(data.getRarity().guid, IRarity.RARE_ID)) {
            return ExplainedResult.failure(Chats.NEEDS_RARE_RARITY.locName());
        }
        return ExplainedResult.success();
    }

    @Override
    public String locDescForLangFile() {
        return "Has a chance of rerolling up to Rare gear with new affixes.";
    }

    @Override
    public String locNameForLangFile() {
        return "Orb of Disorder";
    }

    @Override
    public String GUID() {
        return "affix_rare_reroll";
    }

    @Override
    public int Weight() {
        return Weights.RARE;
    }
}
