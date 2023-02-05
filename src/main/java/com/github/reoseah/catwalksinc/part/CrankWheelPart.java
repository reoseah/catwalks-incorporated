package com.github.reoseah.catwalksinc.part;

import alexiil.mc.lib.multipart.api.MultipartEventBus;
import alexiil.mc.lib.multipart.api.MultipartHolder;
import alexiil.mc.lib.multipart.api.PartDefinition;
import alexiil.mc.lib.multipart.api.property.MultipartProperties;
import alexiil.mc.lib.multipart.api.property.MultipartPropertyContainer;
import alexiil.mc.lib.multipart.api.render.PartModelKey;
import alexiil.mc.lib.net.IMsgReadCtx;
import alexiil.mc.lib.net.IMsgWriteCtx;
import alexiil.mc.lib.net.NetByteBuf;
import com.github.reoseah.catwalksinc.CatwalksUtil;
import com.github.reoseah.catwalksinc.block.Side;
import com.github.reoseah.catwalksinc.block.CrankWheelBlock;
import juuxel.blockstoparts.api.model.StaticVanillaModelKey;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CrankWheelPart extends CatwalksIncPart {
    public static final PartDefinition DEFINITION = new PartDefinition(new Identifier("catwalksinc:crank_wheel"), CrankWheelPart::readFromNbt, CrankWheelPart::loadFromBuffer);

    private final Direction facing;
    private int rotation;

    public CrankWheelPart(PartDefinition definition, MultipartHolder holder, Direction facing, int rotation) {
        super(definition, holder);
        this.facing = facing;
        this.rotation = rotation;
    }

    private static CrankWheelPart readFromNbt(PartDefinition definition, MultipartHolder holder, NbtCompound nbt) {
        return new CrankWheelPart(definition, holder, Direction.byId(nbt.getByte("Facing")), nbt.getByte("Rotation"));
    }

    private static CrankWheelPart loadFromBuffer(PartDefinition definition, MultipartHolder holder, NetByteBuf buf, IMsgReadCtx ctx) {
        return new CrankWheelPart(definition, holder, Direction.byId(buf.readByte()), buf.readByte());
    }

    @Override
    public VoxelShape getShape() {
        return CrankWheelBlock.SHAPES[facing.getId()];
    }

    @Override
    public ItemStack getPickStack(@Nullable BlockHitResult hitResult) {
        return new ItemStack(CrankWheelBlock.ITEM);
    }

    @Override
    public PartModelKey getModelKey() {
        return new StaticVanillaModelKey(this.getBlockState());
    }

    @Override
    public BlockState getBlockState() {
        return CrankWheelBlock.INSTANCE.getDefaultState().with(CrankWheelBlock.FACING, this.facing).with(CrankWheelBlock.ROTATION, this.rotation);
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound nbt = new NbtCompound();
        nbt.putByte("Facing", (byte) this.facing.getId());
        nbt.putByte("Rotation", (byte) this.rotation);
        return nbt;
    }

    @Override
    public void writeCreationData(NetByteBuf buffer, IMsgWriteCtx ctx) {
        buffer.writeByte(this.facing.getId());
        buffer.writeByte(this.rotation);
    }

    @Override
    public void onAdded(MultipartEventBus bus) {
        super.onAdded(bus);
        this.updateRedstoneLevels();
        MultipartPropertyContainer props = this.holder.getContainer().getProperties();
        props.setValue(this, MultipartProperties.CAN_EMIT_REDSTONE, true);
    }

    protected void updateRedstoneLevels() {
        MultipartPropertyContainer props = this.holder.getContainer().getProperties();
        props.setValue(this, MultipartProperties.getStrongRedstonePower(this.facing.getOpposite()), this.rotation);
        for (Direction direction : Direction.values()) {
            props.setValue(this, MultipartProperties.getWeakRedstonePower(direction), this.rotation);
        }
    }

    @Override
    public ActionResult onUse(PlayerEntity player, Hand hand, BlockHitResult hit) {
        Direction facing = this.facing;
        if (facing.getAxis() == Direction.Axis.Y) {
            facing = player.getHorizontalFacing().getOpposite();
        }
        Side side = CatwalksUtil.getTargettedSide(this.getPos(), hit.getPos(), facing);
        int rotation = this.rotation;
        int newRotation = side == Side.RIGHT ? Math.min(15, rotation + 1) : Math.max(0, rotation - 1);

        if (this.getWorld().isClient) {
            if (newRotation != 0 && newRotation != rotation) {
                CrankWheelBlock.spawnParticles(this.facing.getOpposite(), this.getWorld(), this.getPos());
            }
        }

        this.rotation = newRotation;
        if (!this.getWorld().isClient) {
            this.updateRedstoneLevels();
        }
        this.updateListeners();


        return ActionResult.SUCCESS;
    }
}
