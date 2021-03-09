// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.traps.mcqButtonDoor;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.health.DestroyEvent;
import org.terasology.engine.logic.health.EngineDamageTypes;
import org.terasology.engine.logic.players.LocalPlayer;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.nui.CoreScreenLayer;
import org.terasology.furnishings.logic.door.OpenDoorEvent;
import org.terasology.nui.UIWidget;
import org.terasology.nui.layouts.RowLayout;
import org.terasology.nui.widgets.UIButton;
import org.terasology.nui.widgets.UILabel;

import java.util.List;

public class McqButtonDoorScreen extends CoreScreenLayer {

    private UILabel title;
    private UILabel message;
    private UIButton backButton;

    private EntityRef doorEntity;
    private McqButtonDoorComponent mcqButtonDoorComponent;
    private String passwordString;
    private List<String> options;
    private RowLayout buttonLayout;

    @In
    private LocalPlayer localPlayer;

    @Override
    public void initialise() {
        title = find("title", UILabel.class);
        message = find("message", UILabel.class);
        backButton = find("back", UIButton.class);
        buttonLayout = find("buttonLayout", RowLayout.class);

        if (backButton != null) {
            backButton.subscribe(this::onBackButton);
        }
    }

    void setDoorEntity(EntityRef door) {
        doorEntity = door;
        mcqButtonDoorComponent = doorEntity.getComponent(McqButtonDoorComponent.class);
        title.setText(mcqButtonDoorComponent.title);
        message.setText("" + mcqButtonDoorComponent.message);
        passwordString = mcqButtonDoorComponent.password;
        options = mcqButtonDoorComponent.options;

        buttonLayout.removeAllWidgets();

        for (String option : options) {
            UIButton button = new UIButton(option, option);
            if (option.equalsIgnoreCase(passwordString)) {
                button.subscribe(this::onCorrectAnswer);
            } else {
                button.subscribe(this::onWrongAnswer);
            }
            buttonLayout.addWidget(button, null);
        }
    }

    private void onWrongAnswer(UIWidget uiWidget) {
        getManager().popScreen();
        localPlayer.getCharacterEntity().send(new DestroyEvent(doorEntity, EntityRef.NULL, EngineDamageTypes.PHYSICAL.get()));
    }

    private void onCorrectAnswer(UIWidget uiWidget) {
        localPlayer.getCharacterEntity().send(new OpenDoorEvent(doorEntity));
        getManager().popScreen();
    }

    private void onBackButton(UIWidget button) {
        getManager().popScreen();
    }

}
