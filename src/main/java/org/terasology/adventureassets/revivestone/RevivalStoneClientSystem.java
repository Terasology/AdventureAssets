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
package org.terasology.adventureassets.revivestone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.characters.CharacterTeleportEvent;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.logic.health.BeforeDestroyEvent;
import org.terasology.logic.health.DoHealEvent;
import org.terasology.logic.health.HealthComponent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.logic.notifications.NotificationMessageEvent;
import org.terasology.logic.players.PlayerCharacterComponent;
import org.terasology.math.geom.Vector3f;

@RegisterSystem(RegisterMode.CLIENT)
public class RevivalStoneClientSystem extends BaseComponentSystem {

    private static final Logger logger = LoggerFactory.getLogger(RevivalStoneClientSystem.class);

    @ReceiveEvent(priority = EventPriority.PRIORITY_HIGH, components = {PlayerCharacterComponent.class})
    public void onPlayerDeath(BeforeDestroyEvent event, EntityRef player, LocationComponent locationComponent, RevivePlayerComponent revivePlayerComponent) {
        if (revivePlayerComponent.location != null) {
            HealthComponent healthComponent = player.getComponent(HealthComponent.class);
            player.send(new CharacterTeleportEvent(revivePlayerComponent.location));
            player.send(new DoHealEvent(healthComponent.maxHealth));
            event.consume();
        }
    }

    @ReceiveEvent
    public void onRevivalStoneInteract(ActivateEvent event, EntityRef entity, RevivalStoneComponent revivalStoneComponent) {
        EntityRef player = event.getInstigator();
        EntityRef client = player.getOwner();
        if (revivalStoneComponent.activated) {
            revivalStoneComponent.activated = false;
            client.send(new NotificationMessageEvent("Deactivated Revival Stone", client));
            player.removeComponent(RevivePlayerComponent.class);
        } else {
            revivalStoneComponent.activated = true;
            client.send(new NotificationMessageEvent("Activated Revival Stone", client));
            Vector3f location = entity.getComponent(LocationComponent.class).getWorldPosition();
            RevivePlayerComponent revivePlayerComponent = new RevivePlayerComponent();
            revivePlayerComponent.location = location.add(1, 1, 1);
            player.addOrSaveComponent(revivePlayerComponent);
        }
        entity.saveComponent(revivalStoneComponent);
    }
}
