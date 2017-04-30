/*
 * Copyright 2014 MovingBlocks
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
package org.terasology.worldlyTooltip.systems;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.assets.management.AssetManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.input.InputSystem;
import org.terasology.input.Keyboard;
import org.terasology.input.cameraTarget.CameraTargetChangedEvent;
import org.terasology.input.cameraTarget.CameraTargetSystem;
import org.terasology.input.device.KeyboardDevice;
import org.terasology.logic.common.DisplayNameComponent;
import org.terasology.logic.health.HealthComponent;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.rendering.assets.mesh.Mesh;
import org.terasology.rendering.assets.texture.Texture;
import org.terasology.rendering.assets.texture.TextureRegion;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.rendering.nui.layers.ingame.inventory.GetItemTooltip;
import org.terasology.rendering.nui.skin.UISkin;
import org.terasology.rendering.nui.widgets.TooltipLine;
import org.terasology.rendering.nui.widgets.TooltipLineRenderer;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;

import java.util.List;

import static org.terasology.worldlyTooltip.ui.WorldlyTooltip.icon;
import static org.terasology.worldlyTooltip.ui.WorldlyTooltip.blockName;
import static org.terasology.worldlyTooltip.ui.WorldlyTooltip.tooltip;

@RegisterSystem(RegisterMode.CLIENT)
public class WorldyTooltipClientSystem extends BaseComponentSystem implements UpdateSubscriberSystem {
    @In
    private NUIManager nuiManager;

    @In
    private CameraTargetSystem cameraTargetSystem;

    @In
    private WorldProvider worldProvider;

    @In
    private BlockEntityRegistry blockEntityRegistry;

    @In
    private InputSystem inputSystem;

    @In
    private AssetManager assetManager;

    private KeyboardDevice keyboard;

    private static final Logger logger = LoggerFactory.getLogger(WorldyTooltipClientSystem.class);

    @Override
    public void preBegin() {
        nuiManager.getHUD().addHUDElement("WorldlyTooltip:WorldlyTooltip");
        keyboard = inputSystem.getKeyboard();
    }

    @ReceiveEvent
    public void getDurabilityItemTooltip(GetItemTooltip event, EntityRef entity, HealthComponent healthComponent) {
        event.getTooltipLines().add(new TooltipLine("Health: " + healthComponent.currentHealth + "/" + healthComponent.maxHealth));
    }

    @ReceiveEvent
    public void onCameraTargetChanged(CameraTargetChangedEvent event, EntityRef entity){
//        blockName.setText(getBlockName());
//        if (tooltip != null) {
//            UISkin defaultSkin = assetManager.getAsset("core:itemTooltip", UISkin.class).get();
//            tooltip.setItemRenderer(new TooltipLineRenderer(defaultSkin));
//            tooltip.setSkin(defaultSkin);
//            tooltip.setList(getToolTip());
//        }
//        if (icon != null) {
//            icon.setMesh(getMesh());
//            icon.setMeshTexture(assetManager.getAsset("engine:terrain", Texture.class).get());
//        }
    }

    @Override
    public void update(float delta) {
//        logger.info(cameraTargetSystem.getTarget().toString());
//        logger.info(cameraTargetSystem.getTarget().toFullDescription());
    }

    public String getBlockName(){
        if (cameraTargetSystem.isTargetAvailable()) {
            Vector3i blockPosition = cameraTargetSystem.getTargetBlockPosition();
            Block block = worldProvider.getBlock(blockPosition);
            if (keyboard.isKeyDown(Keyboard.KeyId.LEFT_ALT) || keyboard.isKeyDown(Keyboard.KeyId.LEFT_ALT)) {
                return block.getURI().toString();
            } else {
                EntityRef blockEntity = blockEntityRegistry.getBlockEntityAt(blockPosition);
                DisplayNameComponent displayNameComponent = blockEntity.getComponent(DisplayNameComponent.class);
                if (displayNameComponent != null) {
                    return displayNameComponent.name;
                } else {
                    return block.getDisplayName();
                }
            }
        } else {
            return "";
        }
    }

    private List<TooltipLine> getToolTip() {
        if (cameraTargetSystem.isTargetAvailable()) {
            EntityRef targetEntity = blockEntityRegistry.getEntityAt(cameraTargetSystem.getTargetBlockPosition());

            GetItemTooltip itemTooltip = new GetItemTooltip();
            try {
                targetEntity.send(itemTooltip);
                return itemTooltip.getTooltipLines();
            } catch (Exception ex) {
                return Lists.newArrayList(new TooltipLine("Error"));
            }
        }
        return Lists.newArrayList();
    }

    private Mesh getMesh() {
        if (cameraTargetSystem.isTargetAvailable()) {
            Vector3i blockPosition = cameraTargetSystem.getTargetBlockPosition();
            Block block = worldProvider.getBlock(blockPosition);
            if (block.getBlockFamily() != null) {
                return block.getBlockFamily().getArchetypeBlock().getMesh();
            }
        }
        return null;
    }


}
