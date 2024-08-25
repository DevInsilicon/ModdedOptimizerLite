package dev.insilicon.moddedoptimizerlite.client.mixin;

import dev.insilicon.moddedoptimizerlite.client.ModdedoptimizerliteClient;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ClientPlayerInteractionManager.class)
public class ObsidianBreakPreventionMixin {

    @Inject(method = "attackBlock", at = @At("HEAD"), cancellable = true)
    private void onAttackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (shouldPreventInteraction(pos)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "updateBlockBreakingProgress", at = @At("HEAD"), cancellable = true)
    private void onUpdateBlockBreakingProgress(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (shouldPreventInteraction(pos)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.interactionManager != null && client.interactionManager.isBreakingBlock()) {
            //If crystalling and holding end crystal then cancel
            if (ModdedoptimizerliteClient.getInstance().isCrystalling()) {
                if (MinecraftClient.getInstance().player.getMainHandStack() != null) {

                    if (MinecraftClient.getInstance().player.getMainHandStack().getItem() == Items.END_CRYSTAL) {

                        //Cancel
                        client.interactionManager.cancelBlockBreaking();

                    }

                }
            }
        }
    }

    private boolean shouldPreventInteraction(BlockPos pos) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return false;

        if (ModdedoptimizerliteClient.getInstance().isCrystalling()) {
            if (MinecraftClient.getInstance().player.getMainHandStack() != null) {

                if (MinecraftClient.getInstance().player.getMainHandStack().getItem() == Items.END_CRYSTAL) {

                    //Cancel
                    return true;

                }

                return false;

            }

            return false;
        }

        return false;
    }
}

