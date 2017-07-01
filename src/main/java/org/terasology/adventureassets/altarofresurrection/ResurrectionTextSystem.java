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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityBuilder;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.input.cameraTarget.CameraTargetSystem;
import org.terasology.logic.location.Location;
import org.terasology.logic.location.LocationComponent;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.math.geom.Quat4f;
import org.terasology.math.geom.Vector3f;
import org.terasology.registry.In;
import org.terasology.rendering.logic.FloatingTextComponent;
import org.terasology.rendering.nui.Color;

@RegisterSystem(RegisterMode.CLIENT)
public class ResurrectionTextSystem extends BaseComponentSystem implements UpdateSubscriberSystem {

    private static final Logger logger = LoggerFactory.getLogger(ResurrectionTextSystem.class);

    @In
    private CameraTargetSystem cameraTargetSystem;
    @In
    private LocalPlayer localPlayer;
    @In
    private EntityManager entityManager;

    private EntityRef floatingTextEntity = null;

    @Override
    public void postBegin() {
        EntityBuilder floatingTextBuilder = entityManager.newBuilder();
        floatingTextBuilder.setPersistent(false);
        floatingTextEntity = floatingTextBuilder.build();
    }

    @Override
    public void update(float delta) {
        if (cameraTargetSystem.isTargetAvailable()) {
            EntityRef targetEntity = cameraTargetSystem.getTarget();
            if (!(targetEntity.hasComponent(AltarOfResurrectionColliderComponent.class) ||
                    targetEntity.hasComponent(AltarOfResurrectionRootComponent.class))) {
                floatingTextEntity.removeComponent(LocationComponent.class);
                return;
            }

            FloatingTextComponent floatingTextComponent = new FloatingTextComponent();
            floatingTextComponent.scale = 1f;
            LocationComponent locationComponent = new LocationComponent();

            if (targetEntity.hasComponent(AltarOfResurrectionColliderComponent.class)) {
                targetEntity = targetEntity.getOwner();
            }
            floatingTextComponent.text = "Activate to revive here";
            floatingTextComponent.textColor = Color.GREEN;

            EntityRef clientInfo = localPlayer.getClientInfoEntity();
            if (clientInfo.hasComponent(RevivePlayerComponent.class)) {
                EntityRef activatedAltarOfResurrection = clientInfo.getComponent(RevivePlayerComponent.class).altarOfResurrectionEntity;
                if (activatedAltarOfResurrection.equals(targetEntity)) {
                    floatingTextComponent.text = "Altar of Resurrection Activated";
                    floatingTextComponent.textColor = Color.YELLOW;
                } else {
                    floatingTextComponent.text = "Activate to revive here\n(will deactivate previous)";
                    floatingTextComponent.textColor = Color.CYAN;
                }
            }

            floatingTextEntity.addOrSaveComponent(floatingTextComponent);
            floatingTextEntity.addOrSaveComponent(locationComponent);
            Location.attachChild(targetEntity, floatingTextEntity, new Vector3f(0, 1.0f, 0), new Quat4f(1, 0, 0, 0));
        }
    }
}
