// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.altarofresurrection;

import org.terasology.engine.entitySystem.entity.EntityBuilder;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.BeforeRemoveComponent;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.engine.entitySystem.event.EventPriority;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.characters.events.AttackEvent;
import org.terasology.engine.logic.common.ActivateEvent;
import org.terasology.engine.logic.location.Location;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.logic.notifications.NotificationMessageEvent;
import org.terasology.engine.logic.players.event.RespawnRequestEvent;
import org.terasology.engine.network.ClientComponent;
import org.terasology.engine.network.ClientInfoComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.block.BlockComponent;
import org.terasology.gestalt.assets.management.AssetManager;
import org.terasology.math.geom.Quat4f;
import org.terasology.math.geom.Vector3f;

@RegisterSystem(RegisterMode.AUTHORITY)
public class ResurrectionServerSystem extends BaseComponentSystem {

    @In
    private AssetManager assetManager;
    @In
    private EntityManager entityManager;

    /**
     * This method intercepts the RespawnRequestEvent and makes a change to the LocationComponent of the client after
     * the LocationComponent has already been changed to have the spawn location according to World Generator
     * information.
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
            loc.setLocalRotation(new Quat4f());
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
    public void onAltarOfResurrectionCreated(OnActivatedComponent event, EntityRef entity,
                                             AltarOfResurrectionRootComponent altarOfResurrectionRootComponent) {
        Prefab angelColliderPrefab = assetManager.getAsset("AdventureAssets:altarOfResurrectionCollider",
                Prefab.class).get();
        EntityBuilder angelColliderEntityBuilder = entityManager.newBuilder(angelColliderPrefab);
        angelColliderEntityBuilder.setOwner(entity);
        angelColliderEntityBuilder.setPersistent(true);
        EntityRef angelCollider = angelColliderEntityBuilder.build();
        Location.attachChild(entity, angelCollider, new Vector3f(0, 1f, 0), new Quat4f(Quat4f.IDENTITY));
        altarOfResurrectionRootComponent.colliderEntity = angelCollider;
        entity.saveComponent(altarOfResurrectionRootComponent);
    }

    /**
     * This method deals with the destruction of the altar of resurrection. The collider entity on the server side is
     * destroyed. In addition, any clientInfo entity that has the {@link RevivePlayerComponent} for the same altar of
     * resurrection entity being destroyed, has its {@link RevivePlayerComponent} removed.
     *
     * @param event
     * @param entity
     * @param altarOfResurrectionRootComponent
     */
    @ReceiveEvent
    public void onRemove(BeforeRemoveComponent event, EntityRef entity,
                         AltarOfResurrectionRootComponent altarOfResurrectionRootComponent) {
        altarOfResurrectionRootComponent.colliderEntity.destroy();

        // Removes RevivePlayerComponent from clientInfo upon destruction of an altar of resurrection
        for (EntityRef clientInfo : entityManager.getEntitiesWith(RevivePlayerComponent.class)) {
            RevivePlayerComponent revivePlayerComponent = clientInfo.getComponent(RevivePlayerComponent.class);
            if (revivePlayerComponent.altarOfResurrectionEntity.equals(entity)) {
                clientInfo.removeComponent(RevivePlayerComponent.class);
                EntityRef client = clientInfo.getComponent(ClientInfoComponent.class).client;
                client.send(new NotificationMessageEvent("Deactivated Altar of Resurrection due to destruction",
                        client));
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
     * Handles the ActivateEvent for the root entity. Depending on whether the altar of resurrection is activated for
     * the client or not, the altar of resurrection gets activated or deactivated.
     *
     * @param event
     * @param entity
     * @param altarOfResurrectionRootComponent
     */
    @ReceiveEvent
    public void onAltarOfResurrectionInteract(ActivateEvent event, EntityRef entity,
                                              AltarOfResurrectionRootComponent altarOfResurrectionRootComponent) {
        EntityRef clientInfo = event.getInstigator().getOwner().getComponent(ClientComponent.class).clientInfo;

        if (clientInfo.hasComponent(RevivePlayerComponent.class)) {
            EntityRef prevAltarOfResurrection =
                    clientInfo.getComponent(RevivePlayerComponent.class).altarOfResurrectionEntity;
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
        Vector3f location = altarOfResurrection.getComponent(LocationComponent.class).getWorldPosition();
        RevivePlayerComponent revivePlayerComponent = new RevivePlayerComponent();
        revivePlayerComponent.location = location.add(1, 0, 1);
        revivePlayerComponent.altarOfResurrectionEntity = altarOfResurrection;
        clientInfo.addComponent(revivePlayerComponent);
    }
}
