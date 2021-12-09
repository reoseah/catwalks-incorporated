package com.github.reoseah.catwalksinc.rei;

import java.util.List;
import java.util.Optional;

import com.github.reoseah.catwalksinc.CatwalksInc;
import com.google.common.collect.ImmutableList;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.display.DynamicDisplayGenerator;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.plugin.common.displays.DefaultInformationDisplay;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class CatwalksIncREI implements REIClientPlugin {
	@Override
	public void registerDisplays(DisplayRegistry registry) {
		registry.registerGlobalDisplayGenerator(new DynamicDisplayGenerator<DefaultInformationDisplay>() {
			@Override
			public Optional<List<DefaultInformationDisplay>> getUsageFor(EntryStack<?> entry) {
				return Optional.ofNullable(entry.getIdentifier()) //
						.filter(id -> id.getNamespace().equals(CatwalksInc.MODID)) //
						.map(id -> Util.createTranslationKey("misc",
								new Identifier(id.getNamespace(), id.getPath() + ".usage"))) //
						.filter(I18n::hasTranslation) //
						.map(info -> DefaultInformationDisplay.createFromEntry(entry, entry.asFormatStrippedText())
								.line(new TranslatableText(info)))
						.map(ImmutableList::of);
			}

			@Override
			public Optional<List<DefaultInformationDisplay>> getRecipeFor(EntryStack<?> entry) {
				return Optional.ofNullable(entry.getIdentifier()) //
						.filter(id -> id.getNamespace().equals(CatwalksInc.MODID)) //
						.map(id -> Util.createTranslationKey("misc",
								new Identifier(id.getNamespace(), id.getPath() + ".recipe"))) //
						.filter(I18n::hasTranslation) //
						.map(info -> DefaultInformationDisplay.createFromEntry(entry, entry.asFormatStrippedText())
								.line(new TranslatableText(info)))
						.map(ImmutableList::of);
			}
		});
	}
}
