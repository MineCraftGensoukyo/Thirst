package moe.gensoukyo.thirst;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import static moe.gensoukyo.thirst.register.BlockRegister.BLOCKS;
import static moe.gensoukyo.thirst.register.CreativeTabRegister.CREATIVE_TABS;
import static moe.gensoukyo.thirst.register.ItemRegister.ITEMS;

@Mod(Thirst.MODID)
public class Thirst {
    public static final String MODID = "thirst";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Thirst() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        BLOCKS.register(bus);
        CREATIVE_TABS.register(bus);
    }
}
