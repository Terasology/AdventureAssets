// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.traps.wipeout;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.math.geom.Quat4f;

class WipeOutUtilities {

    static void rotateWipeOut(EntityRef wipeOut, float gameTime) {
        LocationComponent locationComponent = wipeOut.getComponent(LocationComponent.class);
        WipeOutComponent wipeOutComponent = wipeOut.getComponent(WipeOutComponent.class);
        if (locationComponent != null && wipeOutComponent.isRotating) {
            float t = gameTime;
            float timePeriod = wipeOutComponent.timePeriod;
            float offset = wipeOutComponent.offset;
            float angle =
                    (float) (((t + offset) % timePeriod) * (2 * Math.PI / timePeriod)) * wipeOutComponent.direction;
            Quat4f rotation = locationComponent.getLocalRotation();
            locationComponent.setLocalRotation(new Quat4f(angle, rotation.getPitch(), rotation.getRoll()));
            wipeOut.saveComponent(locationComponent);
        }
    }
}
