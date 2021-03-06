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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.location.LocationComponent;

final class WipeOutUtilities {

    private static final Logger logger = LoggerFactory.getLogger(WipeOutUtilities.class);
    private WipeOutUtilities() {
    }

    static void rotateWipeOut(EntityRef wipeOut, float gameTime) {
        LocationComponent locationComponent = wipeOut.getComponent(LocationComponent.class);
        WipeOutComponent wipeOutComponent = wipeOut.getComponent(WipeOutComponent.class);
        if (locationComponent != null && wipeOutComponent.isRotating) {
            float timePeriod = wipeOutComponent.timePeriod;
            float offset = wipeOutComponent.offset;
            float targetAngle = (float) (((gameTime + offset) % timePeriod) * (2 * Math.PI / timePeriod)) * wipeOutComponent.direction;

            float angle = Math.abs(targetAngle - wipeOutComponent.lastAngle);
            wipeOutComponent.lastAngle = targetAngle;
            locationComponent.setLocalRotation(locationComponent.getLocalRotation().rotateLocalY(angle, new Quaternionf()));
            wipeOut.saveComponent(locationComponent);
        }
    }
}
