package fun.wich;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
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
import net.minecraft.world.rule.GameRule;
import net.minecraft.world.rule.GameRuleCategory;
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

	public static final GameRule<Boolean> ALLOW_POUCHING_BABY = GameRuleBuilder.forBoolean(true).category(GameRuleCategory.MOBS).buildAndRegister(Identifier.of(MOD_ID, "allow_pouching_baby_mobs"));
	public static final GameRule<Boolean> ALLOW_POUCHING_ALL = GameRuleBuilder.forBoolean(false).category(GameRuleCategory.MOBS).buildAndRegister(Identifier.of(MOD_ID, "allow_pouching_all_mobs"));

	public static final TagKey<EntityType<?>> TAG_POUCHABLE = TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, "pouchable"));
	public static final TagKey<EntityType<?>> TAG_NEVER_POUCHABLE = TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, "never_pouchable"));

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