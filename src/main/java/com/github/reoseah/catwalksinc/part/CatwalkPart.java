package com.github.reoseah.catwalksinc.part;

import alexiil.mc.lib.multipart.api.AbstractPart;
import alexiil.mc.lib.multipart.api.MultipartEventBus;
import alexiil.mc.lib.multipart.api.MultipartHolder;
import alexiil.mc.lib.multipart.api.PartDefinition;
import alexiil.mc.lib.multipart.api.event.PartAddedEvent;
import alexiil.mc.lib.multipart.api.event.PartRemovedEvent;
import alexiil.mc.lib.net.*;
import com.github.reoseah.catwalksinc.CatwalksUtil;
import com.github.reoseah.catwalksinc.block.CatwalkBlock;
import com.github.reoseah.catwalksinc.item.WrenchItem;
import juuxel.blockstoparts.api.category.CategorySet;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class CatwalkPart extends CatwalksIncPart {
    public static final PartDefinition DEFINITION = new PartDefinition(new Identifier("catwalksinc:catwalk"), CatwalkPart::readFromNbt, CatwalkPart::loadFromBuffer);
    public static final ParentNetIdSingle<CatwalkPart> CATWALK_NET_ID = NET_ID.subType(CatwalkPart.class, "catwalksinc:catwalk_update");
    public static final NetIdDataK<CatwalkPart> CATWALK_DATA = CATWALK_NET_ID.idData("catwalk_update").setReceiver(CatwalkPart::updateConnections);

    protected Map<Direction, ConnectionOverride> overrides = new EnumMap<>(Direction.class);
    protected boolean north, west, south, east;

    public CatwalkPart(PartDefinition definition, MultipartHolder holder) {
        super(definition, holder);
    }

    public CatwalkPart(PartDefinition definition, MultipartHolder holder, boolean north, boolean west, boolean south, boolean east) {
        super(definition, holder);
        this.north = north;
        this.west = west;
        this.south = south;
        this.east = east;
    }

    private static CatwalkPart readFromNbt(PartDefinition definition, MultipartHolder holder, NbtCompound nbt) {
        CatwalkPart catwalk = new CatwalkPart(definition, holder);
        if (nbt.contains("Overrides", NbtElement.COMPOUND_TYPE)) {
            NbtCompound overrides = nbt.getCompound("Overrides");
            for (Direction direction : Direction.Type.HORIZONTAL) {
                if (overrides.contains(direction.asString(), NbtElement.STRING_TYPE)) {
                    catwalk.overrides.put(direction, ConnectionOverride.valueOf(overrides.getString(direction.asString())));
                }
            }
        }
        catwalk.north = nbt.getBoolean("HandrailNorth");
        catwalk.west = nbt.getBoolean("HandrailWest");
        catwalk.south = nbt.getBoolean("HandrailSouth");
        catwalk.east = nbt.getBoolean("HandrailEast");
        return catwalk;
    }

    private static CatwalkPart loadFromBuffer(PartDefinition definition, MultipartHolder holder, NetByteBuf buf, IMsgReadCtx ctx) {
        CatwalkPart catwalk = new CatwalkPart(definition, holder);
        int overridesCount = buf.readByte();
        for (int i = 0; i < overridesCount; i++) {
            Direction direction = Direction.fromHorizontal(buf.readByte());
            ConnectionOverride value = ConnectionOverride.values()[MathHelper.clamp(buf.readByte(), 0, ConnectionOverride.values().length)];
            catwalk.overrides.put(direction, value);
        }
        catwalk.north = buf.readBoolean();
        catwalk.west = buf.readBoolean();
        catwalk.south = buf.readBoolean();
        catwalk.east = buf.readBoolean();
        return catwalk;
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound nbt = new NbtCompound();
        if (!this.overrides.isEmpty()) {
            NbtCompound overridesNbt = new NbtCompound();
            for (Map.Entry<Direction, ConnectionOverride> entry : this.overrides.entrySet()) {
                overridesNbt.putString(entry.getKey().asString(), entry.getValue().name());
            }
            nbt.put("Overrides", overridesNbt);
        }
        nbt.putBoolean("HandrailNorth", this.north);
        nbt.putBoolean("HandrailWest", this.west);
        nbt.putBoolean("HandrailSouth", this.south);
        nbt.putBoolean("HandrailEast", this.east);
        return nbt;
    }

    @Override
    public void writeCreationData(NetByteBuf buffer, IMsgWriteCtx ctx) {
        buffer.writeByte(this.overrides.size());
        for (Map.Entry<Direction, ConnectionOverride> entry : this.overrides.entrySet()) {
            buffer.writeByte(entry.getKey().getHorizontal());
            buffer.writeByte(entry.getValue().ordinal());
        }
        buffer.writeBoolean(this.north);
        buffer.writeBoolean(this.west);
        buffer.writeBoolean(this.south);
        buffer.writeBoolean(this.east);
    }

    @Override
    protected void addCategories(CategorySet.Builder builder) {

    }

    @Override
    public BlockState getBlockState() {
        return CatwalkBlock.INSTANCE.getDefaultState() //
                .with(CatwalkBlock.NORTH, this.north) //
                .with(CatwalkBlock.WEST, this.west) //
                .with(CatwalkBlock.SOUTH, this.south) //
                .with(CatwalkBlock.EAST, this.east);
    }

    @Override
    public VoxelShape getShape() {
        return CatwalkBlock.FLOOR_SHAPE;
    }

    @Override
    public VoxelShape getCullingShape() {
        return VoxelShapes.empty();
    }

    @Override
    public VoxelShape getCollisionShape() {
        return CatwalkBlock.COLLISION_SHAPES[CatwalkBlock.getShapeIndex(this.south, this.west, this.north, this.east)];
    }

    @Override
    public VoxelShape getOutlineShape() {
        return CatwalkBlock.OUTLINE_SHAPES[CatwalkBlock.getShapeIndex(this.south, this.west, this.north, this.east)];
    }

    @Override
    public ItemStack getPickStack(@Nullable BlockHitResult hitResult) {
        return new ItemStack(CatwalkBlock.ITEM);
    }

    @Override
    public void onAdded(MultipartEventBus bus) {
        super.onAdded(bus);
        bus.addListener(this, PartAddedEvent.class, event -> {
            if (event.part != this) {
                for (Direction side : Direction.Type.HORIZONTAL) {
                    this.updateSide(side);
                }
                this.holder.getContainer().sendNetworkUpdate(this, CATWALK_DATA, (obj, buf, ctx) -> {
                    writeUpdatePacket(buf);
                });
            }
        });
        bus.addListener(this, PartRemovedEvent.class, event -> {
            this.holder.getContainer().recalculateShape();
            for (Direction side : Direction.Type.HORIZONTAL) {
                this.updateSide(side);
            }
            this.holder.getContainer().sendNetworkUpdate(this, CATWALK_DATA, (obj, buf, ctx) -> {
                writeUpdatePacket(buf);
            });
        });
    }

    private void writeUpdatePacket(NetByteBuf buffer) {
        buffer.writeByte(this.overrides.size());
        for (Map.Entry<Direction, ConnectionOverride> entry : this.overrides.entrySet()) {
            buffer.writeByte(entry.getKey().getHorizontal());
            buffer.writeByte(entry.getValue().ordinal());
        }
        buffer.writeBoolean(this.north);
        buffer.writeBoolean(this.east);
        buffer.writeBoolean(this.south);
        buffer.writeBoolean(this.west);
    }

    private void updateSide(Direction side) {
        if (this.isBlocked(side)) {
            this.setHandrail(side, false);
            this.overrides.remove(side);
            this.updateListeners();
        } else if (this.overrides.containsKey(side)) {
            this.setHandrail(side, this.overrides.get(side) == ConnectionOverride.FORCED);
        } else {
            this.setHandrail(side, CatwalkBlock.shouldHaveHandrail(this.getWorld(), this.getPos(), side));
        }
        this.container.recalculateShape();
        if (!this.getWorld().isClient) {
            this.updateListeners();
            this.holder.getContainer().sendNetworkUpdate(this, CATWALK_DATA, (obj, buf, ctx) -> {
                writeUpdatePacket(buf);
            });
        }
    }

    @Override
    protected void onNeighborUpdate(BlockPos neighborPos) {
        Direction side = CatwalksUtil.compare(this.getPos(), neighborPos);
        if (side.getAxis().isHorizontal()) {
            this.updateSide(side);
        }
    }

    public boolean isBlocked(Direction direction) {
        BlockPos pos = this.getPos();
        VoxelShape shape = getHandrailShape(direction).offset(pos.getX(), pos.getY(), pos.getZ());
        for (AbstractPart part : this.holder.getContainer().getAllParts()) {
            if (part == this || part.canOverlapWith(this)) {
                continue;
            }
            if (VoxelShapes.matchesAnywhere(shape, part.getShape(), BooleanBiFunction.AND)) {
                return true;
            }
        }
        return false;
    }

    public static VoxelShape getHandrailShape(Direction side) {
        return switch (side) {
            case NORTH -> CatwalkBlock.NORTH_HANDRAIL_SHAPE;
            case WEST -> CatwalkBlock.WEST_HANDRAIL_SHAPE;
            case SOUTH -> CatwalkBlock.SOUTH_HANDRAIL_SHAPE;
            case EAST -> CatwalkBlock.EAST_HANDRAIL_SHAPE;
            default -> VoxelShapes.empty();
        };
    }

    public void setHandrail(Direction side, boolean hasHandrail) {
        switch (side) {
            case NORTH -> this.north = hasHandrail;
            case WEST -> this.west = hasHandrail;
            case SOUTH -> this.south = hasHandrail;
            case EAST -> this.east = hasHandrail;
        }
        this.container.recalculateShape();
    }

    @Override
    public ActionResult onUse(PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.isIn(WrenchItem.COMPATIBILITY_TAG)) {
            if (!this.getWorld().isClient) {
                Direction side = CatwalksUtil.getTargetedQuarter(this.getPos(), hit.getPos());
                if (this.isBlocked(side)) {
                    player.sendMessage(Text.translatable("misc.catwalksinc.blocked_by_another_multipart"), true);
                    return ActionResult.FAIL;
                }
                ConnectionOverride override = ConnectionOverride.cycle(this.overrides.get(side));
                if (override == null) {
                    this.overrides.remove(side);
                    player.sendMessage(ConnectionOverride.DEFAULT_TEXT, true);
                } else {
                    this.overrides.put(side, override);
                    player.sendMessage(override.text, true);
                }

                if (stack.isDamageable()) {
                    stack.damage(1, player, p -> p.sendToolBreakStatus(hand));
                }
                this.updateSide(side);
                this.holder.getContainer().sendNetworkUpdate(this, CATWALK_DATA, (obj, buf, ctx) -> {
                    writeUpdatePacket(buf);
                });
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    private void updateConnections(NetByteBuf buffer, IMsgReadCtx ctx) {
        int overridesCount = buffer.readByte();
        for (int i = 0; i < overridesCount; i++) {
            Direction direction = Direction.fromHorizontal(buffer.readByte());
            ConnectionOverride value = ConnectionOverride.values()[MathHelper.clamp(buffer.readByte(), 0, ConnectionOverride.values().length)];
            this.overrides.put(direction, value);
        }
        this.north = buffer.readBoolean();
        this.east = buffer.readBoolean();
        this.south = buffer.readBoolean();
        this.west = buffer.readBoolean();
        this.holder.getContainer().recalculateShape();
        this.holder.getContainer().redrawIfChanged();
    }
}
