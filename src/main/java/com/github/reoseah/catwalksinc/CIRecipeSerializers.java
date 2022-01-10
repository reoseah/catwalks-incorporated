package com.github.reoseah.catwalksinc;

import com.github.reoseah.catwalksinc.recipes.PaintRollerFillingRecipe;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.registry.Registry;

public class CIRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> REGISTER = DeferredRegister.create(CatwalksInc.ID, Registry.RECIPE_SERIALIZER_KEY);

    public static final RecipeSerializer<PaintRollerFillingRecipe> PAINTROLLER_FILLING = register(
            "crafting_special_paint_roller_filling", new SpecialRecipeSerializer<>(PaintRollerFillingRecipe::new));

    private static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String name, S serializer) {
        REGISTER.register(name, () -> serializer);
        return serializer;
    }
}
