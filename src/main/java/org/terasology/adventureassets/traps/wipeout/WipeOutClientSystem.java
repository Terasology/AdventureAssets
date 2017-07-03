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
package org.terasology.adventureassets.traps.wipeout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.assets.management.AssetManager;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityBuilder;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.location.Location;
import org.terasology.math.geom.Quat4f;
import org.terasology.math.geom.Vector3f;
import org.terasology.network.NetworkComponent;
import org.terasology.registry.In;
import org.terasology.world.block.BlockComponent;
import org.terasology.world.block.items.OnBlockToItem;

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
     * This method creates the mesh entity when the {@link WipeOutComponent} is activated. The rod and blade
     * entities are saved in the childrenEntities list inside the {@link WipeOutComponent}.
     * A similar method in the {@link WipeOutServerSystem} adds the rod and surfboard entities to the
     * childrenEntities list.<br/>
     * Note this happens before the block is actually placed in the world i.e. before the OnBlockItemPlacedEvent handler-
     * {@link WipeOutServerSystem#onBlockToItem(OnBlockToItem, EntityRef, WipeOutComponent)} gets called.
     * So, the saved properties (amplitude, time-period, offset etc) are transferred after this, maintaining
     * only the childrenEntities list created here.
     *
     * @param event
     * @param entity
     * @param wipeOutComponent
     */
    @ReceiveEvent(components = {WipeOutComponent.class, BlockComponent.class})
    public void onWipeOutActivated(OnActivatedComponent event, EntityRef entity, WipeOutComponent wipeOutComponent) {
        logger.info(entity.toFullDescription());
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
