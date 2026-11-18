package de.schmompcraft.schmomp.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import de.schmompcraft.schmomp.SchmompMod;
import de.schmompcraft.schmomp.client.model.ModModelLayers;
import de.schmompcraft.schmomp.client.model.SchmompModel;
import de.schmompcraft.schmomp.entity.SchmompEntity;
import org.jspecify.annotations.NonNull;


public class SchmompRenderer extends MobRenderer<SchmompEntity, SchmompRenderState, SchmompModel> {
    private static final Identifier TEXTURE_WILD = SchmompMod.id("textures/entity/schmomp.png");
    private static final Identifier TEXTURE_TAMED = SchmompMod.id("textures/entity/schmomp_tamed.png");

    public SchmompRenderer(EntityRendererProvider.Context context){
        super(context, new SchmompModel(context.bakeLayer(ModModelLayers.SCHMOMP)), 0.4f);
    }

    @Override public SchmompRenderState createRenderState(){
        return new SchmompRenderState();
    }

    @Override
    public void extractRenderState(SchmompEntity entity, SchmompRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.sitting = entity.isInSittingPose();
        state.tame = entity.isTame();
    }

    @Override public Identifier getTextureLocation(SchmompRenderState state){
        return TEXTURE_WILD;
    }
}
