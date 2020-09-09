// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.traps.fireballlauncher;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.network.Replicate;
import org.terasology.engine.world.block.ForceBlockActive;
import org.terasology.math.geom.Vector3f;

/**
 * This component holds the data for a Fireball Launcher
 */

@ForceBlockActive
public class FireballLauncherComponent implements Component {

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
    public Vector3f direction = Vector3f.north();

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
}
