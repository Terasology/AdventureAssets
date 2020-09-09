// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.traps.mcqButtonDoor;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.network.ServerEvent;

import java.util.List;

@ServerEvent
public class SetMcqButtonDoorEvent implements Event {
    private EntityRef doorEntity;
    private String title;
    private String message;
    private String password;
    private List<String> options;

    public SetMcqButtonDoorEvent() {
    }

    public SetMcqButtonDoorEvent(EntityRef doorEntity, String title, String message, String password,
                                 List<String> options) {
        this.title = title;
        this.message = message;
        this.password = password;
        this.options = options;
        this.doorEntity = doorEntity;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getOptions() {
        return options;
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
