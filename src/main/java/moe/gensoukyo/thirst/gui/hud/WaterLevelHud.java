package moe.gensoukyo.thirst.gui.hud;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

@OnlyIn(Dist.CLIENT)
public class WaterLevelHud {
    private static int waterLevel = 0;
    private static final int HIGHLIGHT_TIME = 255;
    private static int leavingHighlightTime = 0;
    public static final IGuiOverlay WATER_LEVEL_HUD = (((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        if (!gui.getMinecraft().options.hideGui && leavingHighlightTime > 1) {
            var font = gui.getMinecraft().font;
            var text = "Water Level: " + waterLevel;
            int alpha = Math.min(2 * leavingHighlightTime, 255);
            guiGraphics.drawString(font, text, 100, 100, 0xFFFFFF + (alpha << 24));
            leavingHighlightTime--;
        }
    }));

    public static void startHighlight(int level) {
        waterLevel = level;
        leavingHighlightTime = HIGHLIGHT_TIME;
    }
}
