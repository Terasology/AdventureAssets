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
package org.terasology.adventureassets.traps.swingingblade.structuretemplateintegration;

import org.joml.Quaternionf;
import org.joml.Vector3i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.adventureassets.traps.swingingblade.SwingingBladeComponent;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.BlockEntityRegistry;
import org.terasology.engine.world.block.BlockComponent;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.block.family.BlockFamily;
import org.terasology.structureTemplates.events.BuildStructureTemplateEntityEvent;
import org.terasology.structureTemplates.events.SpawnTemplateEvent;
import org.terasology.structureTemplates.events.StructureBlocksSpawnedEvent;
import org.terasology.structureTemplates.internal.events.BuildStructureTemplateStringEvent;
import org.terasology.structureTemplates.util.BlockRegionTransform;
import org.terasology.structureTemplates.util.ListUtil;

import java.util.ArrayList;
import java.util.List;

@RegisterSystem(RegisterMode.AUTHORITY)
public class SwingingBladeSTServerSystem extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(SwingingBladeSTServerSystem.class);

    @In
    BlockManager blockManager;
    @In
    BlockEntityRegistry blockEntityRegistry;

    @ReceiveEvent
    public void onSpawnStructure(StructureBlocksSpawnedEvent event, EntityRef entity,
                                 AddSwingingBladeComponent addSwingingBladeComponent) {
        configureSwingingBlades(addSwingingBladeComponent, event.getTransformation());
    }

    @ReceiveEvent
    public void onSpawnTemplate(SpawnTemplateEvent event, EntityRef entity, AddSwingingBladeComponent addSwingingBladeComponent) {
        configureSwingingBlades(addSwingingBladeComponent, event.getTransformation());
    }

    /**
     * This method is used to retrieve the stored settings for each SwingingBlade once it is spawned.
     * This method is called only after the OnActivatedComponent for the {@link SwingingBladeComponent} has executed.<br/>
     * Note: only the properties of the Swinging Blade should be overwritten in the {@link SwingingBladeComponent},
     * since the existing {@link SwingingBladeComponent} already has a list for childrenEntities (rod, blade and mesh).
     *
     * @param addSwingingBladeComponent
     * @param transformation
     */
    private void configureSwingingBlades(AddSwingingBladeComponent addSwingingBladeComponent, BlockRegionTransform transformation) {
        for (AddSwingingBladeComponent.SwingingBladesToSpawn s : addSwingingBladeComponent.swingingBladesToSpawn) {
            Vector3i absolutePosition = transformation.transformVector3i(s.position);
            Quaternionf absoluteRotation = transformation.transformRotation(s.rotation);
            EntityRef swingingBlade = blockEntityRegistry.getBlockEntityAt(absolutePosition);
            SwingingBladeComponent swingingBladeComponent = swingingBlade.getComponent(SwingingBladeComponent.class);
            swingingBladeComponent.amplitude = s.amplitude;
            swingingBladeComponent.timePeriod = s.timePeriod;
            swingingBladeComponent.offset = s.offset;
            swingingBladeComponent.isSwinging = s.isSwinging;
            swingingBlade.saveComponent(swingingBladeComponent);
            LocationComponent locationComponent = swingingBlade.getComponent(LocationComponent.class);
            locationComponent.setWorldRotation(absoluteRotation);
            swingingBlade.addOrSaveComponent(locationComponent);

        }
    }

    @ReceiveEvent
    public void onBuildTemplateWithScheduledStructurePlacement(BuildStructureTemplateEntityEvent event, EntityRef entity) {
        BlockRegionTransform transformToRelative = event.getTransformToRelative();
        BlockFamily blockFamily = blockManager.getBlockFamily("AdventureAssets:SwingingBladeRoot");


        List<AddSwingingBladeComponent.SwingingBladesToSpawn> swingingBladesToSpawn = new ArrayList<>();

        for (Vector3i position : event.findAbsolutePositionsOf(blockFamily)) {
            EntityRef blockEntity = blockEntityRegistry.getBlockEntityAt(position);
            BlockComponent blockComponent = blockEntity.getComponent(BlockComponent.class);
            SwingingBladeComponent swingingBladeComponent = blockEntity.getComponent(SwingingBladeComponent.class);
            AddSwingingBladeComponent.SwingingBladesToSpawn swingingBladeToSpawn = new AddSwingingBladeComponent.SwingingBladesToSpawn();
            Vector3i absolutePosition = new Vector3i(blockComponent.getPosition(new Vector3i()));
            swingingBladeToSpawn.position = transformToRelative.transformVector3i(absolutePosition);
            swingingBladeToSpawn.rotation = transformToRelative.transformRotation(blockEntity.getComponent(LocationComponent.class).getWorldRotation(new Quaternionf()));
            swingingBladeToSpawn.amplitude = swingingBladeComponent.amplitude;
            swingingBladeToSpawn.timePeriod = swingingBladeComponent.timePeriod;
            swingingBladeToSpawn.offset = swingingBladeComponent.offset;
            swingingBladeToSpawn.isSwinging = swingingBladeComponent.isSwinging;

            swingingBladesToSpawn.add(swingingBladeToSpawn);
        }

        if (swingingBladesToSpawn.size() > 0) {
            AddSwingingBladeComponent addSwingingBladeComponent = new AddSwingingBladeComponent();
            addSwingingBladeComponent.swingingBladesToSpawn = swingingBladesToSpawn;
            event.getTemplateEntity().addOrSaveComponent(addSwingingBladeComponent);
        }
    }

    @ReceiveEvent
    public void onBuildTemplateStringWithBlockRegions(BuildStructureTemplateStringEvent event, EntityRef template,
                                                      AddSwingingBladeComponent component) {
        StringBuilder sb = new StringBuilder();
        sb.append("    \"AddSwingingBlade\": {\n");
        sb.append("        \"swingingBladesToSpawn\": [\n");
        ListUtil.visitList(component.swingingBladesToSpawn,
                (AddSwingingBladeComponent.SwingingBladesToSpawn swingingBlade, boolean last) -> {
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
                    sb.append(",\n");
                    sb.append("                \"isSwinging\": ");
                    sb.append(swingingBlade.isSwinging);
                    sb.append("\n");
                    if (last) {
                        sb.append("            }\n");
                    } else {
                        sb.append("            },\n");
                    }
                });
        sb.append("        ]\n");
        sb.append("    }");
        event.addJsonForComponent(sb.toString(), AddSwingingBladeComponent.class);
    }
}
