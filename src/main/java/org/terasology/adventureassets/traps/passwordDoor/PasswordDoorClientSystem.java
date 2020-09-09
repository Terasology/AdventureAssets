// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.adventureassets.traps.passwordDoor;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.players.LocalPlayer;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.nui.NUIManager;
import org.terasology.engine.utilities.Assets;
import org.terasology.furnishings.logic.door.DoorPlacedEvent;
import org.terasology.worldlyTooltipAPI.events.GetTooltipIconEvent;
import org.terasology.worldlyTooltipAPI.events.GetTooltipNameEvent;

@RegisterSystem(RegisterMode.CLIENT)
public class PasswordDoorClientSystem extends BaseComponentSystem {

    @In
    LocalPlayer localPlayer;
    @In
    NUIManager nuiManager;

    @ReceiveEvent
    public void openPasswordDoorRequest(OpenPasswordDoorRequest event, EntityRef player) {
        if (player.equals(localPlayer.getCharacterEntity())) {
            PasswordDoorScreen passwordDoorScreen = nuiManager.pushScreen("AdventureAssets:passwordDoorScreen",
                    PasswordDoorScreen.class);
            passwordDoorScreen.setDoorEntity(event.getDoorEntity());
        }
    }

    @ReceiveEvent(components = {PasswordDoorComponent.class})
    public void onDoorPlaced(DoorPlacedEvent event, EntityRef entity) {
        if (event.getInstigator().equals(localPlayer.getCharacterEntity())) {
            SetPasswordDoorScreen passwordDoorScreen = nuiManager.pushScreen("AdventureAssets:setPasswordDoorScreen",
                    SetPasswordDoorScreen.class);
            passwordDoorScreen.setDoorEntity(entity);
        }
    }

    /*
     * Sets the Name at the top of the WorldlyTooltip to show "Password Door"
     */
    @ReceiveEvent
    public void getTooltipName(GetTooltipNameEvent event, EntityRef entity,
                               PasswordDoorComponent passwordDoorComponent) {
        event.setName("Password Door");
    }

    /*
     * Adds the Icon to the WorldlyTooltip to show the door icon
     */
    @ReceiveEvent
    public void addIconToWorldlyTooltip(GetTooltipIconEvent event, EntityRef entity,
                                        PasswordDoorComponent passwordDoorComponent) {
        event.setIcon(Assets.getTextureRegion("engine:items#door").get());
    }
}
