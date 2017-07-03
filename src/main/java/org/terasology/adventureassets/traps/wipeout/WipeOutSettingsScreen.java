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
package org.terasology.adventureassets.traps.wipeout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.location.LocationComponent;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.math.geom.Quat4f;
import org.terasology.registry.In;
import org.terasology.rendering.nui.BaseInteractionScreen;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.rendering.nui.UIWidget;
import org.terasology.rendering.nui.widgets.UIButton;
import org.terasology.rendering.nui.widgets.UICheckbox;
import org.terasology.rendering.nui.widgets.UIText;

public class WipeOutSettingsScreen extends BaseInteractionScreen {
    private static final Logger logger = LoggerFactory.getLogger(WipeOutSettingsScreen.class);

    private UICheckbox isRotating;
    private UICheckbox clockwise;
    private UIText timePeriod;
    private UIText offset;
    private UIButton cancelButton;
    private UIButton saveButton;
    private EntityRef wipeOutRoot;
    private WipeOutComponent wipeOutComponent;
    private LocationComponent locationComponent;

    private UIText yaw;
    private UIText pitch;
    private UIText roll;

    @In
    private NUIManager nuiManager;
    @In
    private LocalPlayer localPlayer;

    @Override
    public void initialise() {
        isRotating = find("isRotating", UICheckbox.class);
        clockwise = find("clockwise", UICheckbox.class);
        timePeriod = find("timePeriod", UIText.class);
        offset = find("offset", UIText.class);
        yaw = find("yaw", UIText.class);
        pitch = find("pitch", UIText.class);
        roll = find("roll", UIText.class);
        cancelButton = find("cancelButton", UIButton.class);
        saveButton = find("saveButton", UIButton.class);
        if (saveButton != null) {
            saveButton.subscribe(this::onSaveButton);
        }

        if (cancelButton != null) {
            cancelButton.subscribe(this::onCancelButton);
        }
    }

    @Override
    protected void initializeWithInteractionTarget(EntityRef interactionTarget) {
        wipeOutRoot = interactionTarget;
        wipeOutComponent = interactionTarget.getComponent(WipeOutComponent.class);
        locationComponent = interactionTarget.getComponent(LocationComponent.class);

        isRotating.setChecked(wipeOutComponent.isRotating);
        clockwise.setChecked((wipeOutComponent.direction == -1));
        timePeriod.setText("" + wipeOutComponent.timePeriod);
        offset.setText("" + wipeOutComponent.offset);

        Quat4f q = locationComponent.getWorldRotation();
        pitch.setText(String.format("%.2f", Math.toDegrees(q.getPitch())));
        yaw.setText(String.format("%.2f", Math.toDegrees(q.getYaw())));
        roll.setText(String.format("%.2f", Math.toDegrees(q.getRoll())));

    }

    private void onSaveButton(UIWidget button) {
        try {
            wipeOutComponent.timePeriod = Float.parseFloat(timePeriod.getText());
            wipeOutComponent.offset = Float.parseFloat(offset.getText());
            wipeOutComponent.isRotating = isRotating.isChecked();
            wipeOutComponent.direction = clockwise.isChecked() ? -1 : 1;
            double yawValue = Math.toRadians(Double.parseDouble(yaw.getText()));
            double pitchValue = Math.toRadians(Double.parseDouble(pitch.getText()));
            double rollValue = Math.toRadians(Double.parseDouble(roll.getText()));
            locationComponent.setWorldRotation(new Quat4f((float) yawValue, (float) pitchValue, (float) rollValue));
            wipeOutRoot.saveComponent(wipeOutComponent);
            wipeOutRoot.saveComponent(locationComponent);
            localPlayer.getCharacterEntity().send(new SetWipeOutRoot(wipeOutRoot, wipeOutComponent.timePeriod, wipeOutComponent.offset,
                    wipeOutComponent.isRotating, wipeOutComponent.direction, locationComponent.getWorldRotation()));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        getManager().popScreen();
    }

    private void onCancelButton(UIWidget button) {
        getManager().popScreen();
    }

}
