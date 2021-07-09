// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.traps.swingingblade.structuretemplateintegration;

import org.joml.Quaternionf;
import org.joml.Vector3i;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.reflection.MappedContainer;
import org.terasology.structureTemplates.events.SpawnStructureEvent;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This component is intended to be used in structure templates.
 * <p>
 * It adds items (incl. block items) to one ore more chests when the entity receives a
 * {@link SpawnStructureEvent}.
 */
public class AddSwingingBladeComponent implements Component<AddSwingingBladeComponent> {
    public List<SwingingBladesToSpawn> swingingBladesToSpawn;

    @Override
    public void copy(AddSwingingBladeComponent other) {
        this.swingingBladesToSpawn = other.swingingBladesToSpawn.stream()
                .map(SwingingBladesToSpawn::copy)
                .collect(Collectors.toList());
    }

    @MappedContainer
    public static class SwingingBladesToSpawn {
        public Vector3i position;
        public Quaternionf rotation = new Quaternionf();
        public float timePeriod = 2f;
        public float amplitude = 3.14f / 6;
        public float offset = 0f;
        public boolean isSwinging = true;

        SwingingBladesToSpawn copy() {
            SwingingBladesToSpawn newToSpawn = new SwingingBladesToSpawn();
            newToSpawn.position = new Vector3i(this.position);
            newToSpawn.rotation = new Quaternionf(this.rotation);
            newToSpawn.timePeriod = this.timePeriod;
            newToSpawn.amplitude = this.amplitude;
            newToSpawn.offset = this.offset;
            newToSpawn.isSwinging = this.isSwinging;
            return newToSpawn;
        }
    }
}
