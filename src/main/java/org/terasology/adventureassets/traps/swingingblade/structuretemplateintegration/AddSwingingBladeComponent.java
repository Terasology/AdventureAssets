// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.traps.swingingblade.structuretemplateintegration;

import org.terasology.engine.entitySystem.Component;
import org.terasology.math.geom.Quat4f;
import org.terasology.math.geom.Vector3i;
import org.terasology.nui.reflection.MappedContainer;
import org.terasology.structureTemplates.events.SpawnStructureEvent;

import java.util.List;

/**
 * This component is intended to be used in structure templates.
 * <p>
 * It adds items (incl. block items) to one ore more chests when the entity receives a {@link SpawnStructureEvent}.
 */
public class AddSwingingBladeComponent implements Component {
    public List<SwingingBladesToSpawn> swingingBladesToSpawn;

    @MappedContainer
    public static class SwingingBladesToSpawn {
        public Vector3i position;
        public Quat4f rotation = new Quat4f(0, 0, 0, 1);
        public float timePeriod = 2f;
        public float amplitude = 3.14f / 6;
        public float offset = 0f;
        public boolean isSwinging = true;
    }
}
