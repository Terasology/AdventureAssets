// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.damageplayer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.characters.AliveCharacterComponent;
import org.terasology.engine.logic.characters.CharacterImpulseEvent;
import org.terasology.engine.logic.destruction.EngineDamageTypes;
import org.terasology.engine.physics.events.CollideEvent;
import org.terasology.engine.registry.In;
import org.terasology.health.logic.event.DoDamageEvent;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Vector3f;

@RegisterSystem(RegisterMode.AUTHORITY)
public class DamagePlayerSystem extends BaseComponentSystem {

    private static final Logger logger = LoggerFactory.getLogger(DamagePlayerSystem.class);

    @In
    private EntityManager entityManager;

    @ReceiveEvent
    public void onCollide(CollideEvent event, EntityRef entity, DamagePlayerComponent damagePlayerComponent) {
        EntityRef player = event.getOtherEntity();
        if (player.hasComponent(AliveCharacterComponent.class)) {
            player.send(new CharacterImpulseEvent(new Vector3f(event.getNormal()).mul(-1 * damagePlayerComponent.recoil)));
            player.send(new DoDamageEvent(TeraMath.floorToInt(damagePlayerComponent.damage),
                    EngineDamageTypes.PHYSICAL.get(), entity));
        }
    }
}
