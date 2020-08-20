// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.traps.passwordDoor;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.furnishings.logic.door.OpenDoorEvent;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.registry.In;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.nui.UIWidget;
import org.terasology.nui.widgets.UIButton;
import org.terasology.nui.widgets.UILabel;
import org.terasology.nui.widgets.UIText;

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
