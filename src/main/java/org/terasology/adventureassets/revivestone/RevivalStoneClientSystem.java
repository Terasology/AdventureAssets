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
import org.terasology.entitySystem.entity.lifecycleEvents.OnChangedComponent;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.logic.location.Location;
import org.terasology.logic.location.LocationComponent;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.math.geom.Quat4f;
import org.terasology.math.geom.Vector3f;
import org.terasology.registry.In;
import org.terasology.rendering.logic.LightComponent;
import org.terasology.rendering.logic.MeshComponent;
import org.terasology.utilities.Assets;
import org.terasology.world.block.BlockComponent;

@RegisterSystem(RegisterMode.CLIENT)
public class RevivalStoneClientSystem extends BaseComponentSystem {

    private static final Logger logger = LoggerFactory.getLogger(RevivalStoneClientSystem.class);
    EntityRef activatedRevivalStone;
    @In
    private LocalPlayer localPlayer;
    @In
    private AssetManager assetManager;
    @In
    private EntityManager entityManager;

    /**
     * This method creates the mesh and the orb entity for the model visual and lighting once the Revival Stone is
     * placed in the world, upon activation of the {@link RevivalStoneRootComponent}
     *
     * @param event
     * @param entity
     * @param revivalStoneRootComponent
     */
    @ReceiveEvent(components = {RevivalStoneRootComponent.class, BlockComponent.class})
    public void onRevivalStoneCreated(OnActivatedComponent event, EntityRef entity, RevivalStoneRootComponent revivalStoneRootComponent) {
        logger.info("client onRevivalStoneCreated");
        Prefab angelMeshPrefab = assetManager.getAsset("AdventureAssets:revivalStoneMesh", Prefab.class).get();
        EntityBuilder angelMeshEntityBuilder = entityManager.newBuilder(angelMeshPrefab);
        angelMeshEntityBuilder.setOwner(entity);
        angelMeshEntityBuilder.setPersistent(false);
        EntityRef angelMesh = angelMeshEntityBuilder.build();
        Location.attachChild(entity, angelMesh, new Vector3f(0, 1f, 0), new Quat4f(Quat4f.IDENTITY));
        revivalStoneRootComponent.meshEntity = angelMesh;

        Prefab angelOrbPrefab = assetManager.getAsset("AdventureAssets:revivalStoneOrb", Prefab.class).get();
        EntityBuilder angelOrbEntityBuilder = entityManager.newBuilder(angelOrbPrefab);
        angelOrbEntityBuilder.setOwner(entity);
        angelOrbEntityBuilder.setPersistent(false);
        EntityRef angelOrb = angelOrbEntityBuilder.build();
        Location.attachChild(entity, angelOrb, new Vector3f(1f, 1.7f, 0), new Quat4f(Quat4f.IDENTITY));
        revivalStoneRootComponent.orbEntity = angelOrb;
        entity.saveComponent(revivalStoneRootComponent);

        // If a revival stone entity becomes active later, it is still activated
        EntityRef clientInfo = localPlayer.getClientInfoEntity();
        if (clientInfo.hasComponent(RevivePlayerComponent.class)) {
            RevivePlayerComponent revivePlayerComponent = clientInfo.getComponent(RevivePlayerComponent.class);
            if (entity.equals(revivePlayerComponent.revivalStoneEntity)) {
                activateRevivalStone(entity);
                activatedRevivalStone = entity;
            }
        }
    }

    /**
     * This method deals with the destruction of the revival stone. The orb and the mesh entities on the client side
     * are destroyed. If the revival stone is activated, the {@link RevivePlayerComponent} is removed from the local player.
     *
     * @param event
     * @param entityRef
     * @param revivalStoneRootComponent
     */
    @ReceiveEvent
    public void onRemove(BeforeRemoveComponent event, EntityRef entityRef, RevivalStoneRootComponent revivalStoneRootComponent) {
        logger.info("client onRemove");
        revivalStoneRootComponent.meshEntity.destroy();
        revivalStoneRootComponent.orbEntity.destroy();
    }

    /**
     * This method listens for the activation of the {@link RevivePlayerComponent} which is attached to the client info
     * when the {@link ActivateEvent} is handled in the {@link RevivalStoneServerSystem}. This method triggers the
     * activation of the revival stone which includes texture change and particle effects.
     *
     * @param event
     * @param entity
     * @param revivePlayerComponent
     */
    @ReceiveEvent(components = {RevivePlayerComponent.class})
    public void onRevivePlayerActivate(OnActivatedComponent event, EntityRef entity, RevivePlayerComponent revivePlayerComponent) {
        logger.info("client onRevivePlayerActivate");
        EntityRef revivalStone = revivePlayerComponent.revivalStoneEntity;
        activateRevivalStone(revivalStone);
        activatedRevivalStone = revivalStone;
    }

    /**
     * This method listens for the deactivation of the {@link RevivePlayerComponent}. This happens to an already
     * activated revival stone upon activation of a new revival stone, or on deactivation/destruction of a revival stone.
     *
     * @param event
     * @param entity
     * @param revivePlayerComponent
     */
    @ReceiveEvent
    public void onRevivePlayerRemove(BeforeRemoveComponent event, EntityRef entity, RevivePlayerComponent revivePlayerComponent) {
        logger.info("client onRevivePlayerRemove");
        EntityRef revivalStone = revivePlayerComponent.revivalStoneEntity;
        deactivateRevivalStone(revivalStone);
        activatedRevivalStone = null;
    }

    @ReceiveEvent
    public void setRevivePlayerChange(OnChangedComponent event, EntityRef entity, RevivePlayerComponent revivePlayerComponent) {
        logger.info("client onRevivePlayerChange");
        deactivateRevivalStone(activatedRevivalStone);
        EntityRef revivalStone = revivePlayerComponent.revivalStoneEntity;
        activateRevivalStone(revivalStone);
        activatedRevivalStone = revivalStone;
    }

    private void activateRevivalStone(EntityRef revivalStone) {
        RevivalStoneRootComponent revivalStoneRootComponent = revivalStone.getComponent(RevivalStoneRootComponent.class);
        Vector3f location = revivalStone.getComponent(LocationComponent.class).getWorldPosition();

        spawnParticlesOnActivate(location);
        changeMeshToActive(revivalStoneRootComponent.meshEntity);
        lightenOrbEntity(revivalStoneRootComponent.orbEntity);
    }

    private void deactivateRevivalStone(EntityRef revivalStone) {
        RevivalStoneRootComponent revivalStoneRootComponent = revivalStone.getComponent(RevivalStoneRootComponent.class);
        Vector3f location = revivalStone.getComponent(LocationComponent.class).getWorldPosition();

        spawnParticlesOnDeactivate(location);
        changeMeshToInactive(revivalStoneRootComponent.meshEntity);
        darkenOrbEntity(revivalStoneRootComponent.orbEntity);
    }

    private void lightenOrbEntity(EntityRef orbEntity) {
        orbEntity.addComponent(new LightComponent());
    }

    private void darkenOrbEntity(EntityRef orbEntity) {
        orbEntity.removeComponent(LightComponent.class);
    }

    private void changeMeshToInactive(EntityRef meshEntity) {
        MeshComponent meshComponent = meshEntity.getComponent(MeshComponent.class);
        meshComponent.material = Assets.getMaterial("AdventureAssets:angelInactive").get();
        meshEntity.saveComponent(meshComponent);
        meshEntity.removeComponent(LightComponent.class);
        meshEntity.getOwner().removeComponent(LightComponent.class);
    }

    private void changeMeshToActive(EntityRef meshEntity) {
        MeshComponent meshComponent = meshEntity.getComponent(MeshComponent.class);
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
}
