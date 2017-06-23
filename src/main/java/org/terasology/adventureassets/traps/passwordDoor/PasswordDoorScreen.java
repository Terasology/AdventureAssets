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
import org.terasology.adventureassets.traps.swingingblade.SwingingBladeComponent;
import org.terasology.audio.events.PlaySoundEvent;
import org.terasology.core.logic.door.DoorComponent;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.Side;
import org.terasology.math.geom.Quat4f;
import org.terasology.registry.In;
import org.terasology.rendering.nui.BaseInteractionScreen;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.rendering.nui.UIWidget;
import org.terasology.rendering.nui.widgets.UIButton;
import org.terasology.rendering.nui.widgets.UICheckbox;
import org.terasology.rendering.nui.widgets.UILabel;
import org.terasology.rendering.nui.widgets.UIText;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.regions.BlockRegionComponent;

/**
 */
@RegisterSystem
public class PasswordDoorScreen extends CoreScreenLayer {
    private static final Logger logger = LoggerFactory.getLogger(PasswordDoorScreen.class);

    private UILabel title;
    private UILabel message;
    private UILabel invalid;
    private UIText password;
    private UIButton unlockButton;

    private EntityRef doorEntity;
    private PasswordDoorComponent passwordDoorComponent;
    private String passwordString;

    @In
    private NUIManager nuiManager;
    @In
    private WorldProvider worldProvider;
    @In
    private BlockEntityRegistry blockEntityRegistry;

    @Override
    public void initialise() {
        title = find("title", UILabel.class);
        message = find("message", UILabel.class);
        invalid = find("invalid", UILabel.class);
        password = find("password", UIText.class);
        unlockButton = find("unlock", UIButton.class);

        if (unlockButton != null) {
            unlockButton.subscribe(this::onUnlockButton);
        }
    }

    void setDoorEntity(EntityRef door) {
        doorEntity = door;
        passwordDoorComponent = doorEntity.getComponent(PasswordDoorComponent.class);
        title.setText(passwordDoorComponent.title);
        message.setText("" + passwordDoorComponent.message);
        password.setText("");
        invalid.setVisible(false);
        passwordString = passwordDoorComponent.password;
    }

    private void onUnlockButton(UIWidget button) {
        String enteredPassword = password.getText();
        if (enteredPassword.equalsIgnoreCase(passwordString)) {
            DoorComponent doorComponent = doorEntity.getComponent(DoorComponent.class);
            openDoor(doorEntity, doorComponent);
            getManager().popScreen();
        } else {
            invalid.setVisible(true);
        }
    }

    private void openDoor(EntityRef entity, DoorComponent door) {
        Side newSide = door.openSide;
        BlockRegionComponent regionComp = entity.getComponent(BlockRegionComponent.class);
        Block bottomBlock = door.bottomBlockFamily.getBlockForPlacement(worldProvider, blockEntityRegistry, regionComp.region.min(), newSide, Side.TOP);
        worldProvider.setBlock(regionComp.region.min(), bottomBlock);
        Block topBlock = door.topBlockFamily.getBlockForPlacement(worldProvider, blockEntityRegistry, regionComp.region.max(), newSide, Side.TOP);
        worldProvider.setBlock(regionComp.region.max(), topBlock);
        if (door.openSide != null) {
            entity.send(new PlaySoundEvent(door.openSound, 1f));
        }
        door.isOpen = true;
    }

}
