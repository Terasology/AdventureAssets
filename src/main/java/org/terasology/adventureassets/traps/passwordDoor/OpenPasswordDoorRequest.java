// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.traps.passwordDoor;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.network.OwnerEvent;

@OwnerEvent
public class OpenPasswordDoorRequest implements Event {
    private final EntityRef doorEntity;

    public OpenPasswordDoorRequest() {
        doorEntity = EntityRef.NULL;
    }

    public OpenPasswordDoorRequest(EntityRef doorEntity) {
        this.doorEntity = doorEntity;
    }

    public EntityRef getDoorEntity() {
        return doorEntity;
    }
}
