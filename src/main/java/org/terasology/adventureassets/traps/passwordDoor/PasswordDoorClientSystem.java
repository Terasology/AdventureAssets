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
import org.terasology.core.logic.door.DoorPlacedEvent;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.registry.In;
import org.terasology.rendering.nui.NUIManager;

/**
 */
@RegisterSystem(RegisterMode.CLIENT)
public class PasswordDoorClientSystem extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(PasswordDoorClientSystem.class);

    @In
    LocalPlayer localPlayer;
    @In
    NUIManager nuiManager;

    @ReceiveEvent
    public void openPasswordDoorRequest(OpenPasswordDoorRequest event, EntityRef player) {
        if (player.equals(localPlayer.getCharacterEntity())) {
            PasswordDoorScreen passwordDoorScreen = nuiManager.pushScreen("AdventureAssets:passwordDoorScreen", PasswordDoorScreen.class);
            passwordDoorScreen.setDoorEntity(event.getDoorEntity());
        }
    }

    @ReceiveEvent(components = {PasswordDoorComponent.class})
    public void onDoorPlaced(DoorPlacedEvent event, EntityRef entity) {
        if (event.getInstigator().equals(localPlayer.getCharacterEntity())) {
            SetPasswordDoorScreen passwordDoorScreen = nuiManager.pushScreen("AdventureAssets:setPasswordDoorScreen", SetPasswordDoorScreen.class);
            passwordDoorScreen.setDoorEntity(entity);
        }
    }
}
