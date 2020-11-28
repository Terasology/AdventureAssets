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
package org.terasology.adventureassets.traps.fireballlauncher;

import org.joml.Vector3f;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.Event;
import org.terasology.network.ServerEvent;

@ServerEvent
public class SetFireballLauncherEvent implements Event {
    private EntityRef fireballLauncherRoot;
    private boolean isFiring = true;
    private float timePeriod = 2f;
    private float offset = 0f;
    private Vector3f direction = new Vector3f(0,0,1);
    private int maxDistance = 24;
    private int damageAmount = 20;

    public SetFireballLauncherEvent() {
        fireballLauncherRoot = EntityRef.NULL;
    }

    public SetFireballLauncherEvent(EntityRef doorEntity, boolean isFiring, float timePeriod, float offset, Vector3f direction, int maxDistance, int damageAmount) {
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
