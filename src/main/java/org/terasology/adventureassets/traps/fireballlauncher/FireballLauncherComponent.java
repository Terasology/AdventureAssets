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

import org.terasology.entitySystem.Component;
import org.terasology.math.geom.Vector3f;
import org.terasology.world.block.ForceBlockActive;

/**
 * This component holds the data for a Fireball Launcher
 */

@ForceBlockActive
public class FireballLauncherComponent implements Component {

    /**
     * Sets the fireball launcher as active or inactive
     */
    public boolean isFiring = true;

    /**
     * Time between two strikes
     */
    public float timePeriod = 2f;

    /**
     * Time offset for synchronization of multiple launchers
     * Two launchers having the same time period can operate at offsets to fire at different instances
     */
    public float offset = 0f;

    /**
     * Last shot time
     */
    public float lastShotTime = 0f;

    /**
     * Direction to fire
     */
    public Vector3f direction = Vector3f.north();

    /**
     * Distance till which fireball lasts
     */
    public int maxDistance = 24;

    /**
     * Total collective damage that can be inflicted
     */
    public int damageAmount = 20;
}
