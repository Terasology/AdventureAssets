// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.adventureassets.traps.mcqButtonDoor;

import com.google.common.collect.Lists;
import org.terasology.engine.network.Replicate;
import org.terasology.engine.world.block.ForceBlockActive;
import org.terasology.gestalt.entitysystem.component.Component;

import java.util.Arrays;
import java.util.List;

@ForceBlockActive
public class McqButtonDoorComponent implements Component<McqButtonDoorComponent> {
    @Replicate
    public String title = "title";
    @Replicate
    public String message = "message";
    @Replicate
    public String password = "password";
    @Replicate
    public List<String> options = Arrays.asList("password", "password1", "password2");

    @Override
    public void copyFrom(McqButtonDoorComponent other) {
        this.title = other.title;
        this.message = other.message;
        this.password = other.password;
        this.options = Lists.newArrayList(other.options);
    }
}
