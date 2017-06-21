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
package org.terasology.adventureassets.revivestone;

import org.terasology.entitySystem.Component;
import org.terasology.math.geom.Vector3f;

/**
 * This component makes a player respawn near the revival stone
 */

public class RevivePlayerComponent implements Component {
    public Vector3f location;

    public RevivePlayerComponent(Vector3f location) {
        this.location = location;
    }
}
