package com.robertx22.mine_and_slash.uncommon.effectdatas.rework.components;

import com.robertx22.mine_and_slash.uncommon.enumclasses.Elements;

public class ElementEventComponent extends EventComponent {

    public Elements element = Elements.Physical;

    public ElementEventComponent(Elements element) {
        this.element = element;
    }

    @Override
    public String GUID() {
        return EventComponents.ELEMENT_ID;
    }
}
