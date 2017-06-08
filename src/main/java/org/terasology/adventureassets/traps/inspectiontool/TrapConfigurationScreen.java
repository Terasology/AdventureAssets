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
package org.terasology.adventureassets.traps.inspectiontool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.adventureassets.traps.swingingblade.SwingingBladeComponent;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.input.cameraTarget.CameraTargetSystem;
import org.terasology.logic.common.InspectionToolComponent;
import org.terasology.registry.CoreRegistry;
import org.terasology.registry.In;
import org.terasology.rendering.nui.BaseInteractionScreen;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.rendering.nui.layouts.ColumnLayout;
import org.terasology.rendering.nui.layouts.RowLayout;
import org.terasology.rendering.nui.layouts.ScrollableArea;
import org.terasology.rendering.nui.widgets.UIButton;
import org.terasology.rendering.nui.widgets.UICheckbox;
import org.terasology.rendering.nui.widgets.UILabel;
import org.terasology.rendering.nui.widgets.UIText;

import java.text.ParseException;

/**
 */
@RegisterSystem
public class TrapConfigurationScreen extends CoreScreenLayer {
    private UIText fullDescriptionLabel;
    private UIText entityIdField;
    private UIText prefabNameField;
    private ScrollableArea scrollArea;
    private UIButton cancelButton;

    @In
    private CameraTargetSystem cameraTargetSystem;
    @In
    private NUIManager nuiManager;

    private static final Logger logger = LoggerFactory.getLogger(TrapConfigurationScreen.class);

    @Override
    public void initialise() {
        fullDescriptionLabel = find("fullDescriptionLabel", UIText.class);
        entityIdField = find("entityIdField", UIText.class);
        prefabNameField = find("prefabNameField", UIText.class);
        scrollArea = find("scrollArea", ScrollableArea.class);
        cancelButton = find("cancelButton", UIButton.class);

        cancelButton.subscribe(button -> {
            nuiManager.closeScreen(this);
        });
    }

    @Override
    public void onOpened() {
        EntityRef cameraTarget = cameraTargetSystem.getTarget();
        updateFields(cameraTarget);
    }

    private void updateFields(EntityRef target) {
        if (target.exists()) {
            try {
                String prefabName = target.getParentPrefab().getName();
                if (prefabName.equalsIgnoreCase("AdventureAssets:SwingingBladeMesh")) {
                    updateSwingingBlade(target.getOwner());
                } else {
                    entityIdField.setText("" + target.getId());
                    prefabNameField.setText("" + prefabName);
                    fullDescriptionLabel.setText("Target is not a recognized trap.\n" + target.toFullDescription());
                }
            } catch (NullPointerException e) {
                entityIdField.setText("" + target.getId());
                fullDescriptionLabel.setText("Target is not a recognized trap.\n" + target.toFullDescription());
            }
        } else {
            entityIdField.setText("" + target.getId());
            fullDescriptionLabel.setText("Non existing entity with id " + target.getId());
        }
    }

    private void updateSwingingBlade(EntityRef owner) {
        prefabNameField.setText(owner.getParentPrefab().getName());
        entityIdField.setText("" + owner.getId());
        fullDescriptionLabel.setVisible(false);

        SwingingBladeComponent swingingBladeComponent = owner.getComponent(SwingingBladeComponent.class);

        ColumnLayout mainLayout = new ColumnLayout();
        mainLayout.setHorizontalSpacing(8);
        mainLayout.setVerticalSpacing(8);
        mainLayout.setFamily("option-grid");

        UICheckbox isSwinging = new UICheckbox();
        isSwinging.setChecked(swingingBladeComponent.isSwinging);

        mainLayout.addWidget(new RowLayout(new UILabel("isSwinging:"), isSwinging)
                .setColumnRatios(0.8f)
                .setHorizontalSpacing(12));

        UIText timePeriod = new UIText();
        timePeriod.setText("" + swingingBladeComponent.timePeriod);

        mainLayout.addWidget(new RowLayout(new UILabel("Time Period: "), timePeriod)
                .setColumnRatios(0.8f)
                .setHorizontalSpacing(12));

        UIText amplitude = new UIText();
        amplitude.setText("" + swingingBladeComponent.amplitude);

        mainLayout.addWidget(new RowLayout(new UILabel("Amplitude: "), amplitude)
                .setColumnRatios(0.8f)
                .setHorizontalSpacing(12));

        UIText offset = new UIText();
        offset.setText("" + swingingBladeComponent.offset);

        mainLayout.addWidget(new RowLayout(new UILabel("Offset: "), offset)
                .setColumnRatios(0.8f)
                .setHorizontalSpacing(12));

        UIButton saveButton = new UIButton();
        saveButton.setText("Save");
        saveButton.subscribe(button -> {
            try {
                swingingBladeComponent.timePeriod=(Float.parseFloat(timePeriod.getText()));
                swingingBladeComponent.amplitude=(Float.parseFloat(amplitude.getText()));
                swingingBladeComponent.offset=(Float.parseFloat(offset.getText()));
            } catch (NumberFormatException e) {
                logger.error(e.getStackTrace().toString());
            }
        });

        mainLayout.addWidget(saveButton);
        scrollArea.setContent(mainLayout);
    }

}
