package moe.gensoukyo.thirst.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static moe.gensoukyo.thirst.Thirst.MODID;

public class CreativeTabRegister {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<CreativeModeTab> THIRST = CREATIVE_TABS.register("thirst_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("item_group." + MODID + ".thirst_tab"))
            .icon(() -> new ItemStack(ItemRegister.CISTERN.get()))
            .displayItems((params, output) -> {
                output.accept(ItemRegister.CISTERN.get());
            })
            .build());


}
