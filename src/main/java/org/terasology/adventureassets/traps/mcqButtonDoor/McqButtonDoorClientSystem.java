// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.adventureassets.traps.mcqButtonDoor;

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
public class McqButtonDoorClientSystem extends BaseComponentSystem {

    @In
    LocalPlayer localPlayer;
    @In
    NUIManager nuiManager;

    @ReceiveEvent
    public void openPasswordDoorRequest(OpenMcqButtonDoorRequest event, EntityRef player) {
        if (player.equals(localPlayer.getCharacterEntity())) {
            McqButtonDoorScreen mcqButtonDoorScreen = nuiManager.pushScreen("AdventureAssets:mcqButtonDoorScreen",
                    McqButtonDoorScreen.class);
            mcqButtonDoorScreen.setDoorEntity(event.getDoorEntity());
        }
    }

    @ReceiveEvent(components = {McqButtonDoorComponent.class})
    public void onDoorPlaced(DoorPlacedEvent event, EntityRef entity) {
        if (event.getInstigator().equals(localPlayer.getCharacterEntity())) {
            SetMcqButtonDoorScreen passwordDoorScreen = nuiManager.pushScreen("AdventureAssets:setMcqButtonDoorScreen"
                    , SetMcqButtonDoorScreen.class);
            passwordDoorScreen.setDoorEntity(entity);
        }
    }

    /*
     * Sets the Name at the top of the WorldlyTooltip to show "Password Door"
     */
    @ReceiveEvent
    public void getTooltipName(GetTooltipNameEvent event, EntityRef entity,
                               McqButtonDoorComponent mcqButtonDoorComponent) {
        event.setName("MCQ Button Door");
    }

    /*
     * Adds the Icon to the WorldlyTooltip to show the door icon
     */
    @ReceiveEvent
    public void addIconToWorldlyTooltip(GetTooltipIconEvent event, EntityRef entity,
                                        McqButtonDoorComponent mcqButtonDoorComponent) {
        event.setIcon(Assets.getTextureRegion("engine:items#door").get());
    }
}
