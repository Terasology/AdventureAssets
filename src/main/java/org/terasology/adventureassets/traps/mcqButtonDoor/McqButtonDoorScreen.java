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
package org.terasology.adventureassets.traps.mcqButtonDoor;

import org.terasology.core.logic.door.OpenDoorEvent;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.health.DestroyEvent;
import org.terasology.logic.health.EngineDamageTypes;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.registry.In;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.UIWidget;
import org.terasology.rendering.nui.layouts.RowLayout;
import org.terasology.rendering.nui.widgets.UIButton;
import org.terasology.rendering.nui.widgets.UILabel;

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
