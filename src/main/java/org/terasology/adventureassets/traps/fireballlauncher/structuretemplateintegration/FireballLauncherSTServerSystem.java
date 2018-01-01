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
package org.terasology.adventureassets.traps.fireballlauncher.structuretemplateintegration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.adventureassets.traps.fireballlauncher.FireballLauncherComponent;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.Side;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.structureTemplates.events.BuildStructureTemplateEntityEvent;
import org.terasology.structureTemplates.events.SpawnTemplateEvent;
import org.terasology.structureTemplates.events.StructureBlocksSpawnedEvent;
import org.terasology.structureTemplates.internal.events.BuildStructureTemplateStringEvent;
import org.terasology.structureTemplates.util.ListUtil;
import org.terasology.structureTemplates.util.BlockRegionTransform;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.block.BlockComponent;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.family.BlockFamily;

import java.util.ArrayList;
import java.util.List;

@RegisterSystem(RegisterMode.AUTHORITY)
public class FireballLauncherSTServerSystem extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(FireballLauncherSTServerSystem.class);

    @In
    BlockManager blockManager;
    @In
    BlockEntityRegistry blockEntityRegistry;

    @ReceiveEvent
    public void onSpawnStructure(StructureBlocksSpawnedEvent event, EntityRef entity,
                                 AddFireballLauncherComponent addFireballLauncherComponent) {
        configureFireballLaunchers(addFireballLauncherComponent, event.getTransformation());
    }

    @ReceiveEvent
    public void onSpawnTemplate(SpawnTemplateEvent event, EntityRef entity, AddFireballLauncherComponent addFireballLauncherComponent) {
        configureFireballLaunchers(addFireballLauncherComponent, event.getTransformation());
    }

    /**
     * This method is used to retrieve the stored settings for each Fireball Launcher once it is spawned.
     *
     * @param addFireballLauncherComponent
     * @param transformation
     */
    private void configureFireballLaunchers(AddFireballLauncherComponent addFireballLauncherComponent, BlockRegionTransform transformation) {
        for (AddFireballLauncherComponent.FireballLauncherToSpawn f : addFireballLauncherComponent.fireballLaunchersToSpawn) {
            Vector3i absolutePosition = transformation.transformVector3i(f.position);
            EntityRef fireballLauncher = blockEntityRegistry.getBlockEntityAt(absolutePosition);
            FireballLauncherComponent fireballLauncherComponent = fireballLauncher.getComponent(FireballLauncherComponent.class);
            fireballLauncherComponent.isFiring = f.isFiring;
            fireballLauncherComponent.timePeriod = f.timePeriod;
            fireballLauncherComponent.offset = f.offset;
            fireballLauncherComponent.direction = convertDirectionToAbsolute(f.direction, transformation.transformSide(Side.FRONT));
            fireballLauncherComponent.maxDistance = f.maxDistance;
            fireballLauncherComponent.damageAmount = f.damageAmount;
            fireballLauncher.saveComponent(fireballLauncherComponent);
            LocationComponent locationComponent = fireballLauncher.getComponent(LocationComponent.class);
            fireballLauncher.addOrSaveComponent(locationComponent);

        }
    }

    @ReceiveEvent
    public void onBuildTemplateWithScheduledStructurePlacement(BuildStructureTemplateEntityEvent event, EntityRef entity) {
        BlockRegionTransform transformToRelative = event.getTransformToRelative();
        BlockFamily blockFamily = blockManager.getBlockFamily("AdventureAssets:FireballLauncherRoot");

        List<AddFireballLauncherComponent.FireballLauncherToSpawn> fireballLaunchersToSpawn = new ArrayList<>();

        for (Vector3i position : event.findAbsolutePositionsOf(blockFamily)) {
            EntityRef blockEntity = blockEntityRegistry.getBlockEntityAt(position);
            BlockComponent blockComponent = blockEntity.getComponent(BlockComponent.class);
            FireballLauncherComponent fireballLauncherComponent = blockEntity.getComponent(FireballLauncherComponent.class);
            AddFireballLauncherComponent.FireballLauncherToSpawn fireballLauncherToSpawn = new AddFireballLauncherComponent.FireballLauncherToSpawn();
            Vector3i absolutePosition = new Vector3i(blockComponent.getPosition());
            fireballLauncherToSpawn.position = transformToRelative.transformVector3i(absolutePosition);
            fireballLauncherToSpawn.isFiring = fireballLauncherComponent.isFiring;
            fireballLauncherToSpawn.timePeriod = fireballLauncherComponent.timePeriod;
            fireballLauncherToSpawn.offset = fireballLauncherComponent.offset;
            fireballLauncherToSpawn.direction = convertDirectionToRelative(fireballLauncherComponent.direction, transformToRelative.transformSide(Side.FRONT));
            fireballLauncherToSpawn.damageAmount = fireballLauncherComponent.damageAmount;
            fireballLauncherToSpawn.maxDistance = fireballLauncherComponent.maxDistance;

            fireballLaunchersToSpawn.add(fireballLauncherToSpawn);
        }

        if (fireballLaunchersToSpawn.size() > 0) {
            AddFireballLauncherComponent addFireballLauncherComponent = new AddFireballLauncherComponent();
            addFireballLauncherComponent.fireballLaunchersToSpawn = fireballLaunchersToSpawn;
            event.getTemplateEntity().addOrSaveComponent(addFireballLauncherComponent);
        }
    }

    private Vector3f convertDirectionToRelative(Vector3f direction, Side side) {
        Vector3f relativeDirection = new Vector3f();
        relativeDirection.y = direction.getY();
        switch (side) {
            case FRONT:
                relativeDirection.z = direction.getZ();
                relativeDirection.x = direction.getX();
                break;
            case RIGHT:
                relativeDirection.z = -1 * direction.getX();
                relativeDirection.x = direction.getZ();
                break;
            case BACK:
                relativeDirection.x = -1 * direction.getX();
                relativeDirection.z = -1 * direction.getZ();
                break;
            case LEFT:
                relativeDirection.z = direction.getX();
                relativeDirection.x = -1 * direction.getZ();
                break;
        }
        return relativeDirection;
    }

    private Vector3f convertDirectionToAbsolute(Vector3f direction, Side side) {
        Vector3f absoluteDirection = new Vector3f();
        absoluteDirection.y = direction.getY();
        switch (side) {
            case FRONT:
                absoluteDirection.z = direction.getZ();
                absoluteDirection.x = direction.getX();
                break;
            case RIGHT:
                absoluteDirection.z = direction.getX();
                absoluteDirection.x = -1 * direction.getZ();
                break;
            case BACK:
                absoluteDirection.x = -1 * direction.getX();
                absoluteDirection.z = -1 * direction.getZ();
                break;
            case LEFT:
                absoluteDirection.z = -1 * direction.getX();
                absoluteDirection.x = direction.getZ();
                break;
        }
        return absoluteDirection;
    }

    @ReceiveEvent
    public void onBuildTemplateStringWithBlockRegions(BuildStructureTemplateStringEvent event, EntityRef template,
                                                      AddFireballLauncherComponent component) {
        StringBuilder sb = new StringBuilder();
        sb.append("    \"AddFireballLauncher\": {\n");
        sb.append("        \"fireballLaunchersToSpawn\": [\n");
        ListUtil.visitList(component.fireballLaunchersToSpawn,
                (AddFireballLauncherComponent.FireballLauncherToSpawn fireballLauncher, boolean last) -> {
                    sb.append("            {\n");
                    sb.append("                \"position\": [");
                    sb.append(fireballLauncher.position.x);
                    sb.append(", ");
                    sb.append(fireballLauncher.position.y);
                    sb.append(", ");
                    sb.append(fireballLauncher.position.z);
                    sb.append("],\n");
                    sb.append("                \"isFiring\": ");
                    sb.append(fireballLauncher.isFiring);
                    sb.append(",\n");
                    sb.append("                \"timePeriod\": ");
                    sb.append(fireballLauncher.timePeriod);
                    sb.append(",\n");
                    sb.append("                \"offset\": ");
                    sb.append(fireballLauncher.offset);
                    sb.append(",\n");
                    sb.append("                \"direction\": [");
                    sb.append(fireballLauncher.direction.x);
                    sb.append(", ");
                    sb.append(fireballLauncher.direction.y);
                    sb.append(", ");
                    sb.append(fireballLauncher.direction.z);
                    sb.append("],\n");
                    sb.append("                \"maxDistance\": ");
                    sb.append(fireballLauncher.maxDistance);
                    sb.append(",\n");
                    sb.append("                \"damageAmount\": ");
                    sb.append(fireballLauncher.damageAmount);
                    sb.append("\n");
                    if (last) {
                        sb.append("            }\n");
                    } else {
                        sb.append("            },\n");
                    }
                });
        sb.append("        ]\n");
        sb.append("    }");
        event.addJsonForComponent(sb.toString(), AddFireballLauncherComponent.class);
    }
}
