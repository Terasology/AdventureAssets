// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.adventureassets.traps.mcqButtonDoor;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.network.Replicate;
import org.terasology.engine.world.block.ForceBlockActive;

import java.util.Arrays;
import java.util.List;

@ForceBlockActive
public class McqButtonDoorComponent implements Component {
    @Replicate
    public String title = "title";
    @Replicate
    public String message = "message";
    @Replicate
    public String password = "password";
    @Replicate
    public List<String> options = Arrays.asList("password", "password1", "password2");
}
