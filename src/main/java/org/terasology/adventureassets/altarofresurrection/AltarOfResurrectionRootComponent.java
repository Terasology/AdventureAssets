// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.altarofresurrection;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.world.block.ForceBlockActive;

/**
 * This component is attached to every altar of resurrection root entity.
 */

@ForceBlockActive
public class AltarOfResurrectionRootComponent implements Component {

    public EntityRef meshEntity;
    public EntityRef orbEntity;
    public EntityRef colliderEntity;
}
