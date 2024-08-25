package dev.insilicon.moddedoptimizerlite.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ModdedoptimizerliteClient implements ClientModInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger("ModdedsOptimizer");
    private static ModdedoptimizerliteClient instance;
    private final Map<Entity, Integer> toKill;

    private boolean crystalling;
    private int crystalPlaced;

    public ModdedoptimizerliteClient() {
        if (instance == null) {
            instance = this;
            this.toKill = new HashMap<>();
            this.crystalling = false;
            this.crystalPlaced = 0;
        } else {
            throw new IllegalStateException("ModdedsoptimizerClient already initialized");
        }
    }

    public static ModdedoptimizerliteClient getInstance() {
        if (instance == null) {
            new ModdedoptimizerliteClient();
        }
        return instance;
    }

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing ModdedsoptimizerClient");

        // Clear the toKill map
        toKill.clear();


    }

    public Map<Entity, Integer> getToKill() {
        return toKill;
    }

    public void setCrystalling(boolean crystalling) {
        this.crystalling = crystalling;
        LOGGER.info("Crystalling state set to: {}", crystalling);
    }

    public boolean isCrystalling() {
        return crystalling;
    }

    public void setCrystalPlaced(int crystalPlaced) {
        this.crystalPlaced = crystalPlaced;
        LOGGER.info("Crystal placed time set to: {}", crystalPlaced);
    }


}
