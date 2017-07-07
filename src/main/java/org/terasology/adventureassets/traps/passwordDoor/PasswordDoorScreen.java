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

import org.terasology.core.logic.door.OpenDoorEvent;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.registry.In;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.UIWidget;
import org.terasology.rendering.nui.widgets.UIButton;
import org.terasology.rendering.nui.widgets.UILabel;
import org.terasology.rendering.nui.widgets.UIText;

public class PasswordDoorScreen extends CoreScreenLayer {

    private UILabel title;
    private UILabel message;
    private UILabel invalid;
    private UIText password;
    private UIButton unlockButton;

    private EntityRef doorEntity;
    private PasswordDoorComponent passwordDoorComponent;
    private String passwordString;

    @In
    private LocalPlayer localPlayer;

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
            localPlayer.getCharacterEntity().send(new OpenDoorEvent(doorEntity));
            getManager().popScreen();
        } else {
            invalid.setVisible(true);
        }
    }

}
