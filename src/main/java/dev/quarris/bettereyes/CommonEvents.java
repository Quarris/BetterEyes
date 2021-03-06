package dev.quarris.bettereyes;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ConfiguredStructureTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Locale;

@Mod.EventBusSubscriber(modid = ModRef.ID)
public class CommonEvents {

    @SubscribeEvent
    public static void onEyeOfEnderUse(PlayerInteractEvent.RightClickItem event) {
        if (event.getItemStack().getItem() == Items.ENDER_EYE) {
            useStructureFinderOn(event.getWorld(), event.getPlayer(), event.getHand(), ConfiguredStructureTags.EYE_OF_ENDER_LOCATED);
            event.setCanceled(true);
        }
    }

    private static void useStructureFinderOn(Level level, Player player, InteractionHand hand, TagKey<ConfiguredStructureFeature<?, ?>> structureTag) {
        ItemStack held = player.getItemInHand(hand);
        player.startUsingItem(hand);
        if (!(level instanceof ServerLevel)) {
            return;
        }

        BlockPos structurePos = ((ServerLevel) level).findNearestMapFeature(structureTag, player.blockPosition(), 100, false);

        if (structurePos == null) { // Structure not found
            player.displayClientMessage(new TranslatableComponent("bettereyes.not_found"), ModConfigs.printToActionBar.get());
            return;
        }

        // Calculate direction to structure
        Vec3 destVector = player.position().vectorTo(Vec3.atCenterOf(structurePos));
        Vec3 dirVector = destVector.normalize();
        double distance = Math.sqrt(destVector.x * destVector.x + destVector.z * destVector.z);

        // Do a bunch of misc stuff, like triggers, sounds and particles
        if (player instanceof ServerPlayer && held.getItem() == Items.ENDER_EYE) {
            CriteriaTriggers.USED_ENDER_EYE.trigger((ServerPlayer) player, structurePos);
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_EYE_LAUNCH, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.random.nextFloat() * 0.4F + 0.8F));
        double particleSpread = Mth.clamp(distance / 2000, 0, 2);
        ((ServerLevel) level).sendParticles(
            ParticleTypes.PORTAL,
            player.getX() + dirVector.x,
            player.getY() + 1.5 + dirVector.y,
            player.getZ() + dirVector.z,
            100, particleSpread, particleSpread, particleSpread, 0.5);

        // Build the translation key
        boolean shatters = !player.getAbilities().instabuild && level.random.nextDouble() < ModConfigs.shatterChance.get();
        StringBuilder translation = new StringBuilder();
        translation.append("bettereyes");
        if (distance < 12.0) {
            translation.append(".found");
        } else {
            double angle = (Math.toDegrees(Math.atan2(dirVector.z, dirVector.x)) + 90 + 360) % 360;
            MyOwnDirectionCozIDontFuckingWannaCallItDirection direction = MyOwnDirectionCozIDontFuckingWannaCallItDirection.getDirectionFromAngle(angle);
            translation.append(".").append(direction.toString().toLowerCase(Locale.ROOT));
        }
        if (shatters) {
            translation.append(".shatter");
            held.shrink(1);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_EYE_DEATH, SoundSource.NEUTRAL, 1.0F, 1.0F);
            ((ServerLevel) level).sendParticles(
                new ItemParticleOption(ParticleTypes.ITEM, held),
                player.getX(),
                player.getY() + 1.2,
                player.getZ(),
                8, level.random.nextGaussian() * 0.15D, level.random.nextDouble() * 0.2D, level.random.nextGaussian() * 0.15D, 0.1);

        }

        player.displayClientMessage(new TranslatableComponent(translation.toString(), held.getHoverName()), ModConfigs.printToActionBar.get());
        player.awardStat(Stats.ITEM_USED.get(held.getItem()));
    }

}
