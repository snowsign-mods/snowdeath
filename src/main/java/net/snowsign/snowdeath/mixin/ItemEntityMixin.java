package net.snowsign.snowdeath.mixin;

import net.minecraft.core.UUIDUtil;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.snowsign.snowdeath.MarkedItem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

import static net.snowsign.snowdeath.MixinUtil.getPlayerDeaths;

@Debug(export = true)
@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin implements MarkedItem {
    @Unique
    private int deathCount = Short.MIN_VALUE;

    @Unique
    private @Nullable UUID deceased = null;
    @Shadow
    private int age;


    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void writeMarkedData(ValueOutput output, CallbackInfo ci) {
        output.putShort("DeathCount", (short) this.deathCount);
        output.storeNullable("Deceased", UUIDUtil.CODEC, this.deceased);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    public void readMarkedData(ValueInput input, CallbackInfo ci) {
        this.deathCount = input.getShortOr("DeathCount", (short) -1);
        this.deceased = input.read("Deceased", UUIDUtil.CODEC).orElse(null);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;discard()V", ordinal = 1))
    public void discardIfNotMarked(ItemEntity instance) {
        if (this.deceased == null) {
            instance.discard();
            return;
        }

        Integer deaths = getPlayerDeaths(instance.level().getServer(), this.deceased);
        if (
            deaths == null
            || deaths - ((MarkedItem) instance).snowdeath$getDeathCount() < 5
        ) {
            this.age = 6000; // Prevent overflow
            return;
        }
        instance.discard();
    }

    @Override
    public void snowdeath$mark(UUID deceased, int deaths) {
        this.deceased = deceased;
        this.deathCount = deaths;
    }

    @Override
    public int snowdeath$getDeathCount() {
        return this.deathCount;
    }
}