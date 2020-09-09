// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.altarofresurrection;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.Replicate;
import org.terasology.math.geom.Vector3f;

/**
 * This component makes a player respawn near the altar of resurrection. It is attached to the clientInfo entity which
 * always remains active. This is done so that this component can be removed upon the destruction of the concerned altar
 * of resurrection entity even when the player entity is inactive.
 */

public class RevivePlayerComponent implements Component {
    @Replicate
    public Vector3f location;
    @Replicate
    public EntityRef altarOfResurrectionEntity;
}
