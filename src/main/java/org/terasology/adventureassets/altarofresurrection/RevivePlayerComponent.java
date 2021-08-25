// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.altarofresurrection;

import org.joml.Vector3f;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.Replicate;
import org.terasology.gestalt.entitysystem.component.Component;

/**
 * This component makes a player respawn near the altar of resurrection. It is attached to the clientInfo entity which always
 * remains active. This is done so that this component can be removed upon the destruction of the concerned altar of resurrection
 * entity even when the player entity is inactive.
 */

public class RevivePlayerComponent implements Component<RevivePlayerComponent> {
    @Replicate
    public Vector3f location;
    @Replicate
    public EntityRef altarOfResurrectionEntity;

    @Override
    public void copyFrom(RevivePlayerComponent other) {
        this.location = new Vector3f(other.location);
        this.altarOfResurrectionEntity = other.altarOfResurrectionEntity;
    }
}
