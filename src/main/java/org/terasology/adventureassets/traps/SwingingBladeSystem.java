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
import org.terasology.assets.management.AssetManager;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityBuilder;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.characters.CharacterImpulseEvent;
import org.terasology.logic.health.DoDamageEvent;
import org.terasology.logic.health.EngineDamageTypes;
import org.terasology.logic.inventory.InventoryManager;
import org.terasology.logic.location.Location;
import org.terasology.logic.location.LocationComponent;
import org.terasology.logic.players.event.OnPlayerSpawnedEvent;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Quat4f;
import org.terasology.math.geom.Vector3f;
import org.terasology.physics.events.CollideEvent;
import org.terasology.physics.events.ImpulseEvent;
import org.terasology.registry.CoreRegistry;
import org.terasology.registry.In;
import org.terasology.structureTemplates.events.StructureSpawnerFromToolboxRequest;

@RegisterSystem
public class SwingingBladeSystem extends BaseComponentSystem implements UpdateSubscriberSystem {

    private static final Logger logger = LoggerFactory.getLogger(SwingingBladeSystem.class);

    @In
    private EntityManager entityManager;

    @ReceiveEvent(priority = EventPriority.PRIORITY_LOW)
    public void onPlayerSpawnedEvent(OnPlayerSpawnedEvent event, EntityRef player) {
        EntityRef toolbox = entityManager.create("StructureTemplates:toolbox");
        CoreRegistry.get(InventoryManager.class).giveItem(player, EntityRef.NULL, toolbox);
        Prefab prefab = CoreRegistry.get(AssetManager.class).getAsset("AdventureAssets:bladeRoom", Prefab.class).get();
        toolbox.send(new StructureSpawnerFromToolboxRequest(prefab));
    }

    @ReceiveEvent
    public void onCollide(CollideEvent event, EntityRef entity, DamagePlayerComponent damagePlayerComponent) {
        logger.info("collision detected with " + event.getOtherEntity().getParentPrefab().getName());
        logger.info("normal: " + event.getNormal());
        event.getOtherEntity().send(new CharacterImpulseEvent(new Vector3f(event.getNormal()).mul(-10)));
        event.getOtherEntity().send(new DoDamageEvent(TeraMath.floorToInt(damagePlayerComponent.damage), EngineDamageTypes.PHYSICAL.get(), entity));
    }

    @ReceiveEvent(components = {SwingingBladeComponent.class, LocationComponent.class})
    public void onSwingingBladeCreated(OnActivatedComponent event, EntityRef entity,
                                       SwingingBladeComponent swingingBladeComponent) {
        Prefab bladePrefab = CoreRegistry.get(AssetManager.class).getAsset("AdventureAssets:blade", Prefab.class).get();
        EntityBuilder entityBuilder = entityManager.newBuilder(bladePrefab);
        entityBuilder.setOwner(entity);
        EntityRef blade = entityBuilder.build();
        Location.attachChild(entity, blade, new Vector3f(0, -6, 0), new Quat4f(Quat4f.IDENTITY));
    }

    @Override
    public void update(float delta) {
        for (EntityRef blade : entityManager.getEntitiesWith(SwingingBladeComponent.class)) {
            LocationComponent locationComponent = blade.getComponent(LocationComponent.class);
            SwingingBladeComponent swingingBladeComponent = blade.getComponent(SwingingBladeComponent.class);
            if (locationComponent != null) {
                float t = CoreRegistry.get(Time.class).getGameTime();
                float T = swingingBladeComponent.timePeriod;
                float pitch, A = swingingBladeComponent.amplitude, phi = swingingBladeComponent.offset;
                float w = (float) (2 * Math.PI / T);
                pitch = (float) (A * Math.cos(w * t + phi));
                Quat4f rotation = locationComponent.getLocalRotation();
                locationComponent.setLocalRotation(new Quat4f(rotation.getYaw(), pitch, rotation.getRoll()));
                blade.saveComponent(locationComponent);
            }
        }
    }
}
