package net.goose.lifesteal.block;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.item.ModItemGroup;
import net.minecraft.block.Block;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class ModBlocks {

    public static final Block HEART_CORE_BLOCK = registerBlock("heart_core_block",
            new Block(FabricBlockSettings.of(Material.METAL).strength(6f).requiresTool()), ModItemGroup.LIFESTEAL, ItemGroups.INGREDIENTS);
    public static final Block HEART_ORE = registerBlock("heart_ore",
            new ExperienceDroppingBlock(FabricBlockSettings.of(Material.STONE).strength(4f).requiresTool(),
                    UniformIntProvider.create(3, 7)), ModItemGroup.LIFESTEAL, ItemGroups.NATURAL);
    public static final Block DEEPSLATE_HEART_ORE = registerBlock("deepslate_heart_ore",
            new ExperienceDroppingBlock(FabricBlockSettings.of(Material.STONE).strength(5f).requiresTool(),
                    UniformIntProvider.create(3, 7)), ModItemGroup.LIFESTEAL, ItemGroups.NATURAL);
    public static final Block NETHERRACK_HEART_ORE = registerBlock("netherrack_heart_ore",
            new ExperienceDroppingBlock(FabricBlockSettings.of(Material.STONE).strength(2f).requiresTool().resistance(999f),
                    UniformIntProvider.create(5, 9)), ModItemGroup.LIFESTEAL, ItemGroups.NATURAL);

    public static Block registerBlock(String name, Block block, ItemGroup... itemGroups) {
        registerBlockItem(name, block, itemGroups);
        return Registry.register(Registries.BLOCK, new Identifier(LifeSteal.MOD_ID, name), block);
    }

    public static Item registerBlockItem(String name, Block block, ItemGroup... itemGroupList) {
        for (ItemGroup itemGroup : itemGroupList) {
            ItemGroupEvents.modifyEntriesEvent(itemGroup).register(entries -> entries.add(block));
        }
        return Registry.register(Registries.ITEM, new Identifier(LifeSteal.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void register() {
        LifeSteal.LOGGER.debug("Registering ModBlocks for " + LifeSteal.MOD_ID);
    }
}
