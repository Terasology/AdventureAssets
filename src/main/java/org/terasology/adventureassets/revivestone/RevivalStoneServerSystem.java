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
package org.terasology.adventureassets.revivestone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.assets.management.AssetManager;
import org.terasology.entitySystem.entity.EntityBuilder;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.lifecycleEvents.BeforeRemoveComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.characters.events.AttackEvent;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.logic.location.Location;
import org.terasology.logic.location.LocationComponent;
import org.terasology.logic.notifications.NotificationMessageEvent;
import org.terasology.logic.players.event.RespawnRequestEvent;
import org.terasology.math.geom.Quat4f;
import org.terasology.math.geom.Vector3f;
import org.terasology.network.ClientComponent;
import org.terasology.network.ClientInfoComponent;
import org.terasology.registry.In;
import org.terasology.world.block.BlockComponent;

@RegisterSystem(RegisterMode.AUTHORITY)
public class RevivalStoneServerSystem extends BaseComponentSystem {

    private static final Logger logger = LoggerFactory.getLogger(RevivalStoneServerSystem.class);

    @In
    private AssetManager assetManager;
    @In
    private EntityManager entityManager;

    /**
     * This method intercepts the RespawnRequestEvent and makes a change to the LocationComponent of the client after
     * the LocationComponent has already been changed to have the spawn location according to World Generator information.
     *
     * @param event
     * @param entity
     */
    @ReceiveEvent(priority = EventPriority.PRIORITY_HIGH, components = {ClientComponent.class})
    public void setSpawnLocationOnRespawnRequest(RespawnRequestEvent event, EntityRef entity) {
        logger.info("server setSpawnLocationOnRespawnRequest");
        EntityRef clientInfo = entity.getComponent(ClientComponent.class).clientInfo;
        if (clientInfo.hasComponent(RevivePlayerComponent.class)) {
            Vector3f spawnPosition = clientInfo.getComponent(RevivePlayerComponent.class).location;
            LocationComponent loc = entity.getComponent(LocationComponent.class);
            loc.setWorldPosition(spawnPosition);
            loc.setLocalRotation(new Quat4f());
            entity.saveComponent(loc);
        }
    }

    @ReceiveEvent(components = {RevivalStoneRootComponent.class, BlockComponent.class})
    public void onRevivalStoneCreated(OnActivatedComponent event, EntityRef entity, RevivalStoneRootComponent revivalStoneRootComponent) {
        logger.info("server onRevivalStoneCreated");
        Prefab angelColliderPrefab = assetManager.getAsset("AdventureAssets:revivalStoneCollider", Prefab.class).get();
        EntityBuilder angelColliderEntityBuilder = entityManager.newBuilder(angelColliderPrefab);
        angelColliderEntityBuilder.setOwner(entity);
        angelColliderEntityBuilder.setPersistent(true);
        EntityRef angelCollider = angelColliderEntityBuilder.build();
        Location.attachChild(entity, angelCollider, new Vector3f(0, 1f, 0), new Quat4f(Quat4f.IDENTITY));
        revivalStoneRootComponent.colliderEntity = angelCollider;
        entity.saveComponent(revivalStoneRootComponent);
    }

    @ReceiveEvent
    public void onRemove(BeforeRemoveComponent event, EntityRef entity, RevivalStoneRootComponent revivalStoneRootComponent) {
        logger.info("server onRemove");
        revivalStoneRootComponent.colliderEntity.destroy();

        // Removes RevivePlayerComponent from clientInfo upon destruction of a revival stone
        for (EntityRef clientInfo : entityManager.getEntitiesWith(RevivePlayerComponent.class)) {
            RevivePlayerComponent revivePlayerComponent = clientInfo.getComponent(RevivePlayerComponent.class);
            if (revivePlayerComponent.revivalStoneEntity.equals(entity)) {
                clientInfo.removeComponent(RevivePlayerComponent.class);
                EntityRef client = clientInfo.getComponent(ClientInfoComponent.class).client;
                client.send(new NotificationMessageEvent("Deactivated Revival Stone due to destruction", client));
            }
        }
    }

    @ReceiveEvent(priority = EventPriority.PRIORITY_HIGH, components = {RevivalStoneColliderComponent.class})
    public void onActivate(ActivateEvent event, EntityRef entity) {
        logger.info("server onActivate");
        entity.getOwner().send(event);
        event.consume();
    }

    @ReceiveEvent(priority = EventPriority.PRIORITY_HIGH, components = {RevivalStoneColliderComponent.class})
    public void onAttackEntity(AttackEvent event, EntityRef targetEntity) {
        logger.info("server onAttackEntity");
        targetEntity.getOwner().send(event);
        event.consume();
    }

    @ReceiveEvent
    public void onRevivalStoneInteract(ActivateEvent event, EntityRef entity, RevivalStoneRootComponent revivalStoneRootComponent) {
        logger.info("server onRevivalStoneInteract");
        EntityRef client = event.getInstigator().getOwner();
        EntityRef clientInfo = client.getComponent(ClientComponent.class).clientInfo;

        if (clientInfo.hasComponent(RevivePlayerComponent.class)) {
            EntityRef prevRevivalStone = clientInfo.getComponent(RevivePlayerComponent.class).revivalStoneEntity;
            if (entity.equals(prevRevivalStone)) {
                clientInfo.removeComponent(RevivePlayerComponent.class);
                client.send(new NotificationMessageEvent("Deactivated Revival Stone", client));
            } else {
                clientInfo.removeComponent(RevivePlayerComponent.class);
                addRevivePlayerComponent(clientInfo, entity);
                client.send(new NotificationMessageEvent("Activated this Revival Stone and deactivated the previous.", client));
            }
        } else {
            addRevivePlayerComponent(clientInfo, entity);
            client.send(new NotificationMessageEvent("Activated Revival Stone", client));
        }
    }

    private void addRevivePlayerComponent(EntityRef clientInfo, EntityRef revivalStone) {
        Vector3f location = revivalStone.getComponent(LocationComponent.class).getWorldPosition();
        RevivePlayerComponent revivePlayerComponent = new RevivePlayerComponent();
        revivePlayerComponent.location = location.add(1, 0, 1);
        revivePlayerComponent.revivalStoneEntity = revivalStone;
        clientInfo.addComponent(revivePlayerComponent);
    }
}
