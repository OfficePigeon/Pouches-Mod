package fun.wich;

import net.minecraft.block.*;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Hand;
import net.minecraft.world.GameRules;

import java.util.Optional;

public class PouchItem extends Item {
	public PouchItem(Settings settings) { super(settings); }
	public ItemStack getEmptiedStack(ItemStack stack, PlayerEntity player) {
		return !player.isInCreativeMode() ? new ItemStack(Pouches.POUCH) : stack;
	}
	@Override
	public ActionResult useOnEntity(ItemStack stack, PlayerEntity player, LivingEntity livingEntity, Hand hand) {
		return tryPouch(player, hand, livingEntity).orElse(super.useOnEntity(stack, player, livingEntity, hand));
	}
	protected static Optional<ActionResult> tryPouch(PlayerEntity player, Hand hand, LivingEntity entity) {
		ItemStack stack = player.getStackInHand(hand);
		if (player.getEntityWorld() instanceof ServerWorld serverWorld) {
			if (!(entity instanceof PlayerEntity)) {
				GameRules rules = serverWorld.getGameRules();
				if (rules.getBoolean(Pouches.ALLOW_POUCHING_ALL) //Allow pouching all entities
						|| (rules.getBoolean(Pouches.ALLOW_POUCHING_BABY) //Allow babies whether tagged or not
							&& (entity.isBaby() || (entity instanceof SlimeEntity slime && slime.getSize() <= 1)))
						|| entity.getType().isIn(Pouches.TAG_POUCHABLE)) { //Limit to tagged entity types
					if (stack.getItem() == Pouches.POUCH && entity.isAlive()) {
						entity.playSound(Pouches.ITEM_POUCH_FILL, 1.0F, 1.0F);
						ItemStack itemStack2 = new ItemStack(Pouches.FILLED_POUCH);
						entity.dismountVehicle();
						entity.removeAllPassengers();
						copyDataToStack(entity, itemStack2);
						if (player.isInCreativeMode()) {
							if (!player.getInventory().contains(itemStack2)) player.getInventory().insertStack(itemStack2);
						}
						else if (stack.getCount() > 1) {
							stack.decrement(1);
							player.getInventory().insertStack(itemStack2);
						}
						else player.setStackInHand(hand, itemStack2);
						entity.discard();
						return Optional.of(ActionResult.SUCCESS);
					}
				}
			}
		}
		return Optional.empty();
	}

	protected static void copyDataToStack(LivingEntity entity, ItemStack stack) {
		try (ErrorReporter.Logging logging = new ErrorReporter.Logging(entity.getErrorReporterContext(), Pouches.LOGGER)) {
			NbtWriteView nbtWriteView = NbtWriteView.create(logging, entity.getRegistryManager());
			entity.saveData(nbtWriteView);
			stack.set(DataComponentTypes.ENTITY_DATA, TypedEntityData.create(entity.getType(), nbtWriteView.getNbt()));
			NbtCompound nbt = new NbtCompound();
			nbt.putBoolean("IsBaby", entity.isBaby());
			stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
		}
	}
}
