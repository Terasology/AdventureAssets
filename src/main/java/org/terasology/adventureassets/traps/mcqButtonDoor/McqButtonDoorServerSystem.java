// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.adventureassets.traps.mcqButtonDoor;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.EventPriority;
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

    @ReceiveEvent(priority = EventPriority.PRIORITY_HIGH,
            components = {DoorComponent.class, McqButtonDoorComponent.class, BlockRegionComponent.class,
                    LocationComponent.class})
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
