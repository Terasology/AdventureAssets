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
import org.terasology.adventureassets.traps.TrapsPlacementComponent;
import org.terasology.assets.management.AssetManager;
import org.terasology.entitySystem.entity.EntityBuilder;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.characters.CharacterComponent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.Side;
import org.terasology.math.geom.Quat4f;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.structureTemplates.components.ScheduleStructurePlacementComponent;
import org.terasology.structureTemplates.events.BuildStructureTemplateEntityEvent;
import org.terasology.structureTemplates.events.SpawnTemplateEvent;
import org.terasology.structureTemplates.events.StructureBlocksSpawnedEvent;
import org.terasology.structureTemplates.internal.events.BuildStructureTemplateStringEvent;
import org.terasology.structureTemplates.util.ListUtil;
import org.terasology.structureTemplates.util.transform.BlockRegionTransform;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockComponent;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.family.BlockFamily;
import org.terasology.world.block.family.HorizontalBlockFamily;
import org.terasology.world.block.items.OnBlockItemPlaced;

import java.util.ArrayList;
import java.util.List;


/**
 * Contains the logic to make spawning of Swinging Blades work.
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class SpawnSwingingBladeServerSystem extends BaseComponentSystem {

    private static final Logger logger = LoggerFactory.getLogger(SpawnSwingingBladeServerSystem.class);
    @In
    private EntityManager entityManager;
    @In
    private AssetManager assetManager;
    @In
    private BlockManager blockManager;
    @In
    private BlockEntityRegistry blockEntityRegistry;
    @In
    private WorldProvider worldProvider;

    @ReceiveEvent
    public void onSpawnStructureWithSwingingBlade(StructureBlocksSpawnedEvent event, EntityRef entity,
                                                  TrapsPlacementComponent trapsPlacementComponent) {
        if (trapsPlacementComponent.swingingBladeList.size() > 0) {
            spawnSwingingBlades(event.getTransformation(), trapsPlacementComponent.swingingBladeList);
        }
    }

    @ReceiveEvent
    public void onTemplateSpawned(SpawnTemplateEvent event, EntityRef entity, TrapsPlacementComponent trapsPlacementComponent) {
        BlockRegionTransform transformation = event.getTransformation();
        for (SwingingBlade swingingBlade : trapsPlacementComponent.swingingBladeList) {
            Vector3i actualPosition = transformation.transformVector3i(swingingBlade.position);
            Prefab selectedTrapType = swingingBlade.prefab;

            BlockFamily blockFamily = blockManager.getBlockFamily("AdventureAssets:TrapPlaceholder");
            HorizontalBlockFamily horizontalBlockFamily = (HorizontalBlockFamily) blockFamily;
            //TODO: Use rotation to remove FRONT hardcoding
            Block block = horizontalBlockFamily.getBlockForSide(Side.FRONT);
            Vector3i positionAbove = new Vector3i(actualPosition);
            positionAbove.addY(1);
            worldProvider.setBlock(positionAbove, block);
            EntityRef blockEntity = blockEntityRegistry.getBlockEntityAt(positionAbove);
            logger.info("Spawned trapPlaceholder id: " + blockEntity.getId() + " position: " + positionAbove);
            TrapPlaceholderComponent trapPlaceholderComponent = blockEntity.getComponent(TrapPlaceholderComponent.class);
            trapPlaceholderComponent.selectedPrefab = selectedTrapType;
            blockEntity.saveComponent(trapPlaceholderComponent);
        }
    }


    @ReceiveEvent
    public void onBlockItemPlaced(OnBlockItemPlaced event, EntityRef entity) {
        EntityRef trapPlaceholder = event.getPlacedBlock();
        if (!trapPlaceholder.hasComponent(TrapPlaceholderComponent.class)){
            return;
        }
        logger.info("on block placed for id: " + trapPlaceholder.getId());
    }

    //TODO: Move to outer TrapPlacementSystem
    @ReceiveEvent
    public void onBuildTemplateStringWithBlockRegions(BuildStructureTemplateStringEvent event, EntityRef template,
                                                      TrapsPlacementComponent component) {
        StringBuilder sb = new StringBuilder();
        sb.append("    \"TrapsPlacement\": {\n");
        sb.append("        \"swingingBladeList\": [\n");
        ListUtil.visitList(component.swingingBladeList,
                (SwingingBlade swingingBlade, boolean last)-> {
                    sb.append("            {\n");
                    sb.append("                \"position\": [");
                    sb.append(swingingBlade.position.x);
                    sb.append(", ");
                    sb.append(swingingBlade.position.y);
                    sb.append(", ");
                    sb.append(swingingBlade.position.z);
                    sb.append("],\n");
                    sb.append("                \"rotation\": [");
                    sb.append(swingingBlade.rotation.x);
                    sb.append(", ");
                    sb.append(swingingBlade.rotation.y);
                    sb.append(", ");
                    sb.append(swingingBlade.rotation.z);
                    sb.append(", ");
                    sb.append(swingingBlade.rotation.w);
                    sb.append("],\n");
                    sb.append("                \"amplitude\": ");
                    sb.append(swingingBlade.amplitude);
                    sb.append(",\n");
                    sb.append("                \"timePeriod\": ");
                    sb.append(swingingBlade.timePeriod);
                    sb.append(",\n");
                    sb.append("                \"offset\": ");
                    sb.append(swingingBlade.offset);
                    sb.append("\n");
                    if (last) {
                        sb.append("            }\n");
                    } else {
                        sb.append("            },\n");
                    }
                });
        sb.append("        ]\n");
        sb.append("    }");
        event.addJsonForComponent(sb.toString(), ScheduleStructurePlacementComponent.class);
    }

    private void spawnSwingingBlades(BlockRegionTransform transformation, List<SwingingBlade> bladeList) {
        for (SwingingBlade swingingBlade : bladeList) {
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
    public void onBuildTemplateWithTrapPlacement(BuildStructureTemplateEntityEvent event, EntityRef entity) {
        BlockRegionTransform transformToRelative = event.getTransformToRelative();
        BlockFamily blockFamily = blockManager.getBlockFamily("AdventureAssets:TrapPlaceholder");

        List<SwingingBlade> bladeList = new ArrayList<>();
        //TODO: Possible bug in findAbsolutePositionsOf method. Does not return all blocks.
        for (Vector3i position : event.findAbsolutePositionsOf(blockFamily)) {
            EntityRef blockEntity = blockEntityRegistry.getBlockEntityAt(position);
            TrapPlaceholderComponent trapPlaceholderComponent = blockEntity.getComponent(TrapPlaceholderComponent.class);
            if (trapPlaceholderComponent.selectedPrefab == null) {
                continue;
            }
            BlockComponent blockComponent = blockEntity.getComponent(BlockComponent.class);
            SwingingBlade swingingBlade = new SwingingBlade();
            swingingBlade.position = transformToRelative.transformVector3i(blockComponent.getPosition());
            swingingBlade.position.subY(1); // placeholder is on top of marked block
            swingingBlade.rotation = transformToRelative.transformRotation(swingingBlade.rotation);
            //TODO: Fetch properties from the actual swinging blade entity
            bladeList.add(swingingBlade);
        }
        if (bladeList.size() > 0) {
            TrapsPlacementComponent trapsPlacementComponent = event.getTemplateEntity().getComponent(TrapsPlacementComponent.class);
            if (trapsPlacementComponent == null) {
                trapsPlacementComponent = new TrapsPlacementComponent();
            }
            trapsPlacementComponent.swingingBladeList = bladeList;
            event.getTemplateEntity().addOrSaveComponent(trapsPlacementComponent);
        }
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
    }
}
