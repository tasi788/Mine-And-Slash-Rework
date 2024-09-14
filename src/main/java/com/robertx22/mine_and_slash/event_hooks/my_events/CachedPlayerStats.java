package com.robertx22.mine_and_slash.event_hooks.my_events;

import com.robertx22.mine_and_slash.capability.DirtySync;
import com.robertx22.mine_and_slash.saveclasses.skill_gem.SkillGemData;
import com.robertx22.mine_and_slash.saveclasses.unit.stat_ctx.StatContext;
import com.robertx22.mine_and_slash.uncommon.datasaving.Load;
import com.robertx22.mine_and_slash.uncommon.stat_calculation.CommonStatUtils;
import com.robertx22.mine_and_slash.uncommon.stat_calculation.PlayerStatUtils;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class CachedPlayerStats {

    public Player p;

    public List<StatContext> statContexts = new ArrayList<>();

    // I guess these could be all stats that don't change often, fine to set these to recalc everything
    public DirtySync ALLOCATED = new DirtySync("misc_player", x -> {
        recalcAllocated();
    });

    public void tick() {

        ALLOCATED.onTickTrySync(p);
    }

    public CachedPlayerStats(Player p) {
        this.p = p;
    }

    private void recalcAllocated() {
        statContexts = new ArrayList<>();

        var playerData = Load.player(p);

        playerData.aurasOn = new ArrayList<>();
        for (SkillGemData aura : playerData.getSkillGemInventory().getAurasGems()) {
            playerData.aurasOn.add(aura.id);
        }

        statContexts.add(CommonStatUtils.addStatCompat(p));
        statContexts.addAll(PlayerStatUtils.addToolStats(p));

        statContexts.add(PlayerStatUtils.addBonusExpPerCharacters(p));

        statContexts.addAll(playerData.buff.getStatAndContext(p));

        statContexts.addAll(playerData.getSkillGemInventory().getAuraStats(p));
        statContexts.addAll(playerData.getJewels().getStatAndContext(p));
        statContexts.addAll(playerData.statPoints.getStatAndContext(p));

        statContexts.addAll(PlayerStatUtils.addNewbieElementalResists(Load.Unit(p)));
        statContexts.addAll(playerData.talents.getStatAndContext(p));
        statContexts.addAll(playerData.ascClass.getStatAndContext(p));
        statContexts.addAll(playerData.prophecy.getStatAndContext(p));

    }
}