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
package org.terasology.adventureassets.traps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.adventureassets.traps.swingingblade.SwingingBlade;
import org.terasology.assets.management.AssetManager;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.characters.CharacterComponent;
import org.terasology.logic.health.DoDestroyEvent;
import org.terasology.registry.In;
import org.terasology.structureTemplates.components.ScheduleStructurePlacementComponent;
import org.terasology.structureTemplates.internal.events.BuildStructureTemplateStringEvent;
import org.terasology.structureTemplates.util.ListUtil;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.BlockManager;


/**
 * Contains the logic to make spawning of Swinging Blades work.
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class TrapPlaceholderSystem extends BaseComponentSystem {

    private static final Logger logger = LoggerFactory.getLogger(TrapPlaceholderSystem.class);
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
    public void onBuildTemplateStringWithTraps(BuildStructureTemplateStringEvent event, EntityRef template,
                                                      TrapsPlacementComponent component) {
        StringBuilder sb = new StringBuilder();
        sb.append("    \"TrapsPlacement\": {\n");
        sb.append("        \"swingingBladeList\": [\n");
        ListUtil.visitList(component.swingingBladeList,
                (SwingingBlade swingingBlade, boolean last) -> {
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

    @ReceiveEvent(priority = EventPriority.PRIORITY_CRITICAL)
    public void onRequestTrapPlaceholderPrefabSelection(RequestTrapPlaceholderPrefabSelection event, EntityRef characterEntity,
                                                        CharacterComponent characterComponent) {
        EntityRef interactionTarget = characterComponent.authorizedInteractionTarget;
        TrapPlaceholderComponent trapPlaceholderComponent = interactionTarget.getComponent(TrapPlaceholderComponent.class);
        if (trapPlaceholderComponent == null) {
            logger.error("Ignored RequestTrapPlaceholderPrefabSelection event since there was no interaction with a trap placeholder");
            return;
        }

        trapPlaceholderComponent.setSelectedPrefab(event.getPrefab());
        interactionTarget.saveComponent(trapPlaceholderComponent);
    }

    @ReceiveEvent
    public void onDestroyed(DoDestroyEvent event, EntityRef entity, TrapPlaceholderComponent trapPlaceholderComponent) {
        if (trapPlaceholderComponent.getTrapEntity() != null) {
            entity.send(new DeleteTrapEvent(trapPlaceholderComponent.getSelectedPrefab(), trapPlaceholderComponent.getTrapEntity()));
        }
    }
}
