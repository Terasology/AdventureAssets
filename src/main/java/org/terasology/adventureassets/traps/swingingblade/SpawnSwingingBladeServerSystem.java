/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.adventureassets.traps.swingingblade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.adventureassets.traps.RequestTrapPlaceholderPrefabSelection;
import org.terasology.adventureassets.traps.TrapPlaceholderComponent;
import org.terasology.assets.management.AssetManager;
import org.terasology.entitySystem.entity.EntityBuilder;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.characters.CharacterComponent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Quat4f;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.structureTemplates.components.AddItemsToChestComponent;
import org.terasology.structureTemplates.components.ScheduleStructurePlacementComponent;
import org.terasology.structureTemplates.events.BuildStructureTemplateEntityEvent;
import org.terasology.structureTemplates.events.SpawnTemplateEvent;
import org.terasology.structureTemplates.events.StructureBlocksSpawnedEvent;
import org.terasology.structureTemplates.internal.components.StructurePlaceholderComponent;
import org.terasology.structureTemplates.internal.events.BuildStructureTemplateStringEvent;
import org.terasology.structureTemplates.internal.events.RequestStructurePlaceholderPrefabSelection;
import org.terasology.structureTemplates.util.transform.BlockRegionTransform;
import org.terasology.world.WorldProvider;


/**
 * Contains the logic to make {@link SpawnSwingingBladeComponent} work.
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class SpawnSwingingBladeServerSystem extends BaseComponentSystem {

    @In
    private EntityManager entityManager;
    @In
    private AssetManager assetManager;
    @In
    private WorldProvider worldProvider;

    private static final Logger logger = LoggerFactory.getLogger(SpawnSwingingBladeServerSystem.class);

    @ReceiveEvent
    public void onSpawnStructureWithSwingingBlade(StructureBlocksSpawnedEvent event, EntityRef entity,
                                                  SpawnSwingingBladeComponent component) {
        spawnSwingingBlades(event.getTransformation(), component);
    }

    @ReceiveEvent
    public void onTemplateSpawned(SpawnTemplateEvent event, EntityRef entity, SpawnSwingingBladeComponent component) {
        spawnSwingingBlades(event.getTransformation(), component);
    }

    @ReceiveEvent
    public void onBuildTemplateStringWithBlockRegions(BuildStructureTemplateStringEvent event, EntityRef template,
                                                      SpawnSwingingBladeComponent component) {
        logger.info("works");
    }

    private void spawnSwingingBlades(BlockRegionTransform transformation, SpawnSwingingBladeComponent component) {
        for (SpawnSwingingBladeComponent.SwingingBlade swingingBlade : component.bladeList) {
            Vector3i position = transformation.transformVector3i(swingingBlade.position);
            Quat4f rotation = transformation.transformRotation(swingingBlade.rotation);

            EntityBuilder entityBuilder = entityManager.newBuilder(swingingBlade.prefab);
            LocationComponent locationComponent = entityBuilder.getComponent(LocationComponent.class);
            locationComponent.setWorldPosition(position.toVector3f());
            locationComponent.setWorldRotation(rotation);
            SwingingBladeComponent swingingBladeComponent = entityBuilder.getComponent(SwingingBladeComponent.class);
            swingingBladeComponent.timePeriod = swingingBlade.timePeriod;
            swingingBladeComponent.amplitude = swingingBlade.amplitude;
            swingingBladeComponent.offset = swingingBlade.offset;

            entityBuilder.build();
        }
    }

    @ReceiveEvent
    public void onBuildTemplateWithScheduledStructurePlacment(BuildStructureTemplateEntityEvent event, EntityRef entity) {
    }

    @ReceiveEvent
    public void onRequestTrapPlaceholderPrefabSelection(RequestTrapPlaceholderPrefabSelection event, EntityRef characterEntity,
                                                             CharacterComponent characterComponent) {
        EntityRef interactionTarget = characterComponent.authorizedInteractionTarget;
        TrapPlaceholderComponent trapPlaceholderComponent = interactionTarget.getComponent(TrapPlaceholderComponent.class);
        if (trapPlaceholderComponent == null) {
            logger.error("Ignored RequestTrapPlaceholderPrefabSelection event since there was no interaction with a trap placeholder");
            return;
        }

        trapPlaceholderComponent.selectedPrefab = event.getPrefab();
        interactionTarget.saveComponent(trapPlaceholderComponent);
        logger.info("updated trap placeholder to: " + event.getPrefab().getName());
    }
}
