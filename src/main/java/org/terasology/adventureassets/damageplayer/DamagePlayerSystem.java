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
package org.terasology.adventureassets.damageplayer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.characters.AliveCharacterComponent;
import org.terasology.logic.characters.CharacterImpulseEvent;
import org.terasology.logic.health.event.DoDamageEvent;
import org.terasology.logic.health.EngineDamageTypes;
import org.terasology.math.JomlUtil;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Vector3f;
import org.terasology.physics.events.CollideEvent;
import org.terasology.registry.In;

@RegisterSystem(RegisterMode.AUTHORITY)
public class DamagePlayerSystem extends BaseComponentSystem {

    private static final Logger logger = LoggerFactory.getLogger(DamagePlayerSystem.class);

    @In
    private EntityManager entityManager;

    @ReceiveEvent
    public void onCollide(CollideEvent event, EntityRef entity, DamagePlayerComponent damagePlayerComponent) {
        EntityRef player = event.getOtherEntity();
        if (player.hasComponent(AliveCharacterComponent.class)) {
            player.send(new CharacterImpulseEvent(event.getNormal().mul(-1 * damagePlayerComponent.recoil, new org.joml.Vector3f())));
            player.send(new DoDamageEvent(TeraMath.floorToInt(damagePlayerComponent.damage), EngineDamageTypes.PHYSICAL.get(), entity));
        }
    }
}
