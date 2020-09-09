// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.traps.wipeout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.core.Time;
import org.terasology.engine.entitySystem.entity.EntityBuilder;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.engine.logic.location.Location;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.block.BlockComponent;
import org.terasology.engine.world.block.items.OnBlockToItem;
import org.terasology.gestalt.assets.management.AssetManager;
import org.terasology.math.geom.Quat4f;
import org.terasology.math.geom.Vector3f;

@RegisterSystem(RegisterMode.CLIENT)
public class WipeOutClientSystem extends BaseComponentSystem implements UpdateSubscriberSystem {

    private static final Logger logger = LoggerFactory.getLogger(WipeOutClientSystem.class);

    @In
    private EntityManager entityManager;
    @In
    private AssetManager assetManager;
    @In
    private Time time;

    /**
     * This method creates the mesh entity when the {@link WipeOutComponent} is activated. The rod and blade entities
     * are saved in the childrenEntities list inside the {@link WipeOutComponent}. A similar method in the {@link
     * WipeOutServerSystem} adds the rod and surfboard entities to the childrenEntities list.<br/> Note this happens
     * before the block is actually placed in the world i.e. before the OnBlockItemPlacedEvent handler- {@link
     * WipeOutServerSystem#onBlockToItem(OnBlockToItem, EntityRef, WipeOutComponent)} gets called. So, the saved
     * properties (amplitude, time-period, offset etc) are transferred after this, maintaining only the childrenEntities
     * list created here.
     *
     * @param event
     * @param entity
     * @param wipeOutComponent
     */
    @ReceiveEvent(components = {WipeOutComponent.class, BlockComponent.class})
    public void onWipeOutActivated(OnActivatedComponent event, EntityRef entity, WipeOutComponent wipeOutComponent) {
        // So that only the relevant server entity (which gets modified by the server system already) is operated on.
        if (!wipeOutComponent.childrenEntities.isEmpty()) {
            Prefab wipeOutPrefab = assetManager.getAsset("AdventureAssets:wipeOutMesh", Prefab.class).get();
            EntityBuilder wipeOutEntityBuilder = entityManager.newBuilder(wipeOutPrefab);
            wipeOutEntityBuilder.setOwner(entity);
            wipeOutEntityBuilder.setPersistent(false);
            EntityRef wipeOutMesh = wipeOutEntityBuilder.build();
            wipeOutComponent.childrenEntities.add(wipeOutMesh);
            entity.saveComponent(wipeOutComponent);
            Location.attachChild(entity, wipeOutMesh, new Vector3f(0, 0, 1), new Quat4f(Quat4f.IDENTITY));
        }
    }

    @Override
    public void update(float delta) {
        for (EntityRef wipeOut : entityManager.getEntitiesWith(WipeOutComponent.class, BlockComponent.class)) {
            WipeOutUtilities.rotateWipeOut(wipeOut, time.getGameTime());
        }
    }
}
