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
package org.terasology.adventureassets.traps.wipeout;

import org.joml.Quaternionf;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Quat4f;

final class WipeOutUtilities {
    private WipeOutUtilities() {
    }

    static void rotateWipeOut(EntityRef wipeOut, float gameTime) {
        LocationComponent locationComponent = wipeOut.getComponent(LocationComponent.class);
        WipeOutComponent wipeOutComponent = wipeOut.getComponent(WipeOutComponent.class);
        if (locationComponent != null && wipeOutComponent.isRotating) {
            float timePeriod = wipeOutComponent.timePeriod;
            float offset = wipeOutComponent.offset;
            float angle =
                (float) (((gameTime + offset) % timePeriod) * (2 * Math.PI / timePeriod)) * wipeOutComponent.direction;
            Quat4f rotation = locationComponent.getLocalRotation();
            locationComponent.setLocalRotation(new Quaternionf().rotationYXZ(angle, rotation.getPitch(),
                rotation.getRoll()));
            wipeOut.saveComponent(locationComponent);
        }
    }
}
