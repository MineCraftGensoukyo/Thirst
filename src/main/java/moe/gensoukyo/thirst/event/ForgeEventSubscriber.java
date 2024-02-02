package moe.gensoukyo.thirst.event;

import moe.gensoukyo.thirst.block.CisternBlock;
import moe.gensoukyo.thirst.utils.ClientHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static moe.gensoukyo.thirst.Thirst.MODID;
import static moe.gensoukyo.thirst.register.ItemRegister.EMPTY_KETTLE;
import static moe.gensoukyo.thirst.register.ItemRegister.KETTLE;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventSubscriber {
    @SubscribeEvent
    public static void fillWaterFromCistern(PlayerInteractEvent.LeftClickBlock event) {
        BlockPos pos = event.getPos();
        Level level = event.getLevel();
        BlockState blockState = level.getBlockState(pos);
        ItemStack itemStack = event.getItemStack();
        if (itemStack.is(KETTLE.get()) && blockState.getBlock() instanceof CisternBlock) {
            if (ClientHelper.clientMouseGrabbed(Minecraft.getInstance().mouseHandler, level)) {
                event.setCanceled(true);
                return;
            }
            event.setCanceled(true);
            int currentCisternLevel = blockState.getValue(CisternBlock.WATER_LEVEL);
            int kettleLeavingDuration = itemStack.getDamageValue();
            int couldOutputLevel = Math.min(kettleLeavingDuration, currentCisternLevel);
            itemStack.setDamageValue(itemStack.getDamageValue() - couldOutputLevel);
            level.setBlock(pos, blockState.setValue(CisternBlock.WATER_LEVEL, currentCisternLevel - couldOutputLevel), 0b0011);
            if (!level.isClientSide) {
                event.getEntity().swing(InteractionHand.MAIN_HAND);
            }
        } else if (itemStack.is(EMPTY_KETTLE.get()) && blockState.getBlock() instanceof CisternBlock) {
            if (ClientHelper.clientMouseGrabbed(Minecraft.getInstance().mouseHandler, level)) {
                event.setCanceled(true);
                return;
            }
            event.setCanceled(true);
            int currentCisternLevel = blockState.getValue(CisternBlock.WATER_LEVEL);
            if (currentCisternLevel == 0) {
                return;
            }
            final int MAX_KETTLE_LEVEL = 4;
            int couldOutputLevel = Math.min(MAX_KETTLE_LEVEL, currentCisternLevel);
            itemStack.shrink(1);
            if (!level.isClientSide) {
                ItemStack kettle = new ItemStack(KETTLE.get());
                kettle.setDamageValue(4 - currentCisternLevel);
                event.getEntity().addItem(kettle);
                event.getEntity().swing(InteractionHand.MAIN_HAND);
            }
            level.setBlockAndUpdate(pos, blockState.setValue(CisternBlock.WATER_LEVEL, currentCisternLevel - couldOutputLevel));
        }
    }
}
