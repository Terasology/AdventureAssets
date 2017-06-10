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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.assets.management.AssetManager;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityBuilder;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.lifecycleEvents.BeforeRemoveComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.OnAddedComponent;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.health.BeforeDamagedEvent;
import org.terasology.logic.health.BeforeDestroyEvent;
import org.terasology.logic.health.DestroyEvent;
import org.terasology.logic.health.DoDestroyEvent;
import org.terasology.logic.location.Location;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Quat4f;
import org.terasology.math.geom.Vector3f;
import org.terasology.registry.In;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.block.BlockComponent;
import org.terasology.world.block.entity.CreateBlockDropsEvent;
import org.terasology.world.block.items.OnBlockItemPlaced;

@RegisterSystem(RegisterMode.CLIENT)
public class SwingingBladeClientSystem extends BaseComponentSystem implements UpdateSubscriberSystem {

    private static final Logger logger = LoggerFactory.getLogger(SwingingBladeClientSystem.class);

    @In
    private EntityManager entityManager;
    @In
    private AssetManager assetManager;
    @In
    private Time time;

    @ReceiveEvent(components = {SwingingBladeComponent.class, LocationComponent.class, BlockComponent.class})
    public void onSwingingBladeCreated(OnActivatedComponent event, EntityRef entity,
                                       SwingingBladeComponent swingingBladeComponent) {
        Prefab swingingBladePrefab = assetManager.getAsset("AdventureAssets:swingingBladeMesh", Prefab.class).get();
        EntityBuilder swingingBladeEntityBuilder = entityManager.newBuilder(swingingBladePrefab);
        swingingBladeEntityBuilder.setOwner(entity);
        swingingBladeEntityBuilder.setPersistent(false);
        EntityRef swingingBladeMesh = swingingBladeEntityBuilder.build();
        swingingBladeComponent.childrenEntities.add(swingingBladeMesh);
        entity.saveComponent(swingingBladeComponent);
        Location.attachChild(entity, swingingBladeMesh, new Vector3f(Vector3f.zero()), new Quat4f(Quat4f.IDENTITY));
    }

    @Override
    public void update(float delta) {
        for (EntityRef blade : entityManager.getEntitiesWith(SwingingBladeComponent.class)) {
            LocationComponent locationComponent = blade.getComponent(LocationComponent.class);
            SwingingBladeComponent swingingBladeComponent = blade.getComponent(SwingingBladeComponent.class);
            if (locationComponent != null && swingingBladeComponent.isSwinging) {
                float t = time.getGameTime();
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
}
