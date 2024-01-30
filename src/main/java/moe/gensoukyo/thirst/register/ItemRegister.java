package moe.gensoukyo.thirst.register;

import moe.gensoukyo.thirst.item.EmptyKettleItem;
import moe.gensoukyo.thirst.item.KettleItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static moe.gensoukyo.thirst.Thirst.MODID;

public class ItemRegister {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> EMPTY_KETTLE = ITEMS.register("empty_kettle", () -> new EmptyKettleItem(new Item.Properties()));
    public static final RegistryObject<Item> KETTLE = ITEMS.register("kettle", () -> new KettleItem(new Item.Properties()));

    public static final RegistryObject<Item> CISTERN = ITEMS.register("cistern", () -> new BlockItem(BlockRegister.CISTERN.get(), new Item.Properties()));
}
