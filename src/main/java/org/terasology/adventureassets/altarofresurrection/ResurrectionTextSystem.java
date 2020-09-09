// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.altarofresurrection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.entitySystem.entity.EntityBuilder;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.engine.input.cameraTarget.CameraTargetSystem;
import org.terasology.engine.logic.location.Location;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.logic.players.LocalPlayer;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.logic.FloatingTextComponent;
import org.terasology.math.geom.Quat4f;
import org.terasology.math.geom.Vector3f;
import org.terasology.nui.Color;

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
                EntityRef activatedAltarOfResurrection =
                        clientInfo.getComponent(RevivePlayerComponent.class).altarOfResurrectionEntity;
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
