package net.snowsign.snowdeath.mixin;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static net.snowsign.snowdeath.MixinUtil.markIfPlayerDropped;

@Debug(export = true)
@Mixin(Inventory.class)
public abstract class InventoryMixin {
    @Redirect(method = "dropAll()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;"))
    private ItemEntity dropMarked(Player instance, ItemStack itemStack, boolean dropAtSelf, boolean retainOwnership) {
        ItemEntity droppedItem = instance.drop(itemStack, true, false);
        markIfPlayerDropped(droppedItem, instance);
        return droppedItem;
    }
}