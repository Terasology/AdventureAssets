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
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.core.Time;
import org.terasology.engine.entitySystem.entity.EntityBuilder;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.BeforeRemoveComponent;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.engine.entitySystem.event.EventPriority;
import org.terasology.engine.entitySystem.event.Priority;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.engine.logic.location.Location;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.block.BlockComponent;
import org.terasology.engine.world.block.items.BlockItemComponent;
import org.terasology.engine.world.block.items.OnBlockItemPlaced;
import org.terasology.engine.world.block.items.OnBlockToItem;
import org.terasology.gestalt.assets.management.AssetManager;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.module.inventory.systems.InventoryManager;

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

    @ReceiveEvent(components = {SwingingBladeComponent.class, LocationComponent.class, BlockComponent.class})
    public void onSwingingBladeDestroyed(BeforeRemoveComponent event, EntityRef entity,
                                         SwingingBladeComponent swingingBladeComponent) {
    }

    /**
     * This method transfers the saved block properties from the item to the block. <br/> Note that this method is
     * called after the OnActivatedComponent event handler
     * {@link SwingingBladeServerSystem#onSwingingBladeActivated(OnActivatedComponent,
     * EntityRef, SwingingBladeComponent)} filtering the {@link SwingingBladeComponent} gets executed. So the
     * placedBlock entity already has the right childrenEntities and only needs the trap properties to be transferred.
     *
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
        LocationComponent locationComponent = entity.getComponent(LocationComponent.class);
        locationComponent.setWorldRotation(swingingBladeComponent.rotation);
        entity.addOrSaveComponent(locationComponent);
    }

    /**
     * This method transfers the stored properties in the block's {@link SwingingBladeComponent} to the new item
     * created. It also destroys the children entities, i.e. the rod, blade and the mesh.
     *
     * @param event
     * @param blockEntity
     * @param swingingBladeComponent
     */
    @ReceiveEvent
    public void onBlockToItem(OnBlockToItem event, EntityRef blockEntity, SwingingBladeComponent swingingBladeComponent) {
        for (EntityRef e : swingingBladeComponent.childrenEntities) {
            e.destroy();
        }
        swingingBladeComponent.childrenEntities = Lists.newArrayList();
        swingingBladeComponent.rotation = blockEntity.getComponent(LocationComponent.class).getWorldRotation(new Quaternionf());
        event.getItem().addOrSaveComponent(swingingBladeComponent);
    }

    /**
     * This method creates the rod and blade entities when the {@link SwingingBladeComponent} is activated. The rod and
     * blade entities are saved in the childrenEntities list inside the {@link SwingingBladeComponent}. A similar method
     * in the {@link SwingingBladeClientSystem} adds the mesh entity to the childrenEntities list.<br/> Note this
     * happens before the block is actually placed in the world i.e. before the OnBlockItemPlacedEvent handler- {@link
     * SwingingBladeServerSystem#onBlockToItem(OnBlockToItem, EntityRef, SwingingBladeComponent)} gets called. So, the
     * saved properties (amplitude, time-period, offset etc) are transferred after this, maintaining only the
     * childrenEntities list created here.
     *
     * @param event
     * @param entity
     * @param swingingBladeComponent
     */
    @Priority(EventPriority.PRIORITY_HIGH)
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
        Location.attachChild(entity, rod, new Vector3f(0, -1, 0), new Quaternionf());

        Prefab bladePrefab = assetManager.getAsset("AdventureAssets:blade", Prefab.class).get();
        EntityBuilder bladeEntityBuilder = entityManager.newBuilder(bladePrefab);
        bladeEntityBuilder.setOwner(entity);
        bladeEntityBuilder.setPersistent(false);
        EntityRef blade = bladeEntityBuilder.build();
        swingingBladeComponent.childrenEntities.add(blade);
        entity.saveComponent(swingingBladeComponent);
        Location.attachChild(entity, blade, new Vector3f(0, -7, 0), new Quaternionf());
    }

    @ReceiveEvent
    public void onSettingsChanged(SetSwingingBladeRoot event, EntityRef player) {
        EntityRef swingingBladeRoot = event.getSwingingBladeRoot();
        SwingingBladeComponent swingingBladeComponent = swingingBladeRoot.getComponent(SwingingBladeComponent.class);
        swingingBladeComponent.isSwinging = event.isSwinging();
        swingingBladeComponent.amplitude = event.getAmplitude();
        swingingBladeComponent.offset = event.getOffset();
        swingingBladeComponent.timePeriod = event.getTimePeriod();
        LocationComponent locationComponent = swingingBladeRoot.getComponent(LocationComponent.class);
        locationComponent.setWorldRotation(event.getRotation());
        swingingBladeRoot.saveComponent(swingingBladeComponent);
        swingingBladeRoot.saveComponent(locationComponent);
    }

    @Override
    public void update(float delta) {
        for (EntityRef blade : entityManager.getEntitiesWith(SwingingBladeComponent.class, BlockComponent.class)) {
            SwingingBladeUtilities.rotateSwingingBlade(blade, time.getGameTime());
        }
    }
}
