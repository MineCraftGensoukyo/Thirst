package moe.gensoukyo.thirst.item;

import moe.gensoukyo.thirst.net.Channel;
import moe.gensoukyo.thirst.net.pkg.CisternUseLocPack;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import static moe.gensoukyo.thirst.register.BlockRegister.CISTERN;
import static moe.gensoukyo.thirst.register.ItemRegister.KETTLE;

public class EmptyKettleItem extends Item {
    public EmptyKettleItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean canAttackBlock(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull Player pPlayer) {
        return false;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        BlockHitResult blockhitresult = getPlayerPOVHitResult(pLevel, pPlayer, ClipContext.Fluid.SOURCE_ONLY);
        if (blockhitresult.getType() != HitResult.Type.MISS) {
            if (blockhitresult.getType() == HitResult.Type.BLOCK) {
                BlockPos pos = blockhitresult.getBlockPos();
                // check if player could do interact
                if (!pLevel.mayInteract(pPlayer, pos)) {
                    return InteractionResultHolder.pass(itemStack);
                }
                if (pLevel.getFluidState(pos).is(FluidTags.WATER)) {
                    pLevel.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                    pLevel.gameEvent(pPlayer, GameEvent.FLUID_PICKUP, pos);
                    return InteractionResultHolder.sidedSuccess(processKettleItem(itemStack, pPlayer), pLevel.isClientSide());
                }
                if (pLevel.getBlockState(pos).is(CISTERN.get()) && pLevel.isClientSide) {
                    // 如果右键到水箱的话，根据右键的位置发送网络包
                    Vec3 hitVec = blockhitresult.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
                    // 0~0.125是第一个水位，0.125~0.375是第二个水位，0.375~0.625是第三个水位，0.625~0.875是第四个水位，0.875~1是第五个水位
                    int targetWaterLevel = (int)((hitVec.y + 0.125) * 4);
                    Channel.sendToServer(new CisternUseLocPack(pos, targetWaterLevel, pUsedHand));
                }
            }
        }
        return InteractionResultHolder.pass(itemStack);
    }

    protected ItemStack processKettleItem(ItemStack itemStack, Player player) {
        itemStack.shrink(1);
        player.addItem(new ItemStack(KETTLE.get()));
        return itemStack;
    }
}
