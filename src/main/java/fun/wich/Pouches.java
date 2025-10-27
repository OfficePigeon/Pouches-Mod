package fun.wich;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class Pouches implements ModInitializer {
	public static final String MOD_ID = "wich";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final SoundEvent ITEM_POUCH_EMPTY = register("item.pouch.empty");
	public static final SoundEvent ITEM_POUCH_FILL = register("item.pouch.fill");
	private static SoundEvent register(String path) {
		Identifier id = Identifier.of(MOD_ID, path);
		return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
	}

	public static final Item POUCH = register("pouch", PouchItem::new, new Item.Settings());
	public static final Item FILLED_POUCH = register("filled_pouch", EntityPouchItem::new, new Item.Settings());

	public static final GameRules.Key<GameRules.BooleanRule> ALLOW_POUCHING_BABY = GameRuleRegistry.register("allowPouchingBabyMobs", GameRules.Category.MOBS, GameRuleFactory.createBooleanRule(true));
	public static final GameRules.Key<GameRules.BooleanRule> ALLOW_POUCHING_ALL = GameRuleRegistry.register("allowPouchingAllMobs", GameRules.Category.MOBS, GameRuleFactory.createBooleanRule(false));

	public static final TagKey<EntityType<?>> TAG_POUCHABLE = TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, "pouchable"));

	public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
		RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, name));
		Item item = itemFactory.apply(settings.registryKey(key));
		Registry.register(Registries.ITEM, key, item);
		return item;
	}

	@Override
	public void onInitialize() {
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(itemGroup -> itemGroup.add(POUCH));
	}
}