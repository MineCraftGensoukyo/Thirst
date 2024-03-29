package moe.gensoukyo.thirst.event;

import moe.gensoukyo.thirst.net.Channel;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static moe.gensoukyo.thirst.Thirst.MODID;
import static moe.gensoukyo.thirst.gui.hud.WaterLevelHud.WATER_LEVEL_HUD;
import static moe.gensoukyo.thirst.register.ItemRegister.EMPTY_KETTLE;
import static moe.gensoukyo.thirst.register.ItemRegister.KETTLE;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = MODID)
public class ModEventSubscriber {
    @SubscribeEvent
    public static void addCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(EMPTY_KETTLE.get());
            event.accept(KETTLE.get());
        }
    }

    @SubscribeEvent
    public static void onCommonSetupEvent(FMLCommonSetupEvent event) {
        Channel.register();
    }

    @SubscribeEvent
    public static void addHud(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "water_level_hud", WATER_LEVEL_HUD);
    }
}
