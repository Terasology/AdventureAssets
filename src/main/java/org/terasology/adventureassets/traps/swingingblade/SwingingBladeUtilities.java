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
package org.terasology.adventureassets.traps.swingingblade;

import org.joml.Quaternionf;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.location.LocationComponent;

class SwingingBladeUtilities {

    static void rotateSwingingBlade(EntityRef blade, float gameTime) {
        LocationComponent locationComponent = blade.getComponent(LocationComponent.class);
        SwingingBladeComponent swingingBladeComponent = blade.getComponent(SwingingBladeComponent.class);
        if (locationComponent != null && swingingBladeComponent.isSwinging) {
            float timePeriod = swingingBladeComponent.timePeriod;
            float a = swingingBladeComponent.amplitude;
            float phi = swingingBladeComponent.offset;
            float w = (float) (2 * Math.PI / timePeriod);
            float pitch = (float) (a * Math.cos(w * gameTime + phi));

            float delta = pitch - swingingBladeComponent.lastAngle;
            swingingBladeComponent.lastAngle = pitch;
            //FIXME: the blades always swing around the X-axis and don't adapt to how the anchor block is rotated
            locationComponent.setLocalRotation(
                    locationComponent.getLocalRotation().rotateLocalX(delta, new Quaternionf())
            );
            blade.saveComponent(locationComponent);
        }
    }
}
