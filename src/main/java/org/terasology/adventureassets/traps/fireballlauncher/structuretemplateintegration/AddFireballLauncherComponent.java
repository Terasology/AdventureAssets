// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.traps.fireballlauncher.structuretemplateintegration;

import org.terasology.engine.entitySystem.Component;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.nui.reflection.MappedContainer;
import org.terasology.structureTemplates.events.SpawnStructureEvent;

import java.util.List;

/**
 * This component is intended to be used in structure templates.
 * <p>
 * It adds items (incl. block items) to one ore more chests when the entity receives a {@link SpawnStructureEvent}.
 */
public class AddFireballLauncherComponent implements Component {
    public List<FireballLauncherToSpawn> fireballLaunchersToSpawn;

    @MappedContainer
    public static class FireballLauncherToSpawn {
        public Vector3i position;
        public boolean isFiring;
        public float timePeriod;
        public float offset;
        public Vector3f direction;
        public int maxDistance;
        public int damageAmount;
    }
}
