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

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.adventureassets.traps.swingingblade.SwingingBladeComponent;
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
import org.terasology.logic.common.ActivateEvent;
import org.terasology.logic.location.Location;
import org.terasology.logic.location.LocationComponent;
import org.terasology.logic.notifications.NotificationMessageEvent;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.logic.players.event.RespawnRequestEvent;
import org.terasology.math.geom.Quat4f;
import org.terasology.math.geom.Vector3f;
import org.terasology.network.ClientComponent;
import org.terasology.registry.In;
import org.terasology.rendering.assets.material.Material;
import org.terasology.rendering.logic.LightComponent;
import org.terasology.rendering.logic.MeshComponent;
import org.terasology.utilities.Assets;
import org.terasology.world.block.BlockComponent;
import org.terasology.world.block.items.OnBlockToItem;

@RegisterSystem(RegisterMode.CLIENT)
public class RevivalStoneClientSystem extends BaseComponentSystem {

    private static final Logger logger = LoggerFactory.getLogger(RevivalStoneClientSystem.class);

    @In
    private LocalPlayer localPlayer;
    @In
    private AssetManager assetManager;
    @In
    private EntityManager entityManager;

    @ReceiveEvent(components = {RevivalStoneComponent.class, BlockComponent.class})
    public void onRevivalStoneCreated(OnActivatedComponent event, EntityRef entity, RevivalStoneComponent revivalStoneComponent) {
        Prefab angelMeshPrefab = assetManager.getAsset("AdventureAssets:revivalStoneMesh", Prefab.class).get();
        EntityBuilder angelMeshEntityBuilder = entityManager.newBuilder(angelMeshPrefab);
        angelMeshEntityBuilder.setOwner(entity);
        angelMeshEntityBuilder.setPersistent(false);
        EntityRef angelMesh = angelMeshEntityBuilder.build();
        revivalStoneComponent.meshEntity = angelMesh;
        entity.saveComponent(revivalStoneComponent);
        Location.attachChild(entity, angelMesh, new Vector3f(0, 1f, 0), new Quat4f(Quat4f.IDENTITY));
    }

    @ReceiveEvent
    public void onBlockToItem(OnBlockToItem event, EntityRef blockEntity, RevivalStoneComponent revivalStoneComponent) {
        revivalStoneComponent.meshEntity.destroy();
    }

    @ReceiveEvent(priority = EventPriority.PRIORITY_HIGH, components = {ClientComponent.class})
    public void setSpawnLocationOnRespawnRequest(RespawnRequestEvent event, EntityRef entity) {
        EntityRef character = entity.getComponent(ClientComponent.class).character;
        if (character.hasComponent(RevivePlayerComponent.class)) {
            Vector3f spawnPosition = character.getComponent(RevivePlayerComponent.class).location;
            LocationComponent loc = entity.getComponent(LocationComponent.class);
            loc.setWorldPosition(spawnPosition);
            loc.setLocalRotation(new Quat4f());
            entity.saveComponent(loc);
        }
    }

    @ReceiveEvent
    public void onRevivalStoneInteract(ActivateEvent event, EntityRef entity, RevivalStoneComponent revivalStoneComponent) {
        EntityRef player = event.getInstigator();
        EntityRef client = player.getOwner();
        Vector3f location = entity.getComponent(LocationComponent.class).getWorldPosition();

        if (revivalStoneComponent.activated) {
            revivalStoneComponent.activated = false;
            client.send(new NotificationMessageEvent("Deactivated Revival Stone", client));
            spawnParticlesOnDeactivate(location);
            changeMeshToInactive(revivalStoneComponent.meshEntity);
            player.removeComponent(RevivePlayerComponent.class);
        } else {
            if (player.hasComponent(RevivePlayerComponent.class)) {
                EntityRef prevRevivalStone = player.getComponent(RevivePlayerComponent.class).revivalStoneEntity;
                RevivalStoneComponent prevRevivalStoneComponent = prevRevivalStone.getComponent(RevivalStoneComponent.class);
                prevRevivalStoneComponent.activated = false;
                spawnParticlesOnDeactivate(prevRevivalStone.getComponent(LocationComponent.class).getWorldPosition());
                changeMeshToInactive(prevRevivalStoneComponent.meshEntity);
                prevRevivalStone.saveComponent(prevRevivalStoneComponent);
                player.removeComponent(RevivePlayerComponent.class);
                client.send(new NotificationMessageEvent("Deactivated the previous Revival Stone", client));
            }
            revivalStoneComponent.activated = true;
            client.send(new NotificationMessageEvent("Activated Revival Stone", client));

            spawnParticlesOnActivate(location);
            changeMeshToActive(revivalStoneComponent.meshEntity);

            RevivePlayerComponent revivePlayerComponent = new RevivePlayerComponent();
            revivePlayerComponent.location = location.add(1, 0, 1);
            revivePlayerComponent.revivalStoneEntity = entity;
            player.addComponent(revivePlayerComponent);
        }
        entity.saveComponent(revivalStoneComponent);
    }

    private void changeMeshToInactive(EntityRef meshEntity) {
        MeshComponent meshComponent= meshEntity.getComponent(MeshComponent.class);
        meshComponent.material = Assets.getMaterial("AdventureAssets:angelInactive").get();
        meshEntity.saveComponent(meshComponent);
        meshEntity.removeComponent(LightComponent.class);
        meshEntity.getOwner().removeComponent(LightComponent.class);
    }

    private void changeMeshToActive(EntityRef meshEntity) {
        MeshComponent meshComponent= meshEntity.getComponent(MeshComponent.class);
        meshComponent.material = Assets.getMaterial("AdventureAssets:angelActive").get();
        meshEntity.saveComponent(meshComponent);
        meshEntity.addComponent(new LightComponent());
        meshEntity.getOwner().addComponent(new LightComponent());
    }

    private void spawnParticlesOnActivate(Vector3f spawnPos) {
        // Create rising blue particles
        EntityBuilder entityBuilder = entityManager.newBuilder("AdventureAssets:revivalStoneParticleEffect");
        LocationComponent locationComponent = entityBuilder.getComponent(LocationComponent.class);
        locationComponent.setWorldPosition(spawnPos);
        entityBuilder.build();
        // Create a smoke explosion
        entityBuilder = entityManager.newBuilder("core:smokeExplosion");
        locationComponent = entityBuilder.getComponent(LocationComponent.class);
        locationComponent.setWorldPosition(spawnPos);
        entityBuilder.build();
    }

    private void spawnParticlesOnDeactivate(Vector3f spawnPos) {
        // Create a smoke explosion
        EntityBuilder entityBuilder = entityManager.newBuilder("core:smokeExplosion");
        LocationComponent locationComponent = entityBuilder.getComponent(LocationComponent.class);
        locationComponent.setWorldPosition(spawnPos);
        entityBuilder.build();
    }

    @ReceiveEvent
    public void onRemove(BeforeRemoveComponent event, EntityRef entityRef, RevivalStoneComponent revivalStoneComponent) {
        if (revivalStoneComponent.activated) {
            localPlayer.getCharacterEntity().removeComponent(RevivePlayerComponent.class);
            EntityRef client = localPlayer.getClientEntity();
            client.send(new NotificationMessageEvent("Deactivated Revival Stone due to destruction", client));
        }
    }
}
