// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.traps.wipeout;

import com.google.common.collect.Lists;
import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.Replicate;
import org.terasology.engine.world.block.ForceBlockActive;
import org.terasology.math.geom.Quat4f;

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
    public Quat4f rotation = new Quat4f(Quat4f.IDENTITY);

    @Replicate
    public List<EntityRef> childrenEntities = Lists.newArrayList();
}
