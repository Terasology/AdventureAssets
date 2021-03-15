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
package org.terasology.adventureassets.traps.wipeout;

import com.google.common.collect.Lists;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.assets.management.AssetManager;
import org.terasology.engine.core.Time;
import org.terasology.engine.entitySystem.entity.EntityBuilder;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.engine.entitySystem.event.EventPriority;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.module.inventory.systems.InventoryManager;
import org.terasology.engine.logic.location.Location;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.block.BlockComponent;
import org.terasology.engine.world.block.items.BlockItemComponent;
import org.terasology.engine.world.block.items.OnBlockItemPlaced;
import org.terasology.engine.world.block.items.OnBlockToItem;

@RegisterSystem(RegisterMode.AUTHORITY)
public class WipeOutServerSystem extends BaseComponentSystem implements UpdateSubscriberSystem {

    private static final Logger logger = LoggerFactory.getLogger(WipeOutServerSystem.class);

    @In
    private EntityManager entityManager;
    @In
    private AssetManager assetManager;
    @In
    private InventoryManager inventoryManager;
    @In
    private Time time;

    /**
     * This method transfers the saved block properties from the item to the block. <br/> Note that this method is
     * called after the OnActivatedComponent event handler
     * {@link WipeOutServerSystem#onWipeOutActivated(OnActivatedComponent,
     * EntityRef, WipeOutComponent)} filtering the {@link WipeOutComponent} gets executed. So the placedBlock entity
     * already has the right childrenEntities and only needs the trap properties to be transferred.
     *
     * @param event
     * @param itemEntity
     * @param wipeOutComponent
     */
    @ReceiveEvent(components = {BlockItemComponent.class})
    public void onItemToBlock(OnBlockItemPlaced event, EntityRef itemEntity,
                              WipeOutComponent wipeOutComponent) {
        EntityRef entity = event.getPlacedBlock();
        WipeOutComponent component = entity.getComponent(WipeOutComponent.class);
        wipeOutComponent.childrenEntities = component.childrenEntities;
        entity.addOrSaveComponent(wipeOutComponent);
        LocationComponent locationComponent = entity.getComponent(LocationComponent.class);
        locationComponent.setWorldRotation(wipeOutComponent.rotation);
        entity.addOrSaveComponent(locationComponent);
    }

    /**
     * This method transfers the stored properties in the block's {@link WipeOutComponent} to the new item created. It
     * also destroys the children entities, i.e. the rod, surfboard and the mesh.
     *
     * @param event
     * @param blockEntity
     * @param wipeOutComponent
     */
    @ReceiveEvent(components = {})
    public void onBlockToItem(OnBlockToItem event, EntityRef blockEntity, WipeOutComponent wipeOutComponent) {
        for (EntityRef e : wipeOutComponent.childrenEntities) {
            e.destroy();
        }
        wipeOutComponent.childrenEntities = Lists.newArrayList();
        wipeOutComponent.rotation = blockEntity.getComponent(LocationComponent.class).getWorldRotation(new Quaternionf());
        event.getItem().addOrSaveComponent(wipeOutComponent);
    }

    /**
     * This method creates the rod and surfboard entities when the {@link WipeOutComponent} is activated. The rod and
     * surfboard entities are saved in the childrenEntities list inside the {@link WipeOutComponent}. A similar method
     * in the {@link WipeOutClientSystem} adds the mesh entity to the childrenEntities list.<br/> Note this happens
     * before the block is actually placed in the world i.e. before the OnBlockItemPlacedEvent handler- {@link
     * WipeOutServerSystem#onBlockToItem(OnBlockToItem, EntityRef, WipeOutComponent)} gets called. So, the saved
     * properties (offset, time-period etc) are transferred after this, maintaining only the childrenEntities list
     * created here.
     *
     * @param event
     * @param entity
     * @param wipeOutComponent
     */
    @ReceiveEvent(priority = EventPriority.PRIORITY_HIGH, components = {WipeOutComponent.class, BlockComponent.class})
    public void onWipeOutActivated(OnActivatedComponent event, EntityRef entity,
                                   WipeOutComponent wipeOutComponent) {
        Prefab rodPrefab = assetManager.getAsset("AdventureAssets:wipeOutRod", Prefab.class).get();
        EntityBuilder rodEntityBuilder = entityManager.newBuilder(rodPrefab);
        rodEntityBuilder.setOwner(entity);
        rodEntityBuilder.setPersistent(false);
        EntityRef rod = rodEntityBuilder.build();
        wipeOutComponent.childrenEntities.add(rod);
        entity.saveComponent(wipeOutComponent);
        Location.attachChild(entity, rod, new Vector3f(0, 0, 3), new Quaternionf());

        Prefab surfboardPrefab = assetManager.getAsset("AdventureAssets:wipeOutSurfboard", Prefab.class).get();
        EntityBuilder surfboardEntityBuilder = entityManager.newBuilder(surfboardPrefab);
        surfboardEntityBuilder.setOwner(entity);
        surfboardEntityBuilder.setPersistent(false);
        EntityRef surfboard = surfboardEntityBuilder.build();
        wipeOutComponent.childrenEntities.add(surfboard);
        entity.saveComponent(wipeOutComponent);
        Location.attachChild(entity, surfboard, new Vector3f(0, 0, 7), new Quaternionf());
    }

    @ReceiveEvent
    public void onSettingsChanged(SetWipeOutRoot event, EntityRef player) {
        EntityRef wipeOutRoot = event.getWipeOutRoot();
        WipeOutComponent wipeOutComponent = wipeOutRoot.getComponent(WipeOutComponent.class);
        wipeOutComponent.timePeriod = event.getTimePeriod();
        wipeOutComponent.isRotating = event.isRotating();
        wipeOutComponent.direction = event.getDirection();
        wipeOutComponent.offset = event.getOffset();
        LocationComponent locationComponent = wipeOutRoot.getComponent(LocationComponent.class);
        locationComponent.setWorldRotation(event.getRotation());
        wipeOutRoot.saveComponent(wipeOutComponent);
        wipeOutRoot.saveComponent(locationComponent);
    }

    @Override
    public void update(float delta) {
        for (EntityRef wipeOut : entityManager.getEntitiesWith(WipeOutComponent.class, BlockComponent.class)) {
            WipeOutUtilities.rotateWipeOut(wipeOut, time.getGameTime());
        }
    }
}
