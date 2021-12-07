package com.github.reoseah.catwalksinc;

import com.github.reoseah.catwalksinc.recipes.PaintRollerFillingRecipe;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.registry.Registry;

public class CIRecipeSerializers {
	public static final RecipeSerializer<PaintRollerFillingRecipe> PAINTROLLER_FILLING = register(
			"crafting_special_paint_roller_filling", new SpecialRecipeSerializer<>(PaintRollerFillingRecipe::new));

	private static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String name, S serializer) {
		return Registry.register(Registry.RECIPE_SERIALIZER, CatwalksInc.id(name), serializer);
	}
}
