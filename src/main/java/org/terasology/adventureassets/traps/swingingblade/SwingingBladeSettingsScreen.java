// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.traps.swingingblade;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.location.LocationComponent;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.nui.UIWidget;
import org.terasology.nui.widgets.UIButton;
import org.terasology.nui.widgets.UICheckbox;
import org.terasology.nui.widgets.UIText;
import org.terasology.registry.In;
import org.terasology.rendering.nui.BaseInteractionScreen;
import org.terasology.rendering.nui.NUIManager;

public class SwingingBladeSettingsScreen extends BaseInteractionScreen {
    private static final Logger logger = LoggerFactory.getLogger(SwingingBladeSettingsScreen.class);

    private UICheckbox isSwinging;
    private UIText timePeriod;
    private UIText amplitude;
    private UIText offset;
    private UIText yaw;
    private UIText pitch;
    private UIText roll;
    private UIButton cancelButton;
    private UIButton saveButton;
    private EntityRef swingingBladeRoot;
    private SwingingBladeComponent swingingBladeComponent;
    private LocationComponent locationComponent;

    @In
    private NUIManager nuiManager;
    @In
    private LocalPlayer localPlayer;

    @Override
    public void initialise() {
        isSwinging = find("isSwinging", UICheckbox.class);
        timePeriod = find("timePeriod", UIText.class);
        amplitude = find("amplitude", UIText.class);
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
        swingingBladeRoot = interactionTarget;
        swingingBladeComponent = interactionTarget.getComponent(SwingingBladeComponent.class);
        locationComponent = interactionTarget.getComponent(LocationComponent.class);

        isSwinging.setChecked(swingingBladeComponent.isSwinging);
        timePeriod.setText("" + swingingBladeComponent.timePeriod);
        amplitude.setText("" + swingingBladeComponent.amplitude);
        offset.setText("" + swingingBladeComponent.offset);

        Quaternionf q = locationComponent.getWorldRotation(new Quaternionf());
        Vector3f angles = new Vector3f();
        q.getEulerAnglesXYZ(angles);
        pitch.setText(String.format("%.2f", Math.toDegrees(angles.x())));
        yaw.setText(String.format("%.2f", Math.toDegrees(angles.y())));
        roll.setText(String.format("%.2f", Math.toDegrees(angles.z())));
    }

    private void onSaveButton(UIWidget button) {
        try {
            swingingBladeComponent.timePeriod = Float.parseFloat(timePeriod.getText());
            swingingBladeComponent.amplitude = Float.parseFloat(amplitude.getText());
            swingingBladeComponent.offset = Float.parseFloat(offset.getText());
            swingingBladeComponent.isSwinging = isSwinging.isChecked();
            double yawValue = Math.toRadians(Double.parseDouble(yaw.getText()));
            double pitchValue = Math.toRadians(Double.parseDouble(pitch.getText()));
            double rollValue = Math.toRadians(Double.parseDouble(roll.getText()));
            locationComponent.setWorldRotation(new Quaternionf().rotationYXZ((float) yawValue, (float) pitchValue, (float) rollValue));
            localPlayer.getCharacterEntity().send(new SetSwingingBladeRoot(swingingBladeRoot,
                    swingingBladeComponent.timePeriod, swingingBladeComponent.offset, swingingBladeComponent.isSwinging,
                    swingingBladeComponent.amplitude, locationComponent.getWorldRotation(new Quaternionf())));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        getManager().popScreen();
    }

    private void onCancelButton(UIWidget button) {
        getManager().popScreen();
    }

}
