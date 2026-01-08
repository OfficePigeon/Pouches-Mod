package fun.wich.mixin;

import fun.wich.PouchItem;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractDonkeyEntity.class)
public class PouchesAbstractDonkeyEntityMixin {
	@Inject(method="interactMob", at=@At("HEAD"), cancellable=true)
	public void AllowPouching_interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		PouchItem.Mixin_TryPouching(player, hand, cir);
	}
}
