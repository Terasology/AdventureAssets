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

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.assets.ResourceUrn;
import org.terasology.assets.management.AssetManager;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityBuilder;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.lifecycleEvents.BeforeRemoveComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.OnAddedComponent;
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.characters.CharacterComponent;
import org.terasology.logic.inventory.InventoryManager;
import org.terasology.logic.inventory.events.InventorySlotChangedEvent;
import org.terasology.logic.location.Location;
import org.terasology.logic.location.LocationComponent;
import org.terasology.logic.players.event.OnPlayerSpawnedEvent;
import org.terasology.math.geom.Quat4f;
import org.terasology.math.geom.Vector3f;
import org.terasology.registry.CoreRegistry;
import org.terasology.registry.In;
import org.terasology.structureTemplates.events.StructureSpawnerFromToolboxRequest;
import org.terasology.structureTemplates.internal.components.StructureTemplateOriginComponent;
import org.terasology.world.OnChangedBlock;
import org.terasology.world.block.BlockComponent;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.family.BlockFamily;
import org.terasology.world.block.items.BlockItemComponent;
import org.terasology.world.block.items.OnBlockItemPlaced;
import org.terasology.world.block.items.OnBlockToItem;

@RegisterSystem(RegisterMode.AUTHORITY)
public class SwingingBladeServerSystem extends BaseComponentSystem implements UpdateSubscriberSystem {

    private static final Logger logger = LoggerFactory.getLogger(SwingingBladeServerSystem.class);

    @In
    private EntityManager entityManager;
    @In
    private AssetManager assetManager;
    @In
    private InventoryManager inventoryManager;
    @In
    private Time time;

    @ReceiveEvent(priority = EventPriority.PRIORITY_LOW)
    public void onPlayerSpawnedEvent(OnPlayerSpawnedEvent event, EntityRef player) {
        EntityRef toolbox = entityManager.create("StructureTemplates:toolbox");
        inventoryManager.giveItem(player, EntityRef.NULL, toolbox);
        Prefab prefab = assetManager.getAsset("AdventureAssets:bladeRoom", Prefab.class).get();
        toolbox.send(new StructureSpawnerFromToolboxRequest(prefab));
    }

    @ReceiveEvent(components = {SwingingBladeComponent.class, LocationComponent.class, BlockComponent.class})
    public void onSwingingBladeDestroyed(BeforeRemoveComponent event, EntityRef entity,
                                         SwingingBladeComponent swingingBladeComponent) {
    }

    /**
     * This method transfers the saved block properties from the item to the block. <br/>
     * Note that this method is called after the OnActivatedComponent event handler
     * {@link SwingingBladeServerSystem#onSwingingBladeActivated(OnActivatedComponent, EntityRef, SwingingBladeComponent)}
     * filtering the {@link SwingingBladeComponent} gets executed.
     * So the placedBlock entity already has the right childrenEntities and only needs the trap properties to be
     * transferred.
     * @param event
     * @param itemEntity
     * @param swingingBladeComponent
     */
    @ReceiveEvent(components = {BlockItemComponent.class})
    public void onItemToBlock(OnBlockItemPlaced event, EntityRef itemEntity,
                                  SwingingBladeComponent swingingBladeComponent) {
        EntityRef entity = event.getPlacedBlock();
        SwingingBladeComponent component = entity.getComponent(SwingingBladeComponent.class);
        swingingBladeComponent.childrenEntities = component.childrenEntities;
        entity.addOrSaveComponent(swingingBladeComponent);
    }

    /**
     * This method transfers the stored properties in the block's {@link SwingingBladeComponent} to the new item created.
     * It also destroys the children entities, i.e. the rod, blade and the mesh.
     * @param event
     * @param blockEntity
     * @param swingingBladeComponent
     */
    @ReceiveEvent(components = {})
    public void onBlockToItem(OnBlockToItem event, EntityRef blockEntity, SwingingBladeComponent swingingBladeComponent) {
        for (EntityRef e : swingingBladeComponent.childrenEntities) {
            e.destroy();
        }
        swingingBladeComponent.childrenEntities = Lists.newArrayList();
        event.getItem().addOrSaveComponent(swingingBladeComponent);
    }

    /**
     * This method creates the rod and blade entities when the {@link SwingingBladeComponent} is activated. The rod and blade
     * entities are saved in the childrenEntities list inside the {@link SwingingBladeComponent}.
     * A similar method in the {@link SwingingBladeClientSystem} adds the mesh entity to the childrenEntities list.<br/>
     * Note this happens before the block is actually placed in the world i.e. before the OnBlockItemPlacedEvent handler-
     * {@link SwingingBladeServerSystem#onBlockToItem(OnBlockToItem, EntityRef, SwingingBladeComponent)} gets called.
     * So, the saved properties (amplitude, time-period, offset etc) are transferred after this, maintaining
     * only the childrenEntities list created here.
     * @param event
     * @param entity
     * @param swingingBladeComponent
     */
    @ReceiveEvent(components = {SwingingBladeComponent.class, BlockComponent.class})
    public void onSwingingBladeActivated(OnActivatedComponent event, EntityRef entity,
                     SwingingBladeComponent swingingBladeComponent) {
        Prefab rodPrefab = assetManager.getAsset("AdventureAssets:rod", Prefab.class).get();
        EntityBuilder rodEntityBuilder = entityManager.newBuilder(rodPrefab);
        rodEntityBuilder.setOwner(entity);
        rodEntityBuilder.setPersistent(false);
        EntityRef rod = rodEntityBuilder.build();
        swingingBladeComponent.childrenEntities.add(rod);
        entity.saveComponent(swingingBladeComponent);
        Location.attachChild(entity, rod, new Vector3f(0, -1, 0), new Quat4f(Quat4f.IDENTITY));

        Prefab bladePrefab = assetManager.getAsset("AdventureAssets:blade", Prefab.class).get();
        EntityBuilder bladeEntityBuilder = entityManager.newBuilder(bladePrefab);
        bladeEntityBuilder.setOwner(entity);
        bladeEntityBuilder.setPersistent(false);
        EntityRef blade = bladeEntityBuilder.build();
        swingingBladeComponent.childrenEntities.add(blade);
        entity.saveComponent(swingingBladeComponent);
        Location.attachChild(entity, blade, new Vector3f(0, -7, 0), new Quat4f(Quat4f.IDENTITY));
    }

    /**
     * This method checks if a new SwingingBladeRoot block item is added to the inventory. It then adds the
     * {@link SwingingBladeComponent} to it.
     * @param event
     * @param player
     * @param characterComponent
     */
    @ReceiveEvent
    public void playerPickedUpItem(InventorySlotChangedEvent event, EntityRef player,
                                   CharacterComponent characterComponent) {
        EntityRef newItem = event.getNewItem();
        if (newItem.hasComponent(BlockItemComponent.class)) {
            BlockFamily blockFamily = CoreRegistry.get(BlockManager.class).getBlockFamily("AdventureAssets:SwingingBladeRoot");
            if (blockFamily == newItem.getComponent(BlockItemComponent.class).blockFamily) {
                if (!newItem.hasComponent(SwingingBladeComponent.class)) {
                    newItem.addComponent(new SwingingBladeComponent());
                }
            }
        }
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
