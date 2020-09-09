// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.traps.passwordDoor;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.network.ServerEvent;

@ServerEvent
public class SetPasswordDoorEvent implements Event {
    private EntityRef doorEntity;
    private String title;
    private String message;
    private String password;

    public SetPasswordDoorEvent() {
    }

    public SetPasswordDoorEvent(EntityRef doorEntity, String title, String message, String password) {
        this.title = title;
        this.message = message;
        this.password = password;
        this.doorEntity = doorEntity;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getPassword() {
        return password;
    }

    public EntityRef getDoorEntity() {
        return doorEntity;
    }
}
