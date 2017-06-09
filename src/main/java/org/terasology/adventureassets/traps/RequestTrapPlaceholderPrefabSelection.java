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

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.Event;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.network.ServerEvent;
import org.terasology.structureTemplates.internal.components.StructurePlaceholderComponent;

/**
 *
 * The event gets sent to a character entity at the server.
 *
 * The server will then check if the character is interacting with a entity that has the {@link TrapPlaceholderComponent}.
 *
 * If that is the case that component will be updated with the values of the event.
 *
 */
@ServerEvent
public class RequestTrapPlaceholderPrefabSelection implements Event {
    private Prefab prefab;
    private EntityRef trapPlaceholderBlockEntity;

    public RequestTrapPlaceholderPrefabSelection(Prefab prefab, EntityRef trapPlaceholderBlockEntity) {
        this.prefab = prefab;
        this.trapPlaceholderBlockEntity = trapPlaceholderBlockEntity;
    }

    public RequestTrapPlaceholderPrefabSelection() {
    }

    public Prefab getPrefab() {
        return prefab;
    }

    public EntityRef getTrapPlaceholderBlockEntity() {
        return trapPlaceholderBlockEntity;
    }
}
