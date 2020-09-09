// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.traps.passwordDoor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.players.LocalPlayer;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.nui.CoreScreenLayer;
import org.terasology.nui.UIWidget;
import org.terasology.nui.widgets.UIButton;
import org.terasology.nui.widgets.UILabel;
import org.terasology.nui.widgets.UIText;

public class SetPasswordDoorScreen extends CoreScreenLayer {
    private static final Logger logger = LoggerFactory.getLogger(SetPasswordDoorScreen.class);
    @In
    LocalPlayer localPlayer;
    private UIText title;
    private UIText message;
    private UIText password;
    private UILabel invalid;
    private UIButton saveButton;
    private EntityRef doorEntity;

    @Override
    public void initialise() {
        title = find("title", UIText.class);
        message = find("message", UIText.class);
        password = find("password", UIText.class);
        saveButton = find("saveButton", UIButton.class);
        invalid = find("invalid", UILabel.class);

        if (saveButton != null) {
            saveButton.subscribe(this::onSaveButton);
        }
    }

    void setDoorEntity(EntityRef door) {
        doorEntity = door;
        title.setText("");
        message.setText("");
        password.setText("");
        invalid.setVisible(false);
    }

    private void onSaveButton(UIWidget button) {
        if (password.getText().length() > 0 && title.getText().length() > 0 && message.getText().length() > 0) {
            localPlayer.getClientEntity().send(new SetPasswordDoorEvent(doorEntity, title.getText(),
                    message.getText(), password.getText()));
            getManager().popScreen();
        } else {
            invalid.setVisible(true);
        }
    }
}
