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
package org.terasology.adventureassets.traps.swingingblade;

import org.joml.Quaternionf;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.network.ServerEvent;

@ServerEvent
public class SetSwingingBladeRoot implements Event {
    private EntityRef swingingBladeRoot;
    private float timePeriod = 15f;
    private float offset = 0f;
    private boolean isSwinging = true;
    private float amplitude = 3.14f / 6;
    private Quaternionf rotation;

    public SetSwingingBladeRoot() {
    }

    public SetSwingingBladeRoot(EntityRef swingingBladeRoot, float timePeriod, float offset, boolean isSwinging, float amplitude, Quaternionf rotation) {
        this.swingingBladeRoot = swingingBladeRoot;
        this.timePeriod = timePeriod;
        this.offset = offset;
        this.isSwinging = isSwinging;
        this.amplitude = amplitude;
        this.rotation = rotation;
    }

    public float getTimePeriod() {
        return timePeriod;
    }

    public float getOffset() {
        return offset;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public EntityRef getSwingingBladeRoot() {
        return swingingBladeRoot;
    }

    public boolean isSwinging() {
        return isSwinging;
    }

    public float getAmplitude() {
        return amplitude;
    }
}
