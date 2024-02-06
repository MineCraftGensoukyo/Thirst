package moe.gensoukyo.thirst.net.pkg;

import moe.gensoukyo.thirst.block.CisternBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import static moe.gensoukyo.thirst.register.ItemRegister.EMPTY_KETTLE;
import static moe.gensoukyo.thirst.register.ItemRegister.KETTLE;

public class CisternUseLocPack {
    private final BlockPos pos;
    private final int waterLevel;
    private final InteractionHand hand;

    public CisternUseLocPack(BlockPos pos, int level, InteractionHand hand) {
        this.pos = pos;
        this.waterLevel = level;
        this.hand = hand;
    }

    public CisternUseLocPack(FriendlyByteBuf buf) {
        pos = buf.readBlockPos();
        waterLevel = buf.readInt();
        hand = buf.readEnum(InteractionHand.class);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(waterLevel);
        buf.writeEnum(hand);
    }

    @SuppressWarnings("resource")
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        ServerPlayer player = context.getSender();
        if (player != null) {
            BlockState state = player.level().getBlockState(pos);
            if (state.getBlock() instanceof CisternBlock) {
                adjustWaterLevel(player, state, waterLevel, hand);
            }
        }
        return true;
    }

    @SuppressWarnings("resource")
    private void adjustWaterLevel(ServerPlayer player, BlockState state, int targetWaterLevel, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        int cisternLevel = state.getValue(CisternBlock.WATER_LEVEL);
        int adjustAmount = targetWaterLevel - cisternLevel;
        if (stack.is(EMPTY_KETTLE.get()) && adjustAmount < 0) {
            // 空水壶只能取水
            stack.shrink(1);
            ItemStack givingKettle = new ItemStack(KETTLE.get());
            givingKettle.setDamageValue(givingKettle.getMaxDamage() + adjustAmount);
            player.addItem(givingKettle);
            player.level().setBlockAndUpdate(pos, state.setValue(CisternBlock.WATER_LEVEL, targetWaterLevel));
        }
        if (stack.is(KETTLE.get())) {
            // 假想水壶的水量
            int kettleWillBeWaterLevel = stack.getMaxDamage() - stack.getDamageValue() - adjustAmount;
            if (kettleWillBeWaterLevel <= 0) {
                // 如果假想的结果是负数，那么水壶不够水（零也可以在这里处理）
                int realAdjustAmount = stack.getMaxDamage() - stack.getDamageValue();
                player.level().setBlockAndUpdate(pos, state.setValue(CisternBlock.WATER_LEVEL, cisternLevel + realAdjustAmount));
                stack.shrink(1);
                player.addItem(new ItemStack(EMPTY_KETTLE.get()));
            } else if (kettleWillBeWaterLevel > stack.getMaxDamage()) {
                // 如果假想的结果是超出水壶的最大水量，那么水壶装不下
                int realAdjustAmount = stack.getDamageValue();
                player.level().setBlockAndUpdate(pos, state.setValue(CisternBlock.WATER_LEVEL, cisternLevel - realAdjustAmount));
                stack.setDamageValue(0);
            } else {
                // 正常情况
                player.level().setBlockAndUpdate(pos, state.setValue(CisternBlock.WATER_LEVEL, targetWaterLevel));
                stack.setDamageValue(stack.getMaxDamage() - kettleWillBeWaterLevel);
            }
        }
    }
}
