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
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.UIWidget;
import org.terasology.rendering.nui.widgets.UIButton;
import org.terasology.rendering.nui.widgets.UILabel;
import org.terasology.rendering.nui.widgets.UIText;

public class SetPasswordDoorScreen extends CoreScreenLayer {
    private static final Logger logger = LoggerFactory.getLogger(SetPasswordDoorScreen.class);

    private UIText title;
    private UIText message;
    private UIText password;
    private UILabel invalid;
    private UIButton saveButton;

    private EntityRef doorEntity;
    private PasswordDoorComponent passwordDoorComponent;

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
        passwordDoorComponent = doorEntity.getComponent(PasswordDoorComponent.class);
        title.setText("");
        message.setText("");
        password.setText("");
        invalid.setVisible(false);
    }

    private void onSaveButton(UIWidget button) {
        if (password.getText().length() > 0 && title.getText().length() > 0 && message.getText().length() > 0) {
            passwordDoorComponent.title = title.getText();
            passwordDoorComponent.message = message.getText();
            passwordDoorComponent.password = password.getText();
            doorEntity.saveComponent(passwordDoorComponent);
            logger.info(doorEntity.toFullDescription());
            getManager().popScreen();
        } else {
            invalid.setVisible(true);
        }
    }
}
