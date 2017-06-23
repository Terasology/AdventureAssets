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

package org.terasology.adventureassets.traps.passwordDoor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.audio.events.PlaySoundEvent;
import org.terasology.core.logic.door.DoorComponent;
import org.terasology.core.logic.door.DoorPlacedEvent;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.Side;
import org.terasology.registry.In;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.regions.BlockRegionComponent;

/**
 */
@RegisterSystem
public class PasswordDoorSystem extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(PasswordDoorSystem.class);

    @In
    private WorldProvider worldProvider;
    @In
    private BlockEntityRegistry blockEntityRegistry;
    @In
    private NUIManager nuiManager;

    @ReceiveEvent(priority = EventPriority.PRIORITY_HIGH,
            components = {DoorComponent.class, PasswordDoorComponent.class})
    public void onDoorPlaced(DoorPlacedEvent event, EntityRef doorItem) {
        logger.info("Password Door placed");
        EntityRef placedDoor = event.getPlacedDoor();
        placedDoor.addComponent(doorItem.getComponent(PasswordDoorComponent.class));
        SetPasswordDoorScreen passwordDoorScreen = nuiManager.pushScreen("AdventureAssets:setPasswordDoorScreen", SetPasswordDoorScreen.class);
        passwordDoorScreen.setDoorEntity(event.getPlacedDoor());
    }

    @ReceiveEvent(priority = EventPriority.PRIORITY_HIGH,
            components = {DoorComponent.class, PasswordDoorComponent.class, BlockRegionComponent.class, LocationComponent.class})
    public void onFrob(ActivateEvent event, EntityRef entity) {
        event.consume();
        DoorComponent door = entity.getComponent(DoorComponent.class);
        if (door.isOpen) {
            closeDoor(entity, door);
        } else {
            logger.info("trying to open");
            PasswordDoorScreen passwordDoorScreen = nuiManager.pushScreen("AdventureAssets:passwordDoorScreen", PasswordDoorScreen.class);
            passwordDoorScreen.setDoorEntity(entity);
        }
        entity.saveComponent(door);
    }

    private void closeDoor(EntityRef entity, DoorComponent door) {
        Side newSide = door.closedSide;
        BlockRegionComponent regionComp = entity.getComponent(BlockRegionComponent.class);
        Block bottomBlock = door.bottomBlockFamily.getBlockForPlacement(worldProvider, blockEntityRegistry, regionComp.region.min(), newSide, Side.TOP);
        worldProvider.setBlock(regionComp.region.min(), bottomBlock);
        Block topBlock = door.topBlockFamily.getBlockForPlacement(worldProvider, blockEntityRegistry, regionComp.region.max(), newSide, Side.TOP);
        worldProvider.setBlock(regionComp.region.max(), topBlock);
        if (door.closeSound != null) {
            entity.send(new PlaySoundEvent(door.closeSound, 1f));
        }
        door.isOpen = false;
    }
}
