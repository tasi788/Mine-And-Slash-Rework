package com.robertx22.mine_and_slash.gui.screens.map;

import com.robertx22.mine_and_slash.database.data.game_balance_config.PlayerPointsType;
import com.robertx22.mine_and_slash.database.registry.ExileDB;
import com.robertx22.mine_and_slash.gui.bases.BaseScreen;
import com.robertx22.mine_and_slash.gui.bases.IAlertScreen;
import com.robertx22.mine_and_slash.gui.bases.INamedScreen;
import com.robertx22.mine_and_slash.gui.screens.spell.PointsDisplayButton;
import com.robertx22.mine_and_slash.mmorpg.SlashRef;
import com.robertx22.mine_and_slash.uncommon.datasaving.Load;
import com.robertx22.mine_and_slash.uncommon.localization.Words;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.stream.Collectors;

public class MapScreen extends BaseScreen implements INamedScreen, IAlertScreen {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(SlashRef.MODID, "textures/gui/map/background.png");

    static int sizeX = 250;
    static int sizeY = 233;

    Minecraft mc = Minecraft.getInstance();


    public MapScreen() {
        super(sizeX, sizeY);
    }

    @Override
    public ResourceLocation iconLocation() {
        return new ResourceLocation(SlashRef.MODID, "textures/gui/main_hub/icons/spells.png");
    }

    @Override
    public Words screenName() {
        return Words.Classes;
    }

    static int SLOT_SPACING = 21;

    @Override
    public void init() {
        super.init();
        this.clearWidgets();

        try {

            var all = Load.player(mc.player).ascClass.school().stream().map(x -> ExileDB.SpellSchools().get(x)).collect(Collectors.toList());

            //    addRenderableWidget(new LeftRightButton(this, guiLeft + 100 - LeftRightButton.xSize - 5, guiTop + 25 - LeftRightButton.ySize / 2, true));
            //    addRenderableWidget(new LeftRightButton(this, guiLeft + 150 + 5, guiTop + 25 - LeftRightButton.ySize / 2, false));

            addRenderableWidget(new PointsDisplayButton(PlayerPointsType.SPELLS, guiLeft + 8, guiTop + 206));
            addRenderableWidget(new PointsDisplayButton(PlayerPointsType.PASSIVES, guiLeft + 148, guiTop + 206));


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    // so 1 guy can mixin to replace it
    public void mnsRenderBG(GuiGraphics gui) {
        gui.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        gui.blit(BACKGROUND, mc.getWindow()
                        .getGuiScaledWidth() / 2 - sizeX / 2,
                mc.getWindow()
                        .getGuiScaledHeight() / 2 - sizeY / 2, 0, 0, sizeX, sizeY
        );
    }

    @Override
    public void render(GuiGraphics gui, int x, int y, float ticks) {

        try {
            mnsRenderBG(gui);

            gui.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            // gui.blit(currentSchool().getIconLoc(), guiLeft + 107, guiTop + 8, 36, 36, 36, 36, 36, 36);

            // background
            gui.setColor(1.0F, 1.0F, 1.0F, 1.0F);

            // gui.blit(currentSchool().getBackgroundLoc(), guiLeft + 7, guiTop + 8, 93, 36, 93, 36, 93, 36);
            // gui.blit(currentSchool().getBackgroundLoc(), guiLeft + 150, guiTop + 8, 93, 36, 93, 36, 93, 36);

            super.render(gui, x, y, ticks);


            /*
            String txt = Gui.SPELL_POINTS.locName().append(String.valueOf(PlayerPointsType.SPELLS.getFreePoints(mc.player))).getString();
            GuiUtils.renderScaledText(gui, guiLeft + 50, guiTop + 215, 1, txt, ChatFormatting.WHITE);

            String tx2 = Gui.PASSIVE_POINTS.locName().append(String.valueOf(PlayerPointsType.PASSIVES.getFreePoints(mc.player))).getString();
            GuiUtils.renderScaledText(gui, guiLeft + 195, guiTop + 215, 1, tx2, ChatFormatting.WHITE);

             */
            //buttons.forEach(b -> b.renderToolTip(matrix, x, y));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean shouldAlert() {
        return false;
    }
}