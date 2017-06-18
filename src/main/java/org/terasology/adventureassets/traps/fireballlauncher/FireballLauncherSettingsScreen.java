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
package org.terasology.adventureassets.traps.fireballlauncher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.adventureassets.traps.swingingblade.SwingingBladeComponent;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Quat4f;
import org.terasology.math.geom.Vector3f;
import org.terasology.registry.In;
import org.terasology.rendering.nui.BaseInteractionScreen;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.rendering.nui.UIWidget;
import org.terasology.rendering.nui.widgets.UIButton;
import org.terasology.rendering.nui.widgets.UICheckbox;
import org.terasology.rendering.nui.widgets.UIText;

/**
 */
@RegisterSystem
public class FireballLauncherSettingsScreen extends BaseInteractionScreen {
    private static final Logger logger = LoggerFactory.getLogger(FireballLauncherSettingsScreen.class);

    private UIText timePeriod;
    private UIText maxDistance;
    private UIText x;
    private UIText y;
    private UIText z;
    private UIButton cancelButton;
    private UIButton saveButton;
    private EntityRef fireballLauncherRoot;
    private FireballLauncherComponent fireballLauncherComponent;

    @In
    private NUIManager nuiManager;

    @Override
    public void initialise() {
        timePeriod = find("timePeriod", UIText.class);
        maxDistance = find("maxDistance", UIText.class);
        x = find("x", UIText.class);
        y = find("y", UIText.class);
        z = find("z", UIText.class);
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
        fireballLauncherRoot = interactionTarget;
        fireballLauncherComponent = interactionTarget.getComponent(FireballLauncherComponent.class);

        timePeriod.setText("" + fireballLauncherComponent.timePeriod);
        maxDistance.setText("" + fireballLauncherComponent.maxDistance);
        Vector3f direction = fireballLauncherComponent.direction;

        x.setText(String.format("%.2f", direction.getX()));
        y.setText(String.format("%.2f", direction.getY()));
        z.setText(String.format("%.2f", direction.getZ()));
    }

    private void onSaveButton(UIWidget button) {
        try {
            fireballLauncherComponent.timePeriod = Float.parseFloat(timePeriod.getText());
            fireballLauncherComponent.maxDistance = Integer.parseInt(maxDistance.getText());
            double xValue = Double.parseDouble(x.getText());
            double yValue = Double.parseDouble(y.getText());
            double zValue = Double.parseDouble(z.getText());
            fireballLauncherComponent.direction = new Vector3f((float) xValue,(float) yValue,(float) zValue);
            fireballLauncherComponent.direction.normalize();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        fireballLauncherRoot.saveComponent(fireballLauncherComponent);
        getManager().popScreen();
    }

    private void onCancelButton(UIWidget button) {
        getManager().popScreen();
    }

}
