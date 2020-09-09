// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.damageplayer;

import org.terasology.engine.entitySystem.Component;

/**
 * This component marks a rigid body for dealing damage to the player.
 */

public class DamagePlayerComponent implements Component {
    public float damage = 10;
    public float recoil = 5;
}
