// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.traps.fireballlauncher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.players.LocalPlayer;
import org.joml.Vector3f;
import org.terasology.registry.In;
import org.terasology.rendering.nui.BaseInteractionScreen;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.nui.UIWidget;
import org.terasology.nui.widgets.UIButton;
import org.terasology.nui.widgets.UICheckbox;
import org.terasology.nui.widgets.UIText;

public class FireballLauncherSettingsScreen extends BaseInteractionScreen {
    private static final Logger logger = LoggerFactory.getLogger(FireballLauncherSettingsScreen.class);

    private UICheckbox isFiring;
    private UIText timePeriod;
    private UIText offset;
    private UIText maxDistance;
    private UIText damageAmount;
    private UIText x;
    private UIText y;
    private UIText z;
    private UIButton cancelButton;
    private UIButton saveButton;
    private EntityRef fireballLauncherRoot;
    private FireballLauncherComponent fireballLauncherComponent;

    @In
    private NUIManager nuiManager;
    @In
    private LocalPlayer localPlayer;

    @Override
    public void initialise() {
        isFiring = find("isFiring", UICheckbox.class);
        timePeriod = find("timePeriod", UIText.class);
        offset = find("offset", UIText.class);
        maxDistance = find("maxDistance", UIText.class);
        damageAmount = find("damageAmount", UIText.class);
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

        isFiring.setChecked(fireballLauncherComponent.isFiring);
        timePeriod.setText("" + fireballLauncherComponent.timePeriod);
        offset.setText("" + fireballLauncherComponent.offset);
        maxDistance.setText("" + fireballLauncherComponent.maxDistance);
        damageAmount.setText("" + fireballLauncherComponent.damageAmount);
        Vector3f direction = fireballLauncherComponent.direction;

        x.setText(String.format("%.2f", direction.x()));
        y.setText(String.format("%.2f", direction.y()));
        z.setText(String.format("%.2f", direction.z()));
    }

    private void onSaveButton(UIWidget button) {
        try {
            fireballLauncherComponent.isFiring = isFiring.isChecked();
            fireballLauncherComponent.timePeriod = Float.parseFloat(timePeriod.getText());
            fireballLauncherComponent.offset = Float.parseFloat(offset.getText());
            fireballLauncherComponent.maxDistance = Integer.parseInt(maxDistance.getText());
            fireballLauncherComponent.damageAmount = Integer.parseInt(damageAmount.getText());
            double xValue = Double.parseDouble(x.getText());
            double yValue = Double.parseDouble(y.getText());
            double zValue = Double.parseDouble(z.getText());
            fireballLauncherComponent.direction = new Vector3f((float) xValue, (float) yValue, (float) zValue);
            fireballLauncherComponent.direction.normalize();
            localPlayer.getCharacterEntity().send(new SetFireballLauncherEvent(fireballLauncherRoot,
                    fireballLauncherComponent.isFiring, fireballLauncherComponent.timePeriod,
                    fireballLauncherComponent.offset, fireballLauncherComponent.direction,
                    fireballLauncherComponent.maxDistance, fireballLauncherComponent.damageAmount));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        getManager().popScreen();
    }

    private void onCancelButton(UIWidget button) {
        getManager().popScreen();
    }

}
