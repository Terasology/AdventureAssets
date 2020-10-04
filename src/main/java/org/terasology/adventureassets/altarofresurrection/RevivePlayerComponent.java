/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.adventureassets.altarofresurrection;

import org.joml.Vector3f;
import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.network.Replicate;

/**
 * This component makes a player respawn near the altar of resurrection. It is attached to the clientInfo entity which always
 * remains active. This is done so that this component can be removed upon the destruction of the concerned altar of resurrection
 * entity even when the player entity is inactive.
 */

public class RevivePlayerComponent implements Component {
    @Replicate
    public Vector3f location;
    @Replicate
    public EntityRef altarOfResurrectionEntity;
}
