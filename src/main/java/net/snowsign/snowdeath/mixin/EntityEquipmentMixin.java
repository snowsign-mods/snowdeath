package net.snowsign.snowdeath.mixin;

import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static net.snowsign.snowdeath.MixinUtil.markIfPlayerDropped;

@Mixin(EntityEquipment.class)
public class EntityEquipmentMixin {
    @Redirect(method = "dropAll", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;"))
    private ItemEntity dropMarked(LivingEntity instance, ItemStack itemStack, boolean randomly, boolean thrownFromHand) {
        ItemEntity droppedItem = instance.drop(itemStack, true, false);
        markIfPlayerDropped(droppedItem, instance);
        return droppedItem;
    }
}
