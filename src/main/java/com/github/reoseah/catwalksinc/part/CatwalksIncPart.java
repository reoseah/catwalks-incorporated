package com.github.reoseah.catwalksinc.part;

import alexiil.mc.lib.multipart.api.MultipartContainer;
import alexiil.mc.lib.multipart.api.MultipartEventBus;
import alexiil.mc.lib.multipart.api.MultipartHolder;
import alexiil.mc.lib.multipart.api.PartDefinition;
import alexiil.mc.lib.multipart.api.event.NeighbourStateUpdateEvent;
import alexiil.mc.lib.multipart.api.render.PartModelKey;
import juuxel.blockstoparts.api.category.CategorySet;
import juuxel.blockstoparts.api.model.StaticVanillaModelKey;
import juuxel.blockstoparts.api.part.BasePart;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

public abstract class CatwalksIncPart extends BasePart {

    public CatwalksIncPart(PartDefinition definition, MultipartHolder holder) {
        super(definition, holder);
    }

    protected final void updateListeners() {
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
                if (!this.getBlockState().canPlaceAt(world, pos)) {
                    this.breakPart();
                }
            }

            this.onNeighborUpdate(event.pos);
        });
    }

    protected void onNeighborUpdate(BlockPos neighborPos) {
    }

    @Override
    public VoxelShape getCullingShape() {
        return this.getOutlineShape();
    }

    @Override
    public final CategorySet getCategories() {
        return CategorySet.EMPTY;
    }

    @Override
    public PartModelKey getModelKey() {
        return new StaticVanillaModelKey(this.getBlockState());
    }
}
