// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.traps.fireballlauncher.structuretemplateintegration;

import org.joml.Vector3f;
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
public class AddFireballLauncherComponent implements Component<AddFireballLauncherComponent> {
    public List<FireballLauncherToSpawn> fireballLaunchersToSpawn;

    @Override
    public void copy(AddFireballLauncherComponent other) {
        this.fireballLaunchersToSpawn = other.fireballLaunchersToSpawn.stream()
                .map(FireballLauncherToSpawn::copy)
                .collect(Collectors.toList());
    }

    @MappedContainer
    public static class FireballLauncherToSpawn {
        public Vector3i position;
        public boolean isFiring;
        public float timePeriod;
        public float offset;
        public Vector3f direction;
        public int maxDistance;
        public int damageAmount;

        FireballLauncherToSpawn copy() {
            FireballLauncherToSpawn newFL = new FireballLauncherToSpawn();
            newFL.position = new Vector3i(this.position);
            newFL.isFiring = this.isFiring;
            newFL.timePeriod = this.timePeriod;
            newFL.offset = this.offset;
            newFL.direction = new Vector3f(this.direction);
            newFL.maxDistance = this.maxDistance;
            newFL.damageAmount = this.damageAmount;
            return newFL;
        }
    }
}
