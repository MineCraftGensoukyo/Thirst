package moe.gensoukyo.thirst.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static moe.gensoukyo.thirst.register.ItemRegister.EMPTY_KETTLE;
import static moe.gensoukyo.thirst.register.ItemRegister.KETTLE;

public class CisternBlock extends Block {
    public static final IntegerProperty WATER_LEVEL = IntegerProperty.create("level", 0, 4);

    public CisternBlock(Properties pProperties) {
        super(pProperties.strength(1.5f));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(WATER_LEVEL);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(WATER_LEVEL, 0);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult result) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.is(KETTLE.get())) {
            int kettleCurrentDuration = itemStack.getMaxDamage() - itemStack.getDamageValue();
            int currentCisternLevel = blockState.getValue(WATER_LEVEL);
            int cisternLeavingLevel = 4 - currentCisternLevel;
            int couldInputLevel = Math.min(kettleCurrentDuration, cisternLeavingLevel);
            itemStack.setDamageValue(itemStack.getDamageValue() + couldInputLevel);
            if (itemStack.getDamageValue() == itemStack.getMaxDamage()) {
                itemStack.shrink(1);
                if (!level.isClientSide) {
                    player.addItem(new ItemStack(EMPTY_KETTLE.get()));
                }
            }
            level.setBlock(blockPos, blockState.setValue(WATER_LEVEL, currentCisternLevel + couldInputLevel), 0b0011);
        }
        return InteractionResult.SUCCESS;
    }
}
