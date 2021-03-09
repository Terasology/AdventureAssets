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

import com.google.common.collect.Lists;
import org.joml.Quaternionf;
import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.Replicate;
import org.terasology.engine.world.block.ForceBlockActive;

import java.util.List;

/**
 * This component holds the data for a Swinging Blade.
 */

@ForceBlockActive
public class SwingingBladeComponent implements Component {
    /**
     * Time taken by the swinging blade to complete one two and fro motion (in seconds)
     */
    @Replicate
    public float timePeriod = 2f;

    /**
     * Maximum angle to which the swinging blade rotates (in radians)
     */
    @Replicate
    public float amplitude = 3.14f / 6;

    /**
     * Phase difference or offset (in radians)
     */
    @Replicate
    public float offset = 0f;

    /**
     * To set the blade in motion, or stop it
     */
    @Replicate
    public boolean isSwinging = true;

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
