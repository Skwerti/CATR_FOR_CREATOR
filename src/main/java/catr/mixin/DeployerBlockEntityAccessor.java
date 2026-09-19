package catr.mixin;

import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = DeployerBlockEntity.class, remap = false)
public interface DeployerBlockEntityAccessor {
    @Accessor("heldItem")
    ItemStack getHeldItem();

    @Accessor("heldItem")
    void setHeldItem(ItemStack stack);
}
