package dev.insilicon.moddedoptimizerlite.client.mixin;

import dev.insilicon.moddedoptimizerlite.client.ModdedoptimizerliteClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.Map;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void onPreTick(CallbackInfo info) {
        ModdedoptimizerliteClient modClient = ModdedoptimizerliteClient.getInstance();
        Map<Entity, Integer> toKill = modClient.getToKill();
        Iterator<Map.Entry<Entity, Integer>> iterator = toKill.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Entity, Integer> entry = iterator.next();
            Entity entity = entry.getKey();
            int delay = entry.getValue() - 1;
            if (delay == 0) {
                iterator.remove();
                if (!entity.isRemoved()) {
                    entity.kill();
                    entity.setRemoved(Entity.RemovalReason.KILLED);
                    entity.onRemoved();
                }
            } else {
                entry.setValue(delay);
            }
        }
    }
}