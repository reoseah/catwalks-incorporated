package com.github.reoseah.catwalksinc;

import com.github.reoseah.catwalksinc.block.entity.CagedLadderBlockEntity;
import com.github.reoseah.catwalksinc.block.entity.CatwalkBlockEntity;
import com.github.reoseah.catwalksinc.block.entity.CatwalkStairsBlockEntity;
import com.google.common.collect.ImmutableSet;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

public class CIncBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(CatwalksInc.ID, Registry.BLOCK_ENTITY_TYPE_KEY);

    public static final BlockEntityType<CatwalkBlockEntity> CATWALK = register("catwalk",
            new BlockEntityType<>(CatwalkBlockEntity::new, ImmutableSet.of(CIncBlocks.CATWALK, CIncBlocks.YELLOW_CATWALK, CIncBlocks.RED_CATWALK), null));
    public static final BlockEntityType<CatwalkStairsBlockEntity> CATWALK_STAIRS = register("catwalk_stairs",
            new BlockEntityType<>(CatwalkStairsBlockEntity::new, ImmutableSet.of(CIncBlocks.CATWALK_STAIRS, CIncBlocks.YELLOW_CATWALK_STAIRS, CIncBlocks.RED_CATWALK_STAIRS), null));
    public static final BlockEntityType<CagedLadderBlockEntity> CAGED_LADDER = register("caged_ladder",
            new BlockEntityType<>(CagedLadderBlockEntity::new, ImmutableSet.of(CIncBlocks.CAGED_LADDER, CIncBlocks.YELLOW_CAGED_LADDER, CIncBlocks.RED_CAGED_LADDER), null));

    public static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> type) {
        REGISTER.register(name, () -> type);
        return type;
    }
}
