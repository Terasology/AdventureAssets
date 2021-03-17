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
package org.terasology.adventureassets.traps.fireballlauncher;

import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.assets.management.AssetManager;
import org.terasology.engine.core.Time;
import org.terasology.engine.entitySystem.entity.EntityBuilder;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.health.HealthComponent;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.block.BlockComponent;
import org.terasology.engine.world.block.items.BlockItemComponent;
import org.terasology.engine.world.block.items.OnBlockItemPlaced;
import org.terasology.engine.world.block.items.OnBlockToItem;
import org.terasology.projectile.FireProjectileEvent;
import org.terasology.projectile.ProjectileActionComponent;

@RegisterSystem(RegisterMode.AUTHORITY)
public class FireballLauncherServerSystem extends BaseComponentSystem implements UpdateSubscriberSystem {

    private static final Logger logger = LoggerFactory.getLogger(FireballLauncherServerSystem.class);

    @In
    private EntityManager entityManager;
    @In
    private Time time;
    @In
    private AssetManager assetManager;

    /**
     * Save the Fireball Launcher settings by saving the {@link FireballLauncherComponent}
     *
     * @param event
     * @param blockEntity
     * @param fireballLauncherComponent
     */
    @ReceiveEvent
    public void onBlockToItem(OnBlockToItem event, EntityRef blockEntity, FireballLauncherComponent fireballLauncherComponent) {
        event.getItem().addOrSaveComponent(fireballLauncherComponent);
    }

    /**
     * Transfer the saved Fireball Launcher components from the item to block
     *
     * @param event
     * @param itemEntity
     * @param fireballLauncherComponent
     */
    @ReceiveEvent(components = {BlockItemComponent.class})
    public void onItemToBlock(OnBlockItemPlaced event, EntityRef itemEntity,
                              FireballLauncherComponent fireballLauncherComponent) {
        EntityRef entity = event.getPlacedBlock();
        entity.addOrSaveComponent(fireballLauncherComponent);
    }

    /**
     * Saves the settings to the server FireballLauncherRoot entity once the Ok button is clicked on the settings screen.
     * All changes are then replicated to the client entities.
     *
     * @param event
     * @param player
     */
    @ReceiveEvent
    public void setFireballLauncher(SetFireballLauncherEvent event, EntityRef player) {
        EntityRef fireballLauncherRoot = event.getFireballLauncherRoot();
        FireballLauncherComponent fireballLauncherComponent = fireballLauncherRoot.getComponent(FireballLauncherComponent.class);
        fireballLauncherComponent.isFiring = event.isFiring();
        fireballLauncherComponent.timePeriod = event.getTimePeriod();
        fireballLauncherComponent.damageAmount = event.getDamageAmount();
        fireballLauncherComponent.maxDistance = event.getMaxDistance();
        fireballLauncherComponent.offset = event.getOffset();
        fireballLauncherComponent.direction = event.getDirection();

        fireballLauncherRoot.saveComponent(fireballLauncherComponent);
    }

    /**
     * Find all Fireball Launchers and trigger the launch of a Fireball if it is the right time
     *
     * @param delta The time (in seconds) since the last engine update.
     */
    @Override
    public void update(float delta) {
        for (EntityRef fireballLauncher : entityManager.getEntitiesWith(FireballLauncherComponent.class, BlockComponent.class)) {
            FireballLauncherComponent fireballLauncherComponent = fireballLauncher.getComponent(FireballLauncherComponent.class);
            if (fireballLauncherComponent.isFiring && time.getGameTime() > fireballLauncherComponent.timePeriod + fireballLauncherComponent.lastShotTime) {
                Prefab fireballPrefab = assetManager.getAsset("Projectile:fireball", Prefab.class).get();
                EntityBuilder fireballEntityBuilder = entityManager.newBuilder(fireballPrefab);
                EntityRef fireball = fireballEntityBuilder.build();

                ProjectileActionComponent projectileActionComponent = fireball.getComponent(ProjectileActionComponent.class);
                projectileActionComponent.maxDistance = fireballLauncherComponent.maxDistance;
                fireball.saveComponent(projectileActionComponent);

                HealthComponent healthComponent = fireball.getComponent(HealthComponent.class);
                healthComponent.maxHealth = fireballLauncherComponent.damageAmount;
                healthComponent.currentHealth = fireballLauncherComponent.damageAmount;
                fireball.saveComponent(healthComponent);

                Vector3f pos = fireballLauncher.getComponent(LocationComponent.class).getWorldPosition(new Vector3f());
                fireball.send(new FireProjectileEvent(pos, fireballLauncherComponent.direction));

                fireballLauncherComponent.lastShotTime = (float) Math.floor(time.getGameTime() / fireballLauncherComponent.timePeriod)
                        * fireballLauncherComponent.timePeriod + fireballLauncherComponent.offset;
                fireballLauncher.saveComponent(fireballLauncherComponent);
            }
        }
    }
}
