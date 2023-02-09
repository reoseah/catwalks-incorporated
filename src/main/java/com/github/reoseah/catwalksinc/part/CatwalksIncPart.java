package com.github.reoseah.catwalksinc.part;

import alexiil.mc.lib.multipart.api.*;
import alexiil.mc.lib.multipart.api.event.NeighbourStateUpdateEvent;
import alexiil.mc.lib.multipart.api.render.PartModelKey;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class CatwalksIncPart extends AbstractPart {
    public CatwalksIncPart(PartDefinition definition, MultipartHolder holder) {
        super(definition, holder);
    }

    protected World getWorld() {
        return this.holder.getContainer().getMultipartWorld();
    }

    protected BlockPos getPos() {
        return this.holder.getContainer().getMultipartPos();
    }

    protected void updateListeners() {
        BlockPos pos = this.getPos();
        BlockState multipartState = this.getWorld().getBlockState(pos);
        this.getWorld().updateListeners(this.getPos(), multipartState, multipartState, 3);
    }

    @Override
    public void onAdded(MultipartEventBus bus) {
        bus.addListener(this, NeighbourStateUpdateEvent.class, event -> {
            MultipartContainer container = this.holder.getContainer();
            World world = container.getMultipartWorld();
            if (!world.isClient) {
                BlockPos pos = container.getMultipartPos();
                if (!this.getClosestBlockState().canPlaceAt(world, pos)) {
                    this.breakPart();
                }
            }

            this.onNeighborUpdate(event.pos);
        });
    }

    protected void breakPart() {
        this.holder.remove(MultipartHolder.PartRemoval.DROP_ITEMS, MultipartHolder.PartRemoval.BREAK_PARTICLES, MultipartHolder.PartRemoval.BREAK_SOUND);
    }

    protected void onNeighborUpdate(BlockPos neighborPos) {
    }

    @Override
    public PartModelKey getModelKey() {
        return new BlockModelKey(this.getClosestBlockState());
    }

    @Override
    public ItemStack getPickStack(@Nullable BlockHitResult hitResult) {
        return new ItemStack(this.getClosestBlockState().getBlock());
    }

    @Override
    public void addDrops(ItemDropTarget target, LootContext context) {
        target.dropAll(this.getClosestBlockState().getDroppedStacks(new LootContext.Builder(context.getWorld()) //
                .random(context.getRandom()) //
                .luck(context.getLuck()) //
                .parameter(LootContextParameters.BLOCK_STATE, context.get(LootContextParameters.BLOCK_STATE)) //
                .parameter(LootContextParameters.ORIGIN, context.get(LootContextParameters.ORIGIN)) //
                .parameter(LootContextParameters.TOOL, context.get(LootContextParameters.TOOL)) //
                .optionalParameter(LootContextParameters.THIS_ENTITY, context.get(LootContextParameters.THIS_ENTITY)) //
                .optionalParameter(LootContextParameters.BLOCK_ENTITY, context.get(LootContextParameters.BLOCK_ENTITY)) //
                .optionalParameter(LootContextParameters.EXPLOSION_RADIUS, context.get(LootContextParameters.EXPLOSION_RADIUS))));
    }
}
