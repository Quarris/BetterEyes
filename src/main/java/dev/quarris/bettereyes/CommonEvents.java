package dev.quarris.bettereyes;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Locale;

@Mod.EventBusSubscriber(modid = ModRef.ID)
public class CommonEvents {

    @SubscribeEvent
    public static void onEyeOfEnderUse(PlayerInteractEvent.RightClickItem event) {
        if (event.getItemStack().getItem() == Items.ENDER_EYE) {
            useStructureFinderOn(event.getWorld(), event.getPlayer(), event.getHand(), Structure.STRONGHOLD);
            event.setCanceled(true);
        }
    }

    private static void useStructureFinderOn(World level, PlayerEntity player, Hand hand, Structure<?> structure) {
        ItemStack held = player.getItemInHand(hand);
        player.startUsingItem(hand);
        if (!(level instanceof ServerWorld)) {
            return;
        }

        BlockPos structurePos = ((ServerWorld) level).getChunkSource().getGenerator().findNearestMapFeature((ServerWorld) level, structure, player.blockPosition(), 100, false);

        if (structurePos == null) { // Structure not found
            player.displayClientMessage(new TranslationTextComponent("bettereyes.not_found"), ModConfigs.printToActionBar.get());
            return;
        }

        // Calculate direction to structure
        Vector3d destVector = player.position().vectorTo(Vector3d.atCenterOf(structurePos));
        Vector3d dirVector = destVector.normalize();
        double distance = Math.sqrt(destVector.x * destVector.x + destVector.z * destVector.z);

        // Do a bunch of misc stuff, like triggers, sounds and particles
        if (player instanceof ServerPlayerEntity && held.getItem() == Items.ENDER_EYE) {
            CriteriaTriggers.USED_ENDER_EYE.trigger((ServerPlayerEntity) player, structurePos);
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_EYE_LAUNCH, SoundCategory.NEUTRAL, 0.5F, 0.4F / (level.random.nextFloat() * 0.4F + 0.8F));
        double particleSpread = MathHelper.clamp(distance / 2000, 0, 2);
        ((ServerWorld) level).sendParticles(
            ParticleTypes.PORTAL,
            player.getX() + dirVector.x,
            player.getY() + 1.5 + dirVector.y,
            player.getZ() + dirVector.z,
            100, particleSpread, particleSpread, particleSpread, 0.5);

        // Build the translation key
        boolean shatters = !player.abilities.instabuild && level.random.nextDouble() < ModConfigs.shatterChance.get();
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
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_EYE_DEATH, SoundCategory.NEUTRAL, 1.0F, 1.0F);
            ((ServerWorld) level).sendParticles(
                new ItemParticleData(ParticleTypes.ITEM, held),
                player.getX(),
                player.getY() + 1.2,
                player.getZ(),
                8, level.random.nextGaussian() * 0.15D, level.random.nextDouble() * 0.2D, level.random.nextGaussian() * 0.15D, 0.1);

        }

        player.displayClientMessage(new TranslationTextComponent(translation.toString(), held.getHoverName()), ModConfigs.printToActionBar.get());
        player.awardStat(Stats.ITEM_USED.get(held.getItem()));
    }

}
