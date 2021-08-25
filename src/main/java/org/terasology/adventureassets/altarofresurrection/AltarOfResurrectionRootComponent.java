// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.altarofresurrection;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.world.block.ForceBlockActive;
import org.terasology.gestalt.entitysystem.component.Component;

/**
 * This component is attached to every altar of resurrection root entity.
 */

@ForceBlockActive
public class AltarOfResurrectionRootComponent implements Component<AltarOfResurrectionRootComponent> {

    public EntityRef meshEntity;
    public EntityRef orbEntity;
    public EntityRef colliderEntity;

    @Override
    public void copyFrom(AltarOfResurrectionRootComponent other) {
        this.meshEntity = other.meshEntity;
        this.orbEntity = other.meshEntity;
        this.colliderEntity = other.colliderEntity;
    }
}
