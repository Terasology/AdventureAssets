// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.traps.fireballlauncher.structuretemplateintegration;

import org.joml.Vector3f;
import org.joml.Vector3i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.adventureassets.traps.fireballlauncher.FireballLauncherComponent;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.math.Side;
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
            Vector3i absolutePosition = blockComponent.getPosition(new Vector3i());
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
        relativeDirection.y = direction.y();
        switch (side) {
            case FRONT:
                relativeDirection.z = direction.z();
                relativeDirection.x = direction.x();
                break;
            case RIGHT:
                relativeDirection.z = -1 * direction.x();
                relativeDirection.x = direction.z();
                break;
            case BACK:
                relativeDirection.x = -1 * direction.x();
                relativeDirection.z = -1 * direction.z();
                break;
            case LEFT:
                relativeDirection.z = direction.x();
                relativeDirection.x = -1 * direction.z();
                break;
        }
        return relativeDirection;
    }

    private Vector3f convertDirectionToAbsolute(Vector3f direction, Side side) {
        Vector3f absoluteDirection = new Vector3f();
        absoluteDirection.y = direction.y();
        switch (side) {
            case FRONT:
                absoluteDirection.z = direction.z();
                absoluteDirection.x = direction.x();
                break;
            case RIGHT:
                absoluteDirection.z = direction.x();
                absoluteDirection.x = -1 * direction.z();
                break;
            case BACK:
                absoluteDirection.x = -1 * direction.x();
                absoluteDirection.z = -1 * direction.z();
                break;
            case LEFT:
                absoluteDirection.z = -1 * direction.x();
                absoluteDirection.x = direction.z();
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
