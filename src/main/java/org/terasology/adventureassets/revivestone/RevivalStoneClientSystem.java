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
import org.terasology.entitySystem.entity.lifecycleEvents.BeforeRemoveComponent;
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.logic.notifications.NotificationMessageEvent;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.logic.players.event.RespawnRequestEvent;
import org.terasology.math.geom.Quat4f;
import org.terasology.math.geom.Vector3f;
import org.terasology.network.ClientComponent;
import org.terasology.registry.In;

@RegisterSystem(RegisterMode.CLIENT)
public class RevivalStoneClientSystem extends BaseComponentSystem {

    private static final Logger logger = LoggerFactory.getLogger(RevivalStoneClientSystem.class);

    @In
    private LocalPlayer localPlayer;

    @ReceiveEvent(priority = EventPriority.PRIORITY_HIGH, components = {ClientComponent.class})
    public void setSpawnLocationOnRespawnRequest(RespawnRequestEvent event, EntityRef entity) {
        EntityRef character = entity.getComponent(ClientComponent.class).character;
        if (character.hasComponent(RevivePlayerComponent.class)) {
            Vector3f spawnPosition = character.getComponent(RevivePlayerComponent.class).location;
            LocationComponent loc = entity.getComponent(LocationComponent.class);
            loc.setWorldPosition(spawnPosition);
            loc.setLocalRotation(new Quat4f());
            entity.saveComponent(loc);
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
            if (player.hasComponent(RevivePlayerComponent.class)) {
                EntityRef prevRevivalStone = player.getComponent(RevivePlayerComponent.class).revivalStoneEntity;
                RevivalStoneComponent prevRevivalStoneComponent = prevRevivalStone.getComponent(RevivalStoneComponent.class);
                prevRevivalStoneComponent.activated = false;
                prevRevivalStone.saveComponent(prevRevivalStoneComponent);
                player.removeComponent(RevivePlayerComponent.class);
                client.send(new NotificationMessageEvent("Deactivated the previous Revival Stone", client));
            }
            revivalStoneComponent.activated = true;
            client.send(new NotificationMessageEvent("Activated Revival Stone", client));
            Vector3f location = entity.getComponent(LocationComponent.class).getWorldPosition();
            RevivePlayerComponent revivePlayerComponent = new RevivePlayerComponent();
            revivePlayerComponent.location = location.add(1, 0, 1);
            revivePlayerComponent.revivalStoneEntity = entity;
            player.addComponent(revivePlayerComponent);
        }
        entity.saveComponent(revivalStoneComponent);
    }

    @ReceiveEvent
    public void onRemove(BeforeRemoveComponent event, EntityRef entityRef, RevivalStoneComponent revivalStoneComponent) {
        if (revivalStoneComponent.activated) {
            localPlayer.getCharacterEntity().removeComponent(RevivePlayerComponent.class);
            EntityRef client = localPlayer.getClientEntity();
            client.send(new NotificationMessageEvent("Deactivated Revival Stone due to destruction", client));
        }
    }
}
