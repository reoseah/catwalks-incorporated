package com.github.reoseah.catwalksinc.part;

import alexiil.mc.lib.multipart.api.MultipartEventBus;
import alexiil.mc.lib.multipart.api.MultipartHolder;
import alexiil.mc.lib.multipart.api.PartDefinition;
import alexiil.mc.lib.multipart.api.event.PartAddedEvent;
import alexiil.mc.lib.multipart.api.property.MultipartProperties;
import alexiil.mc.lib.net.IMsgReadCtx;
import alexiil.mc.lib.net.IMsgWriteCtx;
import alexiil.mc.lib.net.NetByteBuf;
import com.github.reoseah.catwalksinc.block.CageLampBlock;
import juuxel.blockstoparts.api.category.CategorySet;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CageLampPart extends CatwalksIncPart {
    public static final PartDefinition DEFINITION = new PartDefinition(new Identifier("catwalksinc:cage_lamp"), CageLampPart::readFromNbt, CageLampPart::loadFromBuffer);

    private final Direction facing;

    public CageLampPart(PartDefinition definition, MultipartHolder holder, Direction facing) {
        super(definition, holder);
        this.facing = facing;
    }

    private static CageLampPart readFromNbt(PartDefinition definition, MultipartHolder holder, NbtCompound nbt) {
        return new CageLampPart(definition, holder, Direction.byId(nbt.getByte("Facing")));
    }

    private static CageLampPart loadFromBuffer(PartDefinition definition, MultipartHolder holder, NetByteBuf buf, IMsgReadCtx ctx) {
        return new CageLampPart(definition, holder, Direction.byId(buf.readByte()));
    }

    @Override
    public VoxelShape getShape() {
        return CageLampBlock.SHAPES[facing.getId()];
    }

    @Override
    public ItemStack getPickStack(@Nullable BlockHitResult hitResult) {
        return new ItemStack(CageLampBlock.ITEM);
    }

    @Override
    public void onAdded(MultipartEventBus bus) {
        super.onAdded(bus);

        this.holder.getContainer().getProperties().setValue(this, MultipartProperties.LIGHT_VALUE, this.getBlockState().getLuminance());
        bus.addListener(this, PartAddedEvent.class, event -> this.holder.getContainer().getProperties().setValue(this, MultipartProperties.LIGHT_VALUE, this.getBlockState().getLuminance()));
    }

    @Override
    public BlockState getBlockState() {
        return CageLampBlock.INSTANCE.getDefaultState().with(CageLampBlock.FACING, this.facing);
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound nbt = new NbtCompound();
        nbt.putByte("Facing", (byte) this.facing.getId());
        return nbt;
    }

    @Override
    public void writeCreationData(NetByteBuf buffer, IMsgWriteCtx ctx) {
        buffer.writeByte(this.facing.getId());
    }

    @Override
    protected void addCategories(CategorySet.Builder builder) {

    }
}
