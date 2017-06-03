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
package org.terasology.adventureassets.traps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.console.commandSystem.annotations.Command;
import org.terasology.logic.console.commandSystem.annotations.CommandParam;
import org.terasology.logic.location.LocationComponent;
import org.terasology.logic.permission.PermissionManager;
import org.terasology.math.geom.Quat4f;
import org.terasology.registry.CoreRegistry;
import org.terasology.registry.In;

@RegisterSystem
public class SwingingBladeSystem extends BaseComponentSystem implements UpdateSubscriberSystem {

    private static final Logger logger = LoggerFactory.getLogger(SwingingBladeSystem.class);

    @In
    private EntityManager entityManager;

    @Command(shortDescription = "Set rotation for all blades", runOnServer = true,
            requiredPermission = PermissionManager.USER_MANAGEMENT_PERMISSION)
    public String setBladeRotation(@CommandParam("x") float x, @CommandParam("y") float y, @CommandParam("z") float z) {

        for (EntityRef blade : entityManager.getEntitiesWith(SwingingBladeComponent.class)) {
            LocationComponent locationComponent = blade.getComponent(LocationComponent.class);
            if (locationComponent != null) {
                locationComponent.setLocalRotation(new Quat4f(x, y, z));
                blade.saveComponent(locationComponent);
            }
        }

        return "Changed rotation of all blades";
    }

    @Override
    public void update(float delta) {
        for (EntityRef blade : entityManager.getEntitiesWith(SwingingBladeComponent.class)) {
            LocationComponent locationComponent = blade.getComponent(LocationComponent.class);
            SwingingBladeComponent swingingBladeComponent = blade.getComponent(SwingingBladeComponent.class);
            if (locationComponent != null) {
                float gameTime = CoreRegistry.get(Time.class).getGameTime();
                float timePeriod = swingingBladeComponent.timePeriod;
                float pitch, amplitude = swingingBladeComponent.amplitude, t = gameTime % timePeriod;
                if (t < timePeriod / 2) {
                    pitch = amplitude * (-1 + 4 * t / timePeriod);
                } else {
                    pitch = amplitude * (1 - 4 * (t - timePeriod / 2) / timePeriod);
                }
                Quat4f rotation = locationComponent.getLocalRotation();
                locationComponent.setLocalRotation(new Quat4f(rotation.getYaw(), pitch, rotation.getRoll()));
                blade.saveComponent(locationComponent);
            }
        }
    }
}
