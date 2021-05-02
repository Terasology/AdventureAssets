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
import org.terasology.gestalt.assets.management.AssetManager;
import org.terasology.engine.entitySystem.entity.EntityBuilder;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.BeforeRemoveComponent;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.OnChangedComponent;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.common.ActivateEvent;
import org.terasology.engine.logic.location.Location;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.logic.players.LocalPlayer;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.logic.LightComponent;
import org.terasology.engine.rendering.logic.MeshComponent;
import org.terasology.engine.utilities.Assets;
import org.terasology.engine.world.block.BlockComponent;

@RegisterSystem(RegisterMode.CLIENT)
public class ResurrectionClientSystem extends BaseComponentSystem {

    EntityRef activatedAltarOfResurrection = null;
    @In
    private LocalPlayer localPlayer;
    @In
    private AssetManager assetManager;
    @In
    private EntityManager entityManager;

    /**
     * This method creates the mesh and the orb entity for the model visual and lighting once the altar of resurrection is
     * placed in the world, upon activation of the {@link AltarOfResurrectionRootComponent}.
     * It also activates the altar of resurrection entity in case the local player has the same altar of resurrection activated, once
     * the entity gets loaded.
     *
     * @param event
     * @param entity
     * @param altarOfResurrectionRootComponent
     */
    @ReceiveEvent(components = {AltarOfResurrectionRootComponent.class, BlockComponent.class})
    public void onAltarOfResurrectionCreated(OnActivatedComponent event, EntityRef entity, AltarOfResurrectionRootComponent altarOfResurrectionRootComponent) {
        Prefab angelMeshPrefab = assetManager.getAsset("AdventureAssets:altarOfResurrectionMesh", Prefab.class).get();
        EntityBuilder angelMeshEntityBuilder = entityManager.newBuilder(angelMeshPrefab);
        angelMeshEntityBuilder.setOwner(entity);
        angelMeshEntityBuilder.setPersistent(false);
        EntityRef angelMesh = angelMeshEntityBuilder.build();
        Location.attachChild(entity, angelMesh, new Vector3f(0, 1f, 0), new Quaternionf());
        altarOfResurrectionRootComponent.meshEntity = angelMesh;

        Prefab angelOrbPrefab = assetManager.getAsset("AdventureAssets:altarOfResurrectionOrb", Prefab.class).get();
        EntityBuilder angelOrbEntityBuilder = entityManager.newBuilder(angelOrbPrefab);
        angelOrbEntityBuilder.setOwner(entity);
        angelOrbEntityBuilder.setPersistent(false);
        EntityRef angelOrb = angelOrbEntityBuilder.build();
        Location.attachChild(entity, angelOrb, new Vector3f(1f, 1.7f, 0), new Quaternionf());
        altarOfResurrectionRootComponent.orbEntity = angelOrb;
        entity.saveComponent(altarOfResurrectionRootComponent);

        // If an altar of resurrection entity becomes active later, it is still activated
        EntityRef clientInfo = localPlayer.getClientInfoEntity();
        if (clientInfo.hasComponent(RevivePlayerComponent.class)) {
            RevivePlayerComponent revivePlayerComponent = clientInfo.getComponent(RevivePlayerComponent.class);
            if (entity.equals(revivePlayerComponent.altarOfResurrectionEntity)) {
                activateAltarOfResurrection(entity);
                activatedAltarOfResurrection = entity;
            }
        }
    }

    /**
     * This method deals with the destruction of the altar of resurrection. The orb and the mesh entities on the client side
     * are destroyed.
     *
     * @param event
     * @param entityRef
     * @param altarOfResurrectionRootComponent
     */
    @ReceiveEvent
    public void onRemove(BeforeRemoveComponent event, EntityRef entityRef, AltarOfResurrectionRootComponent altarOfResurrectionRootComponent) {
        altarOfResurrectionRootComponent.meshEntity.destroy();
        altarOfResurrectionRootComponent.orbEntity.destroy();
    }

    /**
     * This method listens for the activation of the {@link RevivePlayerComponent} which is attached to the client info
     * entity when the {@link ActivateEvent} is handled in the {@link ResurrectionServerSystem}. This method triggers the
     * activation of the altar of resurrection which includes texture change and particle effects.
     *
     * @param event
     * @param entity
     * @param revivePlayerComponent
     */
    @ReceiveEvent(components = {RevivePlayerComponent.class})
    public void onRevivePlayerActivate(OnActivatedComponent event, EntityRef entity, RevivePlayerComponent revivePlayerComponent) {
        if (entity.equals(localPlayer.getClientInfoEntity())) {
            EntityRef altarOfResurrection = revivePlayerComponent.altarOfResurrectionEntity;
            if (altarOfResurrection.exists()) {
                activateAltarOfResurrection(altarOfResurrection);
                activatedAltarOfResurrection = altarOfResurrection;
            }
        }
    }

    /**
     * This method listens for the deactivation of the {@link RevivePlayerComponent}. This happens to an already
     * activated altar of resurrection upon deactivation/destruction of an altar of resurrection.
     *
     * @param event
     * @param entity
     * @param revivePlayerComponent
     */
    @ReceiveEvent
    public void onRevivePlayerRemove(BeforeRemoveComponent event, EntityRef entity, RevivePlayerComponent revivePlayerComponent) {
        if (entity.equals(localPlayer.getClientInfoEntity())) {
            EntityRef altarOfResurrection = revivePlayerComponent.altarOfResurrectionEntity;
            deactivateAltarOfResurrection(altarOfResurrection);
            activatedAltarOfResurrection = null;
        }
    }

    /**
     * This method listens for the change in the {@link RevivePlayerComponent}. This happens to an already
     * activated altar of resurrection upon activation of a new altar of resurrection.
     *
     * @param event
     * @param entity
     * @param revivePlayerComponent
     */
    @ReceiveEvent
    public void setRevivePlayerChange(OnChangedComponent event, EntityRef entity, RevivePlayerComponent revivePlayerComponent) {
        if (entity.equals(localPlayer.getClientInfoEntity())) {
            deactivateAltarOfResurrection(activatedAltarOfResurrection);
            EntityRef altarOfResurrection = revivePlayerComponent.altarOfResurrectionEntity;
            activateAltarOfResurrection(altarOfResurrection);
            activatedAltarOfResurrection = altarOfResurrection;
        }
    }

    private void activateAltarOfResurrection(EntityRef altarOfResurrection) {
        AltarOfResurrectionRootComponent altarOfResurrectionRootComponent = altarOfResurrection.getComponent(AltarOfResurrectionRootComponent.class);
        Vector3f location = altarOfResurrection.getComponent(LocationComponent.class).getWorldPosition(new Vector3f());

        spawnParticlesOnActivate(location);
        changeMeshToActive(altarOfResurrectionRootComponent.meshEntity);
        lightenOrbEntity(altarOfResurrectionRootComponent.orbEntity);
    }

    private void deactivateAltarOfResurrection(EntityRef altarOfResurrection) {
        AltarOfResurrectionRootComponent altarOfResurrectionRootComponent = altarOfResurrection.getComponent(AltarOfResurrectionRootComponent.class);
        Vector3f location = altarOfResurrection.getComponent(LocationComponent.class).getWorldPosition(new Vector3f());

        spawnParticlesOnDeactivate(location);
        changeMeshToInactive(altarOfResurrectionRootComponent.meshEntity);
        darkenOrbEntity(altarOfResurrectionRootComponent.orbEntity);
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
        EntityBuilder entityBuilder = entityManager.newBuilder("AdventureAssets:altarOfResurrectionParticleEffect");
        LocationComponent locationComponent = entityBuilder.getComponent(LocationComponent.class);
        locationComponent.setWorldPosition(spawnPos);
        entityBuilder.build();
        // Create a smoke explosion
        entityBuilder = entityManager.newBuilder("CoreAssets:smokeExplosion");
        locationComponent = entityBuilder.getComponent(LocationComponent.class);
        locationComponent.setWorldPosition(spawnPos);
        entityBuilder.build();
    }

    private void spawnParticlesOnDeactivate(Vector3f spawnPos) {
        // Create a smoke explosion
        EntityBuilder entityBuilder = entityManager.newBuilder("CoreAssets:smokeExplosion");
        LocationComponent locationComponent = entityBuilder.getComponent(LocationComponent.class);
        locationComponent.setWorldPosition(spawnPos);
        entityBuilder.build();
    }
}
