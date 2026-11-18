package de.schmompcraft.schmomp.entity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.levelgen.Heightmap;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import de.schmompcraft.schmomp.SchmompMod;

public class ModEntities {
    public static final ResourceKey<EntityType<?>> SCHMOMP_KEY = ResourceKey.create(Registries.ENTITY_TYPE, SchmompMod.id("schmomp"));
    public static final EntityType<SchmompEntity> SCHMOMP = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            SCHMOMP_KEY,
            FabricEntityType.Builder.<SchmompEntity>createMob(SchmompEntity::new, MobCategory.CREATURE, mob -> mob
                            .defaultAttributes(SchmompEntity::createAttributes)
                            .spawnPlacement(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules))
                            .sized(0.65f, 0.75f)
                            .clientTrackingRange(10)
                            .build(SCHMOMP_KEY));

    public static void initialize(){

    }

}
