// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.traps.fireballlauncher;

import org.joml.Vector3f;
import org.terasology.engine.network.Replicate;
import org.terasology.engine.world.block.ForceBlockActive;
import org.terasology.gestalt.entitysystem.component.Component;

/**
 * This component holds the data for a Fireball Launcher
 */

@ForceBlockActive
public class FireballLauncherComponent implements Component<FireballLauncherComponent> {

    /**
     * Sets the fireball launcher as active or inactive
     */
    @Replicate
    public boolean isFiring = true;

    /**
     * Time between two strikes
     */
    @Replicate
    public float timePeriod = 2f;

    /**
     * Time offset for synchronization of multiple launchers Two launchers having the same time period can operate at
     * offsets to fire at different instances
     */
    @Replicate
    public float offset = 0f;

    /**
     * Last shot time
     */
    @Replicate
    public float lastShotTime = 0f;

    /**
     * Direction to fire
     */
    @Replicate
    public Vector3f direction = new Vector3f(0, 0, 1);

    /**
     * Distance till which fireball lasts
     */
    @Replicate
    public int maxDistance = 24;

    /**
     * Total collective damage that can be inflicted
     */
    @Replicate
    public int damageAmount = 20;

    @Override
    public void copy(FireballLauncherComponent other) {
        this.isFiring = other.isFiring;
        this.timePeriod = other.timePeriod;
        this.offset = other.offset;
        this.lastShotTime = other.lastShotTime;
        this.direction = new Vector3f(other.direction);
        this.maxDistance = other.maxDistance;
        this.damageAmount = other.damageAmount;
    }
}
