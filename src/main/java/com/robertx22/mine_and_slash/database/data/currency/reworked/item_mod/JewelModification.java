package com.robertx22.mine_and_slash.database.data.currency.reworked.item_mod;

import com.robertx22.mine_and_slash.itemstack.ExileStack;
import com.robertx22.mine_and_slash.itemstack.StackKeys;

public abstract class JewelModification extends ItemModification {

    public JewelModification(String serializer, String id) {
        super(serializer, id);
    }

    public abstract void modifyJewel(ExileStack data);

    @Override
    public void applyINTERNAL(ExileStack stack, ItemModificationResult r) {
        var data = stack.get(StackKeys.JEWEL).get();

        if (data != null) {
            modifyJewel(stack);
        }
    }

}
