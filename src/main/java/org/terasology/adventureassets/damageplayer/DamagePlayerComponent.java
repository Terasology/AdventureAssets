// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.damageplayer;

import org.terasology.gestalt.entitysystem.component.Component;

/**
 * This component marks a rigid body for dealing damage to the player.
 */

public class DamagePlayerComponent implements Component<DamagePlayerComponent> {
    public float damage = 10;
    public float recoil = 5;

    @Override
    public void copyFrom(DamagePlayerComponent other) {
        this.damage = other.damage;
        this.recoil = other.recoil;
    }
}
