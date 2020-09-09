// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.traps.wipeout.structuretemplateintegration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.adventureassets.traps.wipeout.WipeOutComponent;
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
import org.terasology.math.geom.Quat4f;
import org.terasology.math.geom.Vector3i;
import org.terasology.structureTemplates.events.BuildStructureTemplateEntityEvent;
import org.terasology.structureTemplates.events.SpawnTemplateEvent;
import org.terasology.structureTemplates.events.StructureBlocksSpawnedEvent;
import org.terasology.structureTemplates.internal.events.BuildStructureTemplateStringEvent;
import org.terasology.structureTemplates.util.BlockRegionTransform;
import org.terasology.structureTemplates.util.ListUtil;

import java.util.ArrayList;
import java.util.List;

@RegisterSystem(RegisterMode.AUTHORITY)
public class WipeOutSTServerSystem extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(WipeOutSTServerSystem.class);

    @In
    BlockManager blockManager;
    @In
    BlockEntityRegistry blockEntityRegistry;

    @ReceiveEvent
    public void onSpawnStructure(StructureBlocksSpawnedEvent event, EntityRef entity,
                                 AddWipeOutComponent addWipeOutComponent) {
        configureWipeOut(addWipeOutComponent, event.getTransformation());
    }

    @ReceiveEvent
    public void onSpawnTemplate(SpawnTemplateEvent event, EntityRef entity, AddWipeOutComponent addWipeOutComponent) {
        configureWipeOut(addWipeOutComponent, event.getTransformation());
    }

    /**
     * This method is used to retrieve the stored settings for each WipeOut once it is spawned. This method is called
     * only after the OnActivatedComponent for the {@link WipeOutComponent} has executed.<br/> Note: only the properties
     * of the Wipe Out should be overwritten in the {@link WipeOutComponent}, since the existing {@link
     * WipeOutComponent} already has a list for childrenEntities (rod, blade and mesh).
     *
     * @param addWipeOutComponent
     * @param transformation
     */
    private void configureWipeOut(AddWipeOutComponent addWipeOutComponent, BlockRegionTransform transformation) {
        for (AddWipeOutComponent.WipeOutsToSpawn w : addWipeOutComponent.wipeOutsToSpawn) {
            Vector3i absolutePosition = transformation.transformVector3i(w.position);
            Quat4f absoluteRotation = transformation.transformRotation(w.rotation);
            EntityRef wipeOut = blockEntityRegistry.getBlockEntityAt(absolutePosition);
            WipeOutComponent wipeOutComponent = wipeOut.getComponent(WipeOutComponent.class);
            wipeOutComponent.direction = w.direction;
            wipeOutComponent.timePeriod = w.timePeriod;
            wipeOutComponent.offset = w.offset;
            wipeOutComponent.isRotating = w.isRotating;

            wipeOut.saveComponent(wipeOutComponent);
            LocationComponent locationComponent = wipeOut.getComponent(LocationComponent.class);
            locationComponent.setWorldRotation(absoluteRotation);
            wipeOut.addOrSaveComponent(locationComponent);

        }
    }

    @ReceiveEvent
    public void onBuildTemplateWithScheduledStructurePlacement(BuildStructureTemplateEntityEvent event,
                                                               EntityRef entity) {
        BlockRegionTransform transformToRelative = event.getTransformToRelative();
        BlockFamily blockFamily = blockManager.getBlockFamily("AdventureAssets:WipeOutRoot");


        List<AddWipeOutComponent.WipeOutsToSpawn> wipeOutsToSpawns = new ArrayList<>();

        for (Vector3i position : event.findAbsolutePositionsOf(blockFamily)) {
            EntityRef blockEntity = blockEntityRegistry.getBlockEntityAt(position);
            BlockComponent blockComponent = blockEntity.getComponent(BlockComponent.class);
            WipeOutComponent wipeOutComponent = blockEntity.getComponent(WipeOutComponent.class);
            AddWipeOutComponent.WipeOutsToSpawn wipeOutToSpawn = new AddWipeOutComponent.WipeOutsToSpawn();
            Vector3i absolutePosition = new Vector3i(blockComponent.getPosition());
            wipeOutToSpawn.position = transformToRelative.transformVector3i(absolutePosition);
            wipeOutToSpawn.rotation =
                    transformToRelative.transformRotation(blockEntity.getComponent(LocationComponent.class).getWorldRotation());
            wipeOutToSpawn.direction = wipeOutComponent.direction;
            wipeOutToSpawn.timePeriod = wipeOutComponent.timePeriod;
            wipeOutToSpawn.offset = wipeOutComponent.offset;
            wipeOutToSpawn.isRotating = wipeOutComponent.isRotating;

            wipeOutsToSpawns.add(wipeOutToSpawn);
        }

        if (wipeOutsToSpawns.size() > 0) {
            AddWipeOutComponent addWipeOutComponent = new AddWipeOutComponent();
            addWipeOutComponent.wipeOutsToSpawn = wipeOutsToSpawns;
            event.getTemplateEntity().addOrSaveComponent(addWipeOutComponent);
        }
    }

    @ReceiveEvent
    public void onBuildTemplateStringWithBlockRegions(BuildStructureTemplateStringEvent event, EntityRef template,
                                                      AddWipeOutComponent component) {
        StringBuilder sb = new StringBuilder();
        sb.append("    \"AddWipeOut\": {\n");
        sb.append("        \"WipeOutsToSpawn\": [\n");
        ListUtil.visitList(component.wipeOutsToSpawn,
                (AddWipeOutComponent.WipeOutsToSpawn wipeOut, boolean last) -> {
                    sb.append("            {\n");
                    sb.append("                \"position\": [");
                    sb.append(wipeOut.position.x);
                    sb.append(", ");
                    sb.append(wipeOut.position.y);
                    sb.append(", ");
                    sb.append(wipeOut.position.z);
                    sb.append("],\n");
                    sb.append("                \"rotation\": [");
                    sb.append(wipeOut.rotation.x);
                    sb.append(", ");
                    sb.append(wipeOut.rotation.y);
                    sb.append(", ");
                    sb.append(wipeOut.rotation.z);
                    sb.append(", ");
                    sb.append(wipeOut.rotation.w);
                    sb.append("],\n");
                    sb.append("                \"direction\": ");
                    sb.append(wipeOut.direction);
                    sb.append(",\n");
                    sb.append("                \"timePeriod\": ");
                    sb.append(wipeOut.timePeriod);
                    sb.append(",\n");
                    sb.append("                \"offset\": ");
                    sb.append(wipeOut.offset);
                    sb.append(",\n");
                    sb.append("                \"isRotating\": ");
                    sb.append(wipeOut.isRotating);
                    sb.append("\n");
                    if (last) {
                        sb.append("            }\n");
                    } else {
                        sb.append("            },\n");
                    }
                });
        sb.append("        ]\n");
        sb.append("    }");
        event.addJsonForComponent(sb.toString(), AddWipeOutComponent.class);
    }
}
