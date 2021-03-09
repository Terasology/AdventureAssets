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
package org.terasology.adventureassets.traps.wipeout;

import org.joml.Quaternionf;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.network.ServerEvent;

@ServerEvent
public class SetWipeOutRoot implements Event {
    private EntityRef wipeOutRoot;
    private float timePeriod = 15f;
    private float offset = 0f;
    private boolean isRotating = true;
    private int direction = 1;
    private Quaternionf rotation;

    public SetWipeOutRoot() {
    }

    public SetWipeOutRoot(EntityRef wipeOutRoot, float timePeriod, float offset, boolean isRotating, int direction, Quaternionf rotation) {
        this.wipeOutRoot = wipeOutRoot;
        this.timePeriod = timePeriod;
        this.offset = offset;
        this.isRotating = isRotating;
        this.direction = direction;
        this.rotation = rotation;
    }

    public float getTimePeriod() {
        return timePeriod;
    }

    public float getOffset() {
        return offset;
    }

    public boolean isRotating() {
        return isRotating;
    }

    public int getDirection() {
        return direction;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public EntityRef getWipeOutRoot() {
        return wipeOutRoot;
    }
}
