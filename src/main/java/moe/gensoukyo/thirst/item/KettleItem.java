package moe.gensoukyo.thirst.item;

import moe.gensoukyo.thirst.block.CisternBlock;
import moe.gensoukyo.thirst.net.Channel;
import moe.gensoukyo.thirst.net.pkg.CisternUseLocPack;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import static moe.gensoukyo.thirst.register.ItemRegister.EMPTY_KETTLE;

public class KettleItem extends Item {
    public KettleItem(Properties pProperties) {
        super(pProperties.defaultDurability(4));
    }
    private static final int DRINKING_DURATION = 32;

    @Override
    public boolean canAttackBlock(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull Player pPlayer) {
        return false;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        BlockHitResult blockhitresult = getPlayerPOVHitResult(pLevel, pPlayer, ClipContext.Fluid.SOURCE_ONLY);
        if (blockhitresult.getType() == HitResult.Type.MISS) {
            return ItemUtils.startUsingInstantly(pLevel, pPlayer, pUsedHand);
        } else {
            if (blockhitresult.getType() == HitResult.Type.BLOCK) {
                BlockPos pos = blockhitresult.getBlockPos();
                // 检查玩家是否被允许与方块交互
                if (!pLevel.mayInteract(pPlayer, pos)) {
                    return InteractionResultHolder.pass(itemStack);
                }
                if (pLevel.getFluidState(pos).is(FluidTags.WATER)) {
                    // 如果右键到水源的话，就把水瓶填满
                    if (!pLevel.isClientSide) {
                        pLevel.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                        pLevel.gameEvent(pPlayer, GameEvent.FLUID_PICKUP, pos);
                        itemStack.setDamageValue(0);
                        return InteractionResultHolder.success(itemStack);
                    }
                    return InteractionResultHolder.consume(itemStack);
                } else if (pLevel.getBlockState(pos).getBlock() instanceof CisternBlock && pLevel.isClientSide) {
                    // 如果右键到水箱的话，根据右键的位置发送网络包
                    Vec3 hitVec = blockhitresult.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
                    // 0~0.125是第一个水位，0.125~0.375是第二个水位，0.375~0.625是第三个水位，0.625~0.875是第四个水位，0.875~1是第五个水位
                    int targetWaterLevel = (int)((hitVec.y + 0.125) * 4);
                    Channel.sendToServer(new CisternUseLocPack(pos, targetWaterLevel, pUsedHand));
                }
                else {
                    return ItemUtils.startUsingInstantly(pLevel, pPlayer, pUsedHand);
                }
            }
            return InteractionResultHolder.pass(itemStack);
        }
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pLivingEntity) {
        Player player = pLivingEntity instanceof Player ? (Player)pLivingEntity : null;
        if (!pLevel.isClientSide && player != null) {
            return shrinkWater(pStack, player);
        }
        return pStack;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack pStack) {
        return DRINKING_DURATION;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack pStack) {
        return UseAnim.DRINK;
    }

    protected ItemStack shrinkWater(ItemStack itemStack, Player player) {
        itemStack.setDamageValue(itemStack.getDamageValue() + 1);
        if (itemStack.getDamageValue() >= itemStack.getMaxDamage()) {
            itemStack.shrink(1);
            player.addItem(new ItemStack(EMPTY_KETTLE.get()));
        }
        return itemStack;
    }
}
