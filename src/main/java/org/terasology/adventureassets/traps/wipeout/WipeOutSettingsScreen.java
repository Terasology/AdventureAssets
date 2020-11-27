// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.traps.wipeout;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.location.LocationComponent;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.registry.In;
import org.terasology.rendering.nui.BaseInteractionScreen;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.nui.UIWidget;
import org.terasology.nui.widgets.UIButton;
import org.terasology.nui.widgets.UICheckbox;
import org.terasology.nui.widgets.UIText;

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

        Quaternionf q = locationComponent.getWorldRotation(new Quaternionf());
        Vector3f angles = new Vector3f();
        q.getEulerAnglesXYZ(angles);
        pitch.setText(String.format("%.2f", Math.toDegrees(angles.x())));
        yaw.setText(String.format("%.2f", Math.toDegrees(angles.y())));
        roll.setText(String.format("%.2f", Math.toDegrees(angles.z())));

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
            locationComponent.setWorldRotation(new Quaternionf().rotationYXZ((float) yawValue, (float) pitchValue, (float) rollValue));
            wipeOutRoot.saveComponent(wipeOutComponent);
            wipeOutRoot.saveComponent(locationComponent);
            localPlayer.getCharacterEntity().send(new SetWipeOutRoot(wipeOutRoot, wipeOutComponent.timePeriod, wipeOutComponent.offset,
                    wipeOutComponent.isRotating, wipeOutComponent.direction, locationComponent.getWorldRotation(new Quaternionf())));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        getManager().popScreen();
    }

    private void onCancelButton(UIWidget button) {
        getManager().popScreen();
    }

}
