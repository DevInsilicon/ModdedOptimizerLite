package dev.insilicon.moddedoptimizerlite.client.mixin;

import dev.insilicon.moddedoptimizerlite.client.ModdedoptimizerliteClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EndCrystalItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndCrystalItem.class)
public class EndCrystalPlacementMixin {

    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void onEndCrystalPlace(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        System.out.println("End Crystal placed at: " + blockPos.getX() + " " + blockPos.getY() + " " + blockPos.getZ());

        if (world != null && player != null) {
            if (player.getUuid().equals(MinecraftClient.getInstance().player.getUuid())) {
                ModdedoptimizerliteClient modClient = ModdedoptimizerliteClient.getInstance();
                // Set last crystal placement time
                modClient.setCrystalPlaced((int) (System.currentTimeMillis() / 1000));
                // Set crystalling state to true
                modClient.setCrystalling(true);
                System.out.println("END CRYSTAL VERIFIED");
                System.out.println("Crystalling state set to: " + modClient.isCrystalling());
            }
        }
    }
}