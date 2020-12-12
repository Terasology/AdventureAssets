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

import org.joml.Quaternionf;
import org.joml.Vector3f;
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
import org.terasology.network.ClientComponent;
import org.terasology.network.ClientInfoComponent;
import org.terasology.registry.In;
import org.terasology.world.block.BlockComponent;

@RegisterSystem(RegisterMode.AUTHORITY)
public class ResurrectionServerSystem extends BaseComponentSystem {

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
        ClientComponent clientComponent = entity.getComponent(ClientComponent.class);
        EntityRef character = clientComponent.character;
        EntityRef clientInfo = clientComponent.clientInfo;
        if (clientInfo.hasComponent(RevivePlayerComponent.class)) {
            Vector3f spawnPosition = clientInfo.getComponent(RevivePlayerComponent.class).location;
            LocationComponent loc = character.getComponent(LocationComponent.class);
            loc.setWorldPosition(spawnPosition);
            loc.setLocalRotation(new Quaternionf());
            character.saveComponent(loc);
        }
    }

    /**
     * This method creates the collider entity for the model once the altar of resurrection is placed in the world, upon
     * activation of the {@link AltarOfResurrectionRootComponent}.
     *
     * @param event
     * @param entity
     * @param altarOfResurrectionRootComponent
     */
    @ReceiveEvent(components = {AltarOfResurrectionRootComponent.class, BlockComponent.class})
    public void onAltarOfResurrectionCreated(OnActivatedComponent event, EntityRef entity, AltarOfResurrectionRootComponent altarOfResurrectionRootComponent) {
        Prefab angelColliderPrefab = assetManager.getAsset("AdventureAssets:altarOfResurrectionCollider", Prefab.class).get();
        EntityBuilder angelColliderEntityBuilder = entityManager.newBuilder(angelColliderPrefab);
        angelColliderEntityBuilder.setOwner(entity);
        angelColliderEntityBuilder.setPersistent(true);
        EntityRef angelCollider = angelColliderEntityBuilder.build();
        Location.attachChild(entity, angelCollider, new Vector3f(0, 1f, 0), new Quaternionf());
        altarOfResurrectionRootComponent.colliderEntity = angelCollider;
        entity.saveComponent(altarOfResurrectionRootComponent);
    }

    /**
     * This method deals with the destruction of the altar of resurrection. The collider entity on the server side is destroyed.
     * In addition, any clientInfo entity that has the {@link RevivePlayerComponent} for the same altar of resurrection entity
     * being destroyed, has its {@link RevivePlayerComponent} removed.
     *
     * @param event
     * @param entity
     * @param altarOfResurrectionRootComponent
     */
    @ReceiveEvent
    public void onRemove(BeforeRemoveComponent event, EntityRef entity, AltarOfResurrectionRootComponent altarOfResurrectionRootComponent) {
        altarOfResurrectionRootComponent.colliderEntity.destroy();

        // Removes RevivePlayerComponent from clientInfo upon destruction of an altar of resurrection
        for (EntityRef clientInfo : entityManager.getEntitiesWith(RevivePlayerComponent.class)) {
            RevivePlayerComponent revivePlayerComponent = clientInfo.getComponent(RevivePlayerComponent.class);
            if (revivePlayerComponent.altarOfResurrectionEntity.equals(entity)) {
                clientInfo.removeComponent(RevivePlayerComponent.class);
                EntityRef client = clientInfo.getComponent(ClientInfoComponent.class).client;
                client.send(new NotificationMessageEvent("Deactivated Altar of Resurrection due to destruction", client));
            }
        }
    }

    /**
     * Receives the ActivateEvent for the activation of the mesh. Passes the ActivateEvent to the root entity.
     *
     * @param event
     * @param entity
     */
    @ReceiveEvent(priority = EventPriority.PRIORITY_HIGH, components = {AltarOfResurrectionColliderComponent.class})
    public void onActivate(ActivateEvent event, EntityRef entity) {
        entity.getOwner().send(event);
        event.consume();
    }

    /**
     * Receives the AttackEvent for the attack on the mesh. Passes the event to the root entity.
     *
     * @param event
     * @param targetEntity
     */
    @ReceiveEvent(priority = EventPriority.PRIORITY_HIGH, components = {AltarOfResurrectionColliderComponent.class})
    public void onAttackEntity(AttackEvent event, EntityRef targetEntity) {
        targetEntity.getOwner().send(event);
        event.consume();
    }

    /**
     * Handles the ActivateEvent for the root entity. Depending on whether the altar of resurrection is activated for the client
     * or not, the altar of resurrection gets activated or deactivated.
     *
     * @param event
     * @param entity
     * @param altarOfResurrectionRootComponent
     */
    @ReceiveEvent
    public void onAltarOfResurrectionInteract(ActivateEvent event, EntityRef entity, AltarOfResurrectionRootComponent altarOfResurrectionRootComponent) {
        EntityRef clientInfo = event.getInstigator().getOwner().getComponent(ClientComponent.class).clientInfo;

        if (clientInfo.hasComponent(RevivePlayerComponent.class)) {
            EntityRef prevAltarOfResurrection = clientInfo.getComponent(RevivePlayerComponent.class).altarOfResurrectionEntity;
            if (entity.equals(prevAltarOfResurrection)) {
                clientInfo.removeComponent(RevivePlayerComponent.class);
            } else {
                clientInfo.removeComponent(RevivePlayerComponent.class);
                addRevivePlayerComponent(clientInfo, entity);
                /* Note: Despite a remove and add component happening on the clientInfo entity above, the event is
                   collectively received as a OnChangedComponent on the client system. */
            }
        } else {
            addRevivePlayerComponent(clientInfo, entity);
        }
    }

    private void addRevivePlayerComponent(EntityRef clientInfo, EntityRef altarOfResurrection) {
        Vector3f location = altarOfResurrection.getComponent(LocationComponent.class).getWorldPosition(new Vector3f());
        RevivePlayerComponent revivePlayerComponent = new RevivePlayerComponent();
        revivePlayerComponent.location = location.add(1, 0, 1);
        revivePlayerComponent.altarOfResurrectionEntity = altarOfResurrection;
        clientInfo.addComponent(revivePlayerComponent);
    }
}
