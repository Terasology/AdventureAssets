// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.traps.swingingblade;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.network.ServerEvent;
import org.terasology.math.geom.Quat4f;

@ServerEvent
public class SetSwingingBladeRoot implements Event {
    private EntityRef swingingBladeRoot;
    private float timePeriod = 15f;
    private float offset = 0f;
    private boolean isSwinging = true;
    private float amplitude = 3.14f / 6;
    private Quat4f rotation;

    public SetSwingingBladeRoot() {
    }

    public SetSwingingBladeRoot(EntityRef swingingBladeRoot, float timePeriod, float offset, boolean isSwinging,
                                float amplitude, Quat4f rotation) {
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

    public Quat4f getRotation() {
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
