package net.goose.lifesteal.item.custom;

import com.mojang.authlib.GameProfile;
import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.component.ComponentRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.BannedPlayerList;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ReviveCrystalItem extends Item {
    public ReviveCrystalItem(Settings settings) {
        super(settings);
    }

    public void revivePlayer(World level, BlockPos blockPos, GameProfile gameprofile, PlayerEntity player, @Nullable BannedPlayerList userBanList) {
        level.removeBlock(blockPos, true);
        Entity entity = new LightningEntity(EntityType.LIGHTNING_BOLT, level);
        entity.setPosition(blockPos.toCenterPos());
        level.spawnEntity(entity);
        if (userBanList != null) {
            userBanList.remove(gameprofile);
        }

        level.getComponent(ComponentRegistry.UUID_AND_BLOCKPOS_MAP).setUUIDanditsBlockPos(gameprofile.getId(), blockPos);

        if (!LifeSteal.config.silentlyRevivePlayer.get()) {
            MutableText mutableComponent = Text.translatable("chat.message.lifesteal.revived_player");
            MutableText mutableComponent1 = Text.literal(gameprofile.getName());
            String combinedMessage = Formatting.YELLOW + mutableComponent1.getString() + mutableComponent.getString();
            PlayerManager playerlist = level.getServer().getPlayerManager();
            List<ServerPlayerEntity> playerList = playerlist.getPlayerList();
            for (ServerPlayerEntity serverPlayer : playerList) {
                serverPlayer.getCameraEntity().sendMessage(Text.literal(combinedMessage));
            }
        } else {
            player.sendMessage(Text.translatable("gui.lifesteal.revived"), true);
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext useOnContext) {
        if (!useOnContext.getWorld().isClient) {
            World level = useOnContext.getWorld();
            PlayerEntity player = useOnContext.getPlayer();

            if (level.getServer().isSingleplayer()) {
                player.sendMessage(Text.translatable("gui.lifesteal.singleplayer"), true);
                return super.useOnBlock(useOnContext);
            }

            ItemStack itemStack = useOnContext.getStack();
            BlockPos blockPos = useOnContext.getBlockPos();
            Block block = level.getBlockState(blockPos).getBlock();

            if (block == Blocks.PLAYER_HEAD || block == Blocks.PLAYER_WALL_HEAD) {
                BlockEntity blockEntity = level.getBlockEntity(blockPos);
                NbtCompound compoundTag = blockEntity.toInitialChunkDataNbt();

                GameProfile gameprofile;
                if (compoundTag != null) {
                    if (compoundTag.contains("SkullOwner", 10)) {
                        gameprofile = NbtHelper.toGameProfile(compoundTag.getCompound("SkullOwner"));
                    } else if (compoundTag.contains("SkullOwner", 8) && !StringUtils.isBlank(compoundTag.getString("SkullOwner"))) {
                        gameprofile = new GameProfile(null, compoundTag.getString("SkullOwner"));
                    } else {
                        gameprofile = null;
                    }
                } else {
                    gameprofile = null;
                }
                if (gameprofile != null) {
                    BannedPlayerList userBanList = level.getServer().getPlayerManager().getUserBanList();

                    if (userBanList.contains(gameprofile)) {
                        itemStack.decrement(1);
                        revivePlayer(level, blockPos, gameprofile, player, userBanList);
                    } else {
                        player.sendMessage(Text.translatable("gui.lifesteal.already_revived"), true);
                    }
                }
            } else {
                player.sendMessage(Text.translatable("gui.lifesteal.invaild_revive_block"), true);
            }
        }
        return super.useOnBlock(useOnContext);
    }
}
