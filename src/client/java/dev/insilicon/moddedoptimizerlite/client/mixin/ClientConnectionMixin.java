package dev.insilicon.moddedoptimizerlite.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.*;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"))
    private void onPacketSend(Packet<?> packet, CallbackInfo info) {
        final MinecraftClient mc = MinecraftClient.getInstance();
        if (packet instanceof PlayerInteractEntityC2SPacket) {
            PlayerInteractEntityC2SPacket interactPacket = (PlayerInteractEntityC2SPacket)packet;
            interactPacket.handle(new PlayerInteractEntityC2SPacket.Handler() {
                public void interact(Hand hand) {
                }

                public void interactAt(Hand hand, Vec3d pos) {
                }

                public void attack() {
                    HitResult hitResult = mc.crosshairTarget;
                    if (hitResult != null) {
                        if (hitResult.getType() == HitResult.Type.ENTITY) {
                            EntityHitResult entityHitResult = (EntityHitResult)hitResult;
                            Entity entity = entityHitResult.getEntity();
                            if (entity instanceof MobEntity) {
                                StatusEffectInstance weakness = mc.player.getStatusEffect(StatusEffects.WEAKNESS);
                                StatusEffectInstance strength = mc.player.getStatusEffect(StatusEffects.STRENGTH);
                                if (weakness != null && (strength == null || strength.getAmplifier() <= weakness.getAmplifier()) && !ClientConnectionMixin.this.isTool(mc.player.getMainHandStack())) {
                                    return;
                                }

                                entity.kill();
                                entity.setRemoved(Entity.RemovalReason.KILLED);
                                entity.onRemoved();
                            }
                        }
                    }
                }
            });
        }
    }

    private boolean isTool(ItemStack itemStack) {
        if (itemStack.getItem() instanceof ToolItem && !(itemStack.getItem() instanceof SwordItem)) {
            ToolMaterial material = ((ToolItem)itemStack.getItem()).getMaterial();
            return material == ToolMaterials.NETHERITE || material == ToolMaterials.DIAMOND;
        } else {
            return false;
        }
    }
}