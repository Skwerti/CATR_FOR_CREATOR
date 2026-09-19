package catr.mixin;

import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.slepa01.catr.items.ModItemsGen;

// ========== 1. Ваш старый миксин (без изменений) ==========
@Mixin(value = DeployerBlockEntity.class, remap = false)
public abstract class DeployerBlockEntityMixin {

    @Shadow(remap = false)
    public DeployerFakePlayer player;

    @Inject(method = "activate", at = @At("RETURN"), remap = false)
    private void catr$damageDrillBit(CallbackInfo ci) {
        if (player == null) return;
        ItemStack held = player.getMainHandItem();

        if (held.getItem() == ModItemsGen.DIAMOND_DRILL_BIT.get()) {
            int newDamage = held.getDamageValue() + 1;
            if (newDamage >= held.getMaxDamage()) {
                player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            } else {
                held.setDamageValue(newDamage);
                player.setItemInHand(InteractionHand.MAIN_HAND, held);
            }
        }
    }
}
