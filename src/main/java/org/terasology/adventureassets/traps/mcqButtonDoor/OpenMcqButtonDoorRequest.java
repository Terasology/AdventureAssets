// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.traps.mcqButtonDoor;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.network.OwnerEvent;

@OwnerEvent
public class OpenMcqButtonDoorRequest implements Event {
    private final EntityRef doorEntity;

    public OpenMcqButtonDoorRequest() {
        doorEntity = EntityRef.NULL;
    }

    public OpenMcqButtonDoorRequest(EntityRef doorEntity) {
        this.doorEntity = doorEntity;
    }

    public EntityRef getDoorEntity() {
        return doorEntity;
    }
}
