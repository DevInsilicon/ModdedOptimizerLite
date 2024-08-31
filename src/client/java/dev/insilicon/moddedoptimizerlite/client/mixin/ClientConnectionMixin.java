package dev.insilicon.moddedoptimizerlite.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"))
    private void onPacketSend(Packet<?> packet, CallbackInfo ci) {
        final MinecraftClient mc = MinecraftClient.getInstance();
        if (packet instanceof PlayerInteractEntityC2SPacket interactPacket) {
            interactPacket.handle(new PlayerInteractEntityC2SPacket.Handler() {
                @Override
                public void interact(Hand hand) {
                    // Empty implementation
                }

                @Override
                public void interactAt(Hand hand, Vec3d pos) {
                    // Empty implementation
                }

                @Override
                public void attack() {
                    HitResult hitResult = mc.crosshairTarget;
                    if (hitResult != null) {
                        Entity entity;
                        if (hitResult.getType() == HitResult.Type.ENTITY && (entity = ((EntityHitResult)hitResult).getEntity()) instanceof EndCrystalEntity) {
                            StatusEffectInstance weakness = null;
                            if (mc.player != null) {
                                weakness = mc.player.getStatusEffect(StatusEffects.WEAKNESS);
                            }
                            StatusEffectInstance strength = mc.player.getStatusEffect(StatusEffects.STRENGTH);
                            if (weakness != null && (strength == null || strength.getAmplifier() <= weakness.getAmplifier()) && !isTool(mc.player.getMainHandStack())) {
                                return;
                            }
                            entity.kill();
                            entity.setRemoved(Entity.RemovalReason.KILLED);
                            entity.onRemoved();
                        }
                    }
                }
            });
        }
    }

    @Unique
    private boolean isTool(ItemStack itemStack) {
        if (itemStack.getItem() instanceof MiningToolItem && !(itemStack.getItem() instanceof SwordItem)) {
            ToolMaterial material = ((MiningToolItem) itemStack.getItem()).getMaterial();
            return material == ToolMaterials.NETHERITE || material == ToolMaterials.DIAMOND;
        } else {
            return false;
        }
    }
}