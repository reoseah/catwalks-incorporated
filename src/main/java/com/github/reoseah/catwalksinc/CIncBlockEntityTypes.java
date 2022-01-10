package com.github.reoseah.catwalksinc;

import com.github.reoseah.catwalksinc.block.entity.CagedLadderBlockEntity;
import com.github.reoseah.catwalksinc.block.entity.CatwalkBlockEntity;
import com.github.reoseah.catwalksinc.block.entity.CatwalkStairsBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

public class CIncBlockEntityTypes {
    public static final BlockEntityType<CatwalkBlockEntity> CATWALK = register("catwalk", FabricBlockEntityTypeBuilder.create(CatwalkBlockEntity::new, CIncBlocks.CATWALK, CIncBlocks.YELLOW_CATWALK, CIncBlocks.RED_CATWALK).build());
    public static final BlockEntityType<CatwalkStairsBlockEntity> CATWALK_STAIRS = register("catwalk_stairs", FabricBlockEntityTypeBuilder.create(CatwalkStairsBlockEntity::new, CIncBlocks.CATWALK_STAIRS, CIncBlocks.YELLOW_CATWALK_STAIRS, CIncBlocks.RED_CATWALK_STAIRS).build());
    public static final BlockEntityType<CagedLadderBlockEntity> CAGED_LADDER = register("caged_ladder", FabricBlockEntityTypeBuilder.create(CagedLadderBlockEntity::new, CIncBlocks.CAGED_LADDER, CIncBlocks.YELLOW_CAGED_LADDER, CIncBlocks.RED_CAGED_LADDER).build());

    public static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> type) {
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, CatwalksInc.id(name), type);
    }
}
