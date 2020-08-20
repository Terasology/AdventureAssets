// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.traps.mcqButtonDoor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.registry.In;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.nui.UIWidget;
import org.terasology.nui.widgets.UIButton;
import org.terasology.nui.widgets.UILabel;
import org.terasology.nui.widgets.UIText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SetMcqButtonDoorScreen extends CoreScreenLayer {
    private static final Logger logger = LoggerFactory.getLogger(SetMcqButtonDoorScreen.class);
    @In
    LocalPlayer localPlayer;
    private UIText title;
    private UIText message;
    private UIText options;
    private UIText password;
    private UILabel invalid;
    private UIButton saveButton;
    private EntityRef doorEntity;

    @Override
    public void initialise() {
        title = find("title", UIText.class);
        message = find("message", UIText.class);
        options = find("options", UIText.class);
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
        options.setText("");
        invalid.setVisible(false);
    }

    private void onSaveButton(UIWidget button) {
        List<String> optionList = new ArrayList<String>(Arrays.asList(options.getText().split("\\s*,\\s*")));
        optionList.removeAll(Collections.singleton(""));
        String passwordText = password.getText();
        if (optionList.size() > 0 && passwordText.length() > 0 && title.getText().length() > 0 && message.getText().length() > 0) {
            if (!optionList.contains(passwordText)) {
                invalid.setText("Correct option not present in provided options");
                invalid.setVisible(true);
            } else {
                localPlayer.getClientEntity().send(new SetMcqButtonDoorEvent(doorEntity, title.getText(), message.getText(), passwordText, optionList));
                getManager().popScreen();
            }
        } else {
            invalid.setText("Please fill all fields!");
            invalid.setVisible(true);
        }
    }
}
