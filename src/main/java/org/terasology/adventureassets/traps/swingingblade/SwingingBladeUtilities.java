// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.traps.swingingblade;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.math.geom.Quat4f;

class SwingingBladeUtilities {

    static void rotateSwingingBlade(EntityRef blade, float gameTime) {
        LocationComponent locationComponent = blade.getComponent(LocationComponent.class);
        SwingingBladeComponent swingingBladeComponent = blade.getComponent(SwingingBladeComponent.class);
        if (locationComponent != null && swingingBladeComponent.isSwinging) {
            float t = gameTime;
            float timePeriod = swingingBladeComponent.timePeriod;
            float pitch, A = swingingBladeComponent.amplitude, phi = swingingBladeComponent.offset;
            float w = (float) (2 * Math.PI / timePeriod);
            pitch = (float) (A * Math.cos(w * t + phi));
            Quat4f rotation = locationComponent.getLocalRotation();
            locationComponent.setLocalRotation(new Quat4f(rotation.getYaw(), pitch, rotation.getRoll()));
            blade.saveComponent(locationComponent);
        }
    }
}
