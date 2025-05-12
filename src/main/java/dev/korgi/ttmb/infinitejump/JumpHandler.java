package dev.korgi.ttmb.infinitejump;

import dev.korgi.ttmb.TTMB;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.TickEvent;

@Mod.EventBusSubscriber(modid = TTMB.MOD_ID, value = Dist.CLIENT)
public class JumpHandler {

    private static boolean wasJumpPressed = false;
    private static boolean isInAir = false;

    @SuppressWarnings("null")
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.screen != null) {
                return;
            }

            Player player = mc.player;

            boolean isOnGround = player.onGround();
            boolean isJumpPressed = mc.options.keyJump.isDown();

            if (!isJumpPressed && !wasJumpPressed && !isInAir) {
                isInAir = true;
                return;
            }

            if (isOnGround) {
                isInAir = false;
                return;
            }

            if (isJumpPressed && !wasJumpPressed && !isOnGround && isInAir) {
                Vec3 velocity = player.getDeltaMovement();
                double v = 0.2D;
                double yRot = Math.toRadians(player.getYRot());

                double xAdd = -Math.sin(yRot) * v;
                double zAdd = Math.cos(yRot) * v;

                player.setDeltaMovement(velocity.x + xAdd, 0.52D, velocity.z + zAdd);

                double radius = 1;

                double change = (10 * Math.PI) / 180;

                for (int i = 0; i < (2 * Math.PI) / change; i++) {

                    double xTrans = Math.cos(change * (i + 1));
                    double yTrans = Math.sin(change * (i + 1));

                    mc.level.addParticle(
                            ParticleTypes.CLOUD,
                            player.getX() + xTrans * radius,
                            player.getY(),
                            player.getZ() + yTrans * radius,
                            0, 0, 0);
                }

                player.hasImpulse = true;

            }

            wasJumpPressed = isJumpPressed;
        }
    }

}
