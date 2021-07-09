// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.adventureassets.traps.passwordDoor;

import org.terasology.engine.network.Replicate;
import org.terasology.engine.world.block.ForceBlockActive;
import org.terasology.gestalt.entitysystem.component.Component;

@ForceBlockActive
public class PasswordDoorComponent implements Component<PasswordDoorComponent> {
    @Replicate
    public String title = "title";
    @Replicate
    public String message = "message";
    @Replicate
    public String password = "password";

    @Override
    public void copy(PasswordDoorComponent other) {
        this.title = other.title;
        this.message = other.message;
        this.password = other.password;
    }
}
