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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.registry.In;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.UIWidget;
import org.terasology.rendering.nui.widgets.UIButton;
import org.terasology.rendering.nui.widgets.UILabel;
import org.terasology.rendering.nui.widgets.UIText;

import java.util.Arrays;
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
        List<String> optionList = Arrays.asList(options.getText().split("\\s*,\\s*"));
        String passwordText = password.getText();
        if (!optionList.contains(passwordText)) {
            invalid.setText("Correct option not present in provided options");
            invalid.setVisible(true);
        } else if (passwordText.length() > 0 && title.getText().length() > 0 && message.getText().length() > 0) {
            localPlayer.getClientEntity().send(new SetMcqButtonDoorEvent(doorEntity, title.getText(), message.getText(), passwordText, optionList));
            getManager().popScreen();
        } else {
            invalid.setText("Please fill all fields!");
            invalid.setVisible(true);
        }
    }
}
