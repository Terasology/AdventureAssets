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

package org.terasology.adventureassets.traps.mcqButtonDoor;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.EventPriority;
import org.terasology.engine.entitySystem.event.Priority;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.common.ActivateEvent;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.nui.NUIManager;
import org.terasology.engine.world.BlockEntityRegistry;
import org.terasology.engine.world.WorldProvider;
import org.terasology.engine.world.block.regions.BlockRegionComponent;
import org.terasology.furnishings.logic.door.CloseDoorEvent;
import org.terasology.furnishings.logic.door.DoorComponent;

@RegisterSystem(RegisterMode.AUTHORITY)
public class McqButtonDoorServerSystem extends BaseComponentSystem {

    @In
    private WorldProvider worldProvider;
    @In
    private BlockEntityRegistry blockEntityRegistry;
    @In
    private NUIManager nuiManager;

    @Priority(EventPriority.PRIORITY_HIGH)
    @ReceiveEvent(components =
            {DoorComponent.class, McqButtonDoorComponent.class, BlockRegionComponent.class, LocationComponent.class}
    )
    public void onFrob(ActivateEvent event, EntityRef entity) {
        event.consume();
        DoorComponent door = entity.getComponent(DoorComponent.class);
        if (door.isOpen) {
            event.getInstigator().send(new CloseDoorEvent(entity));
        } else {
            event.getInstigator().send(new OpenMcqButtonDoorRequest(entity));
        }
    }

    @ReceiveEvent
    public void setPasswordDoor(SetMcqButtonDoorEvent event, EntityRef entity) {
        EntityRef doorEntity = event.getDoorEntity();
        McqButtonDoorComponent mcqButtonDoorComponent = new McqButtonDoorComponent();
        mcqButtonDoorComponent.title = event.getTitle();
        mcqButtonDoorComponent.message = event.getMessage();
        mcqButtonDoorComponent.password = event.getPassword();
        mcqButtonDoorComponent.options = event.getOptions();
        doorEntity.addOrSaveComponent(mcqButtonDoorComponent);
    }
}
