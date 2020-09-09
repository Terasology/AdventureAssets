// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.traps.fireballlauncher;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.network.ServerEvent;
import org.terasology.math.geom.Vector3f;

@ServerEvent
public class SetFireballLauncherEvent implements Event {
    private final EntityRef fireballLauncherRoot;
    private boolean isFiring = true;
    private float timePeriod = 2f;
    private float offset = 0f;
    private Vector3f direction = Vector3f.north();
    private int maxDistance = 24;
    private int damageAmount = 20;

    public SetFireballLauncherEvent() {
        fireballLauncherRoot = EntityRef.NULL;
    }

    public SetFireballLauncherEvent(EntityRef doorEntity, boolean isFiring, float timePeriod, float offset,
                                    Vector3f direction, int maxDistance, int damageAmount) {
        this.fireballLauncherRoot = doorEntity;
        this.isFiring = isFiring;
        this.timePeriod = timePeriod;
        this.offset = offset;
        this.direction = direction;
        this.maxDistance = maxDistance;
        this.damageAmount = damageAmount;
    }

    public EntityRef getFireballLauncherRoot() {
        return fireballLauncherRoot;
    }

    public boolean isFiring() {
        return isFiring;
    }

    public float getTimePeriod() {
        return timePeriod;
    }

    public float getOffset() {
        return offset;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public int getDamageAmount() {
        return damageAmount;
    }

    public int getMaxDistance() {
        return maxDistance;
    }
}
