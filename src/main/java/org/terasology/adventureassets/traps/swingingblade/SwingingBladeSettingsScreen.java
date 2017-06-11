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
package org.terasology.adventureassets.traps.swingingblade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.input.cameraTarget.CameraTargetSystem;
import org.terasology.logic.location.Location;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Quat4f;
import org.terasology.protobuf.EntityData;
import org.terasology.registry.In;
import org.terasology.rendering.nui.BaseInteractionScreen;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.rendering.nui.UIWidget;
import org.terasology.rendering.nui.layouts.ColumnLayout;
import org.terasology.rendering.nui.layouts.RowLayout;
import org.terasology.rendering.nui.layouts.ScrollableArea;
import org.terasology.rendering.nui.widgets.UIButton;
import org.terasology.rendering.nui.widgets.UICheckbox;
import org.terasology.rendering.nui.widgets.UILabel;
import org.terasology.rendering.nui.widgets.UIText;

/**
 */
@RegisterSystem
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
        pitch.setText("" + Math.toDegrees(Math.atan2(2.0*(q.y*q.z + q.w*q.x), q.w*q.w - q.x*q.x - q.y*q.y + q.z*q.z)));
        yaw.setText("" + Math.toDegrees(Math.asin(-2.0*(q.x*q.z - q.w*q.y))));
        roll.setText("" + Math.toDegrees(Math.atan2(2.0*(q.x*q.y + q.w*q.z), q.w*q.w + q.x*q.x - q.y*q.y - q.z*q.z)));

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
            locationComponent.setWorldRotation(new Quat4f((float) yawValue,(float) pitchValue,(float) rollValue));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        swingingBladeRoot.saveComponent(swingingBladeComponent);
        swingingBladeRoot.saveComponent(locationComponent);
        getManager().popScreen();
    }

    private void onCancelButton(UIWidget button) {
        getManager().popScreen();
    }

}
