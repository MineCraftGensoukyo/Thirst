package moe.gensoukyo.thirst.register;

import moe.gensoukyo.thirst.block.CisternBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static moe.gensoukyo.thirst.Thirst.MODID;

public class BlockRegister {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

    public static final RegistryObject<Block> CISTERN = BLOCKS.register("cistern", () -> new CisternBlock(BlockBehaviour.Properties.of().noOcclusion()));
}
