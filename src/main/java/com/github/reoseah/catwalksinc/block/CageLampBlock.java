package com.github.reoseah.catwalksinc.block;

import alexiil.mc.lib.multipart.api.MultipartContainer;
import alexiil.mc.lib.multipart.api.NativeMultipart;
import com.github.reoseah.catwalksinc.CatwalksInc;
import com.github.reoseah.catwalksinc.part.CageLampPart;
import com.google.common.collect.ImmutableList;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CageLampBlock extends WallDecorationBlock implements NativeMultipart {
    public static final VoxelShape[] SHAPES = { //
            Block.createCuboidShape(4, 6, 4, 12, 16, 12), //
            Block.createCuboidShape(4, 0, 4, 12, 10, 12), //
            Block.createCuboidShape(4, 4, 6, 12, 12, 16), //
            Block.createCuboidShape(4, 4, 0, 12, 12, 10), //
            Block.createCuboidShape(6, 4, 4, 16, 12, 12), //
            Block.createCuboidShape(0, 4, 4, 10, 12, 12), //
    };

    public static final Block INSTANCE = new CageLampBlock(FabricBlockSettings.of(Material.METAL, MapColor.GRAY).sounds(BlockSoundGroup.LANTERN).strength(2F, 10F).nonOpaque().luminance(14));
    public static final Item ITEM = new BlockItem(INSTANCE, new FabricItemSettings().group(CatwalksInc.ITEM_GROUP));

    public CageLampBlock(Settings settings) {
        super(settings);
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES[state.get(FACING).getId()];
    }

    @Nullable
    @Override
    public List<MultipartContainer.MultipartCreator> getMultipartConversion(World world, BlockPos pos, BlockState state) {
        return ImmutableList.of(holder -> new CageLampPart(CageLampPart.DEFINITION, holder, state.get(CageLampBlock.FACING)));
    }
}
