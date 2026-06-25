package net.snowsign.snowdeath.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.snowsign.snowdeath.MarkedItem;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static net.snowsign.snowdeath.MixinUtil.getPlayerDeaths;

@Debug(export = true)
@Mixin(Inventory.class)
public abstract class InventoryMixin {
    @Redirect(method = "dropAll()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;"))
    private ItemEntity dropItemMarked(Player instance, ItemStack itemStack, boolean dropAtSelf, boolean retainOwnership) {
        ItemEntity droppedItem = instance.drop(itemStack, true, false);
        if (
            droppedItem != null
            && instance instanceof ServerPlayer serverPlayer
        ) {
            Integer deaths = getPlayerDeaths(serverPlayer.level().getServer(), serverPlayer.getUUID());
            ((MarkedItem) droppedItem).snowdeath$mark(serverPlayer.getUUID(), deaths != null ? deaths + 1 : 1);
        }
        return droppedItem;
    }
}