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

import com.google.common.collect.Lists;
import org.joml.Quaternionf;
import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.network.Replicate;
import org.terasology.world.block.ForceBlockActive;

import java.util.List;

/**
 * This component holds the data for a Wipe Out setup.
 */

@ForceBlockActive
public class WipeOutComponent implements Component {
    /**
     * Time taken by the wipe out to complete one circle (in seconds)
     */
    @Replicate
    public float timePeriod = 15f;

    /**
     * Offset, a fraction of time-period(in seconds)
     */
    @Replicate
    public float offset = 0f;

    /**
     * To set the wipe out in motion, or stop it
     */
    @Replicate
    public boolean isRotating = true;

    /**
     * To set the wipe out motion direction (1: anticlockwise, -1:clockwise)
     */
    @Replicate
    public int direction = 1;

    /**
     * Saved rotation extracted when block turns to item
     */
    @Replicate
    public Quaternionf rotation = new Quaternionf();

    @Replicate
    public float lastAngle = 0f;

    @Replicate
    public List<EntityRef> childrenEntities = Lists.newArrayList();
}
