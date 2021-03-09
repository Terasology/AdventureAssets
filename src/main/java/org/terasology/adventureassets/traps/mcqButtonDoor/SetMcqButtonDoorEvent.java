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

    public SetMcqButtonDoorEvent(EntityRef doorEntity, String title, String message, String password, List<String> options) {
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
