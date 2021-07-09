// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.traps.wipeout.structuretemplateintegration;

import com.google.common.collect.Lists;
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
public class AddWipeOutComponent implements Component<AddWipeOutComponent> {
    public List<WipeOutsToSpawn> wipeOutsToSpawn = Lists.newArrayList();

    @Override
    public void copy(AddWipeOutComponent other) {
        this.wipeOutsToSpawn = other.wipeOutsToSpawn.stream()
                .map(WipeOutsToSpawn::copy)
                .collect(Collectors.toList());
    }

    @MappedContainer
    public static class WipeOutsToSpawn {
        public Vector3i position;
        public Quaternionf rotation = new Quaternionf();
        public float timePeriod = 2f;
        public int direction = 1;
        public float offset = 0f;
        public boolean isRotating = true;

        WipeOutsToSpawn copy() {
            WipeOutsToSpawn newWipe = new WipeOutsToSpawn();
            newWipe.position = new Vector3i(this.position);
            newWipe.rotation = new Quaternionf(this.rotation);
            newWipe.timePeriod = this.timePeriod;
            newWipe.direction = this.direction;
            newWipe.offset = this.offset;
            newWipe.isRotating = this.isRotating;
            return newWipe;
        }
    }
}
