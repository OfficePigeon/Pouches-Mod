package fun.wich;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class EntityPouchItem extends PouchItem {
	public EntityPouchItem(Settings settings) { super(settings); }
	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		PlayerEntity player = context.getPlayer();
		ItemStack stack = context.getStack();
		this.onEmptied(player, context.getWorld(), stack, context.getBlockPos().offset(context.getSide()));
		if (player != null) {
			player.incrementStat(Stats.USED.getOrCreateStat(this));
			player.setStackInHand(context.getHand(), getEmptiedStack(stack, player));
		}
		return ActionResult.SUCCESS;
	}
	@Override
	public Text getName(ItemStack stack) {
		TypedEntityData<EntityType<?>> typedEntityData = stack.get(DataComponentTypes.ENTITY_DATA);
		NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
		if (typedEntityData != null) {
			NbtCompound nbt = typedEntityData.copyNbtWithoutId();
			String name = nbt.getString("CustomName", null);
			Text mob;
			if (name != null) mob = Text.literal(name);
			else {
				EntityType<?> entityType = typedEntityData.getType();
				if (entityType != null) {
					mob = Text.translatable(entityType.getTranslationKey());
					boolean isBaby = customData != null && customData.copyNbt().getBoolean("IsBaby", false);
					if (isBaby) mob = Text.translatable("item.wich.pouch_baby").append(mob);
				}
				else mob = Text.translatable("item.wich.pouch_invalid");
			}
			return Text.translatable("item.wich.pouch_of").append(mob).formatted(Formatting.WHITE);
		}
		return super.getName(stack);
	}
	public void onEmptied(LivingEntity user, World world, ItemStack stack, BlockPos pos) {
		if (world instanceof ServerWorld serverWorld) {
			world.playSound(null, pos, Pouches.ITEM_POUCH_EMPTY, SoundCategory.NEUTRAL, 1.0f, 1.0f);
			TypedEntityData<EntityType<?>> typedEntityData = stack.get(DataComponentTypes.ENTITY_DATA);
			if (typedEntityData == null) return;
			EntityType<?> entityType = typedEntityData.getType();
			NbtCompound nbt = typedEntityData.copyNbtWithoutId();
			nbt.put("Pos", Vec3d.CODEC, pos.toBottomCenterPos());
			stack.set(DataComponentTypes.ENTITY_DATA, TypedEntityData.create(entityType, nbt));
			if (entityType == null) return;
			Entity entity = entityType.spawnFromItemStack(serverWorld, stack, user, pos, SpawnReason.BUCKET, true, false);
			if (entity != null) {
				world.emitGameEvent(user, GameEvent.ENTITY_PLACE, pos);
				if (entity instanceof MobEntity mob) mob.playAmbientSound();
			}
		}
	}
}
