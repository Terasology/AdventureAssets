// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.adventureassets.traps.passwordDoor;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.network.Replicate;
import org.terasology.engine.world.block.ForceBlockActive;

@ForceBlockActive
public class PasswordDoorComponent implements Component {
    @Replicate
    public String title = "title";
    @Replicate
    public String message = "message";
    @Replicate
    public String password = "password";
}
