package de.schmompcraft.schmomp.client;

import de.schmompcraft.schmomp.client.model.ModModelLayers;
import de.schmompcraft.schmomp.client.model.SchmompModel;
import de.schmompcraft.schmomp.client.render.SchmompRenderer;
import de.schmompcraft.schmomp.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;

public class SchmompModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModelLayerRegistry.registerModelLayer(ModModelLayers.SCHMOMP, SchmompModel::createBodyLayer);
		EntityRenderers.register(ModEntities.SCHMOMP, SchmompRenderer::new);
	}
}