// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.traps.swingingblade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.logic.players.LocalPlayer;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.nui.BaseInteractionScreen;
import org.terasology.engine.rendering.nui.NUIManager;
import org.terasology.math.geom.Quat4f;
import org.terasology.nui.UIWidget;
import org.terasology.nui.widgets.UIButton;
import org.terasology.nui.widgets.UICheckbox;
import org.terasology.nui.widgets.UIText;

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

        Quat4f q = locationComponent.getWorldRotation();
        pitch.setText(String.format("%.2f", Math.toDegrees(q.getPitch())));
        yaw.setText(String.format("%.2f", Math.toDegrees(q.getYaw())));
        roll.setText(String.format("%.2f", Math.toDegrees(q.getRoll())));
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
            locationComponent.setWorldRotation(new Quat4f((float) yawValue, (float) pitchValue, (float) rollValue));
            localPlayer.getCharacterEntity().send(new SetSwingingBladeRoot(swingingBladeRoot,
                    swingingBladeComponent.timePeriod, swingingBladeComponent.offset, swingingBladeComponent.isSwinging,
                    swingingBladeComponent.amplitude, locationComponent.getWorldRotation()));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        getManager().popScreen();
    }

    private void onCancelButton(UIWidget button) {
        getManager().popScreen();
    }

}
