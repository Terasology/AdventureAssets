// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.adventureassets.traps.swingingblade;

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
public class SwingingBladeClientSystem extends BaseComponentSystem implements UpdateSubscriberSystem {

    private static final Logger logger = LoggerFactory.getLogger(SwingingBladeClientSystem.class);

    @In
    private EntityManager entityManager;
    @In
    private AssetManager assetManager;
    @In
    private Time time;

    /**
     * This method creates the mesh entity when the {@link SwingingBladeComponent} is activated. The rod and blade
     * entities are saved in the childrenEntities list inside the {@link SwingingBladeComponent}. A similar method in
     * the {@link SwingingBladeServerSystem} adds the rod and blade entities to the childrenEntities list.<br/> Note
     * this happens before the block is actually placed in the world i.e. before the OnBlockItemPlacedEvent handler-
     * {@link SwingingBladeServerSystem#onBlockToItem(OnBlockToItem, EntityRef, SwingingBladeComponent)} gets called.
     * So, the saved properties (amplitude, time-period, offset etc) are transferred after this, maintaining only the
     * childrenEntities list created here.
     *
     * @param event
     * @param entity
     * @param swingingBladeComponent
     */
    @ReceiveEvent(components = {SwingingBladeComponent.class, BlockComponent.class})
    public void onSwingingBladeActivated(OnActivatedComponent event, EntityRef entity,
                                         SwingingBladeComponent swingingBladeComponent) {
        // So that only the relevant server entity (which gets modified by the server system already) is operated on.
        if (!swingingBladeComponent.childrenEntities.isEmpty()) {
            Prefab swingingBladePrefab = assetManager.getAsset("AdventureAssets:swingingBladeMesh", Prefab.class).get();
            EntityBuilder swingingBladeEntityBuilder = entityManager.newBuilder(swingingBladePrefab);
            swingingBladeEntityBuilder.setOwner(entity);
            swingingBladeEntityBuilder.setPersistent(false);
            EntityRef swingingBladeMesh = swingingBladeEntityBuilder.build();
            swingingBladeComponent.childrenEntities.add(swingingBladeMesh);
            entity.saveComponent(swingingBladeComponent);
            Location.attachChild(entity, swingingBladeMesh, new Vector3f(0, -1, 0), new Quat4f(Quat4f.IDENTITY));
        }
    }

    @Override
    public void update(float delta) {
        for (EntityRef blade : entityManager.getEntitiesWith(SwingingBladeComponent.class, BlockComponent.class)) {
            SwingingBladeUtilities.rotateSwingingBlade(blade, time.getGameTime());
        }
    }
}
