// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.traps.swingingblade;

import com.google.common.collect.Lists;
import org.joml.Quaternionf;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.Replicate;
import org.terasology.engine.world.block.ForceBlockActive;
import org.terasology.gestalt.entitysystem.component.Component;

import java.util.List;

/**
 * This component holds the data for a Swinging Blade.
 */

@ForceBlockActive
public class SwingingBladeComponent implements Component<SwingingBladeComponent> {
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

    @Override
    public void copy(SwingingBladeComponent other) {
        this.timePeriod = other.timePeriod;
        this.amplitude = other.amplitude;
        this.offset = other.offset;
        this.isSwinging = other.isSwinging;
        this.rotation = new Quaternionf(other.rotation);
        this.lastAngle = other.lastAngle;
        this.childrenEntities = Lists.newArrayList(other.childrenEntities);
    }
}
