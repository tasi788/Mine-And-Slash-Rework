package com.robertx22.age_of_exile.database.data;

import com.google.gson.JsonObject;
import com.robertx22.age_of_exile.database.data.stats.Stat;
import com.robertx22.age_of_exile.database.data.stats.name_regex.StatNameRegex;
import com.robertx22.age_of_exile.database.registry.ExileDB;
import com.robertx22.age_of_exile.saveclasses.ExactStatData;
import com.robertx22.age_of_exile.uncommon.enumclasses.ModType;
import com.robertx22.age_of_exile.uncommon.utilityclasses.TooltipUtils;
import com.robertx22.library_of_exile.registry.serialization.IByteBuf;
import com.robertx22.library_of_exile.registry.serialization.ISerializable;
import com.robertx22.library_of_exile.wrappers.ExileText;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;


public class StatMod implements ISerializable<StatMod>, IByteBuf<StatMod> {

    public float min = 0;
    public float max = 0;

    public String stat;
    public String type;

    public static StatMod EMPTY = new StatMod();

    private StatMod() {

    }

    @Override
    public StatMod getFromBuf(FriendlyByteBuf buf) {
        StatMod mod = new StatMod();
        mod.min = buf.readFloat();
        mod.max = buf.readFloat();

        mod.stat = buf.readUtf(100);
        mod.type = buf.readUtf(100);
        return mod;
    }

    @Override
    public void toBuf(FriendlyByteBuf buf) {
        buf.writeFloat(min);
        buf.writeFloat(max);

        buf.writeUtf(stat, 100);
        buf.writeUtf(type, 100);

    }

    public static StatMod percent(float firstMin, float firstMax, Stat stat) {
        StatMod mod = new StatMod();
        mod.min = firstMin;
        mod.max = firstMax;
        mod.stat = stat.GUID();
        if (!stat.IsPercent()) {
            mod.type = ModType.PERCENT.name();
        } else {
            mod.type = ModType.FLAT.name();
        }
        return mod;
    }

    public StatMod(float firstMin, float firstMax, Stat stat) {
        this.min = firstMin;
        this.max = firstMax;
        this.stat = stat.GUID();
        this.type = ModType.FLAT.name();
    }

    public StatMod(float firstMin, float firstMax, String stat, ModType type) {
        this.min = firstMin;
        this.max = firstMax;
        this.stat = stat;
        this.type = type.name();
    }

    public StatMod(float firstMin, float firstMax, Stat stat, ModType type) {

        this.min = firstMin;
        this.max = firstMax;
        this.stat = stat.GUID();
        this.type = type.name();
    }

    public StatMod(float firstMin, float firstMax, float secondMin, float secondMax, String stat, ModType type) {

        this.min = firstMin;
        this.max = firstMax;
        this.stat = stat;
        this.type = type.name();
    }

    public Stat GetStat() {
        return ExileDB.Stats()
                .get(stat);
    }

    public MutableComponent getRangeToShow(int lvl) {

        int fmin = (int) min;
        int fmax = (int) max;

        fmin = (int) GetStat().scale(getModType(), min, lvl);
        fmax = (int) GetStat().scale(getModType(), max, lvl);

        String text = fmin + "/" + fmax;

        if (GetStat().IsPercent() || getModType().isPercent()) {
            text = text + "%";
        } else if (getModType() == ModType.MORE) {
            if (fmin > 0) {
                text = text + " More";
            } else {
                text = text + " Less";

            }
        }

        return ExileText.ofText("(").get().withStyle(ChatFormatting.GREEN)
                .append(text)
                .append(")");

    }

    public ModType getModType() {
        return ModType.fromString(type);
    }

    @Override
    public JsonObject toJson() {

        JsonObject json = new JsonObject();

        json.addProperty("min", min);
        json.addProperty("max", max);

        json.addProperty("stat", stat);
        json.addProperty("type", ModType.valueOf(type).id);

        return json;
    }

    @Override
    public StatMod fromJson(JsonObject json) {

        float firstMin = json.get("min")
                .getAsFloat();
        float firstMax = json.get("max")
                .getAsFloat();

        String stat = json.get("stat")
                .getAsString();

        ModType type = ModType.fromString(json.get("type")
                .getAsString());

        return new StatMod(firstMin, firstMax, stat, type);

    }

    public List<Component> getEstimationTooltip(int lvl) {

        List<Component> list = new ArrayList<>();

        if (GetStat().is_long) {
            return TooltipUtils.cutIfTooLong(ExileText.ofText(
                    StatNameRegex.JUST_NAME.translate(ChatFormatting.GREEN, null, getModType(), min, GetStat())
            ).get());
        }

        Component txt = getRangeToShow(lvl).append(" ")
                .append(GetStat().locName());

        list.add(txt);

        return list;
    }

    public ExactStatData ToExactStat(int percent, float lvl) {
        return ExactStatData.fromStatModifier(this, percent, lvl);
    }

}