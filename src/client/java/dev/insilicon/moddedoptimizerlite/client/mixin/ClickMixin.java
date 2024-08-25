package dev.insilicon.moddedoptimizerlite.client.mixin;


import dev.insilicon.moddedoptimizerlite.client.ModdedoptimizerliteClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.item.EndCrystalItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class ClickMixin {

    private int nextMouseBtn = -1;

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen == null && action == 1) { // action 1 is press, 0 is release
            String clickType;
            switch (button) {
                case 0:
                    clickType = "Left Click";
                    handleClickType(true, ci);
                    break;
                case 1:
                    clickType = "Right Click";
                    handleClickType(false, ci);
                    break;
                case 2:
                    clickType = "Middle Click";
                    break;
                default:
                    clickType = "Other Click";
            }

            System.out.println("Mouse button pressed: " + clickType);
        }
    }

    private void handleClickType(boolean left, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        ItemStack mainHandItem = client.player.getMainHandStack();
        boolean holdingEndCrystal = mainHandItem.getItem() instanceof EndCrystalItem;

        // Check if crystalling and holding an end crystal
        ModdedoptimizerliteClient modClient = ModdedoptimizerliteClient.getInstance();
        boolean crystalling = modClient.isCrystalling() && holdingEndCrystal;
        System.out.println("Crystalling: " + crystalling + ", Holding End Crystal: " + holdingEndCrystal);

        // Check if the player is looking at an entity
        boolean lookingAtEntity = client.crosshairTarget instanceof EntityHitResult;

        if (crystalling && !lookingAtEntity) {
            //If entity looking at is end crystal, then cancel
            if (client.crosshairTarget instanceof EntityHitResult) {
                if (((EntityHitResult) client.crosshairTarget).getEntity() instanceof net.minecraft.entity.decoration.EndCrystalEntity) {
                    ci.cancel();
                    System.out.println("Cancelled click on end crystal");
                    return;
                }
            }
            if (!left) { // Only apply logic to right-click (crystal placement)
                if (nextMouseBtn == -1 || nextMouseBtn == 1) {
                    // Allow the first right click or if we're expecting a right click
                    nextMouseBtn = 0; // Expect left click next
                    System.out.println("Right click (crystal placement) allowed. Expecting left click next.");
                } else {
                    // Cancel if we're not expecting a right click
                    ci.cancel();
                    System.out.println("Cancelled right click (crystal placement)");
                }
            } else {
                // Always allow left clicks (crystal breaking)
                nextMouseBtn = 1; // Expect right click next
                System.out.println("Left click allowed. Expecting right click next.");
            }

            System.out.println("Expected next mouse button: " + (nextMouseBtn == 0 ? "Left" : "Right"));
        } else {
            nextMouseBtn = -1;
        }
    }
}