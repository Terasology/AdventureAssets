// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.traps.wipeout;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.network.ServerEvent;
import org.terasology.math.geom.Quat4f;

@ServerEvent
public class SetWipeOutRoot implements Event {
    private EntityRef wipeOutRoot;
    private float timePeriod = 15f;
    private float offset = 0f;
    private boolean isRotating = true;
    private int direction = 1;
    private Quat4f rotation;

    public SetWipeOutRoot() {
    }

    public SetWipeOutRoot(EntityRef wipeOutRoot, float timePeriod, float offset, boolean isRotating, int direction,
                          Quat4f rotation) {
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

    public Quat4f getRotation() {
        return rotation;
    }

    public EntityRef getWipeOutRoot() {
        return wipeOutRoot;
    }
}
