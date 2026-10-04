package artemis.better_climbing.mixin;

import artemis.better_climbing.Config;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    private int climbDownTicks = 0;
    private int climbUpTicks = 0;
    private boolean climbingUpThisTick = false;

    @Shadow
    public abstract boolean onClimbable();

    @WrapOperation(
            method = "handleOnClimbable(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(DDD)D")
    )
    private double betterClimbing$horizontal(double speed, double min, double max, Operation<Double> original) {
        if (!level().isClientSide() || !Config.get().improveHorizontalMovement) {
            return original.call(speed, min, max);
        }
        if (!onGround() && isCrouching()) {
            return original.call(speed, min, max);
        }
        return speed;
    }

    @WrapOperation(
            method = "handleOnClimbable(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;",
            at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(DD)D")
    )
    private double betterClimbing$vertical(double currentY, double vanillaDownSpeed, Operation<Double> original) {
        if (!level().isClientSide() || !Config.get().fastDescent) {
            return original.call(currentY, vanillaDownSpeed);
        }

        double maxDownSpeed = Mth.clampedMap(getXRot(), 20, 90, vanillaDownSpeed, -0.4);
        if (maxDownSpeed < -0.15) {
            maxDownSpeed = Mth.clampedMap(climbDownTicks, 0, 60, maxDownSpeed, maxDownSpeed * 1.5);
        }
        return original.call(currentY, maxDownSpeed);
    }

    @Inject(
            method = "handleRelativeFrictionAndCalculateMovement(Lnet/minecraft/world/phys/Vec3;F)Lnet/minecraft/world/phys/Vec3;",
            at = @At("RETURN")
    )
    private void betterClimbing$timers(CallbackInfoReturnable<Vec3> cir) {
        if (!level().isClientSide()) {
            return;
        }

        Vec3 movement = cir.getReturnValue();
        if (Config.get().fastDescent && onClimbable() && movement.y < 0 && getXRot() > 20) {
            climbDownTicks++;
        } else {
            climbDownTicks = 0;
        }

        if (climbingUpThisTick) {
            climbUpTicks++;
            climbingUpThisTick = false;
        } else {
            climbUpTicks = 0;
        }
    }

    @ModifyArg(
            method = "handleRelativeFrictionAndCalculateMovement(Lnet/minecraft/world/phys/Vec3;F)Lnet/minecraft/world/phys/Vec3;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;<init>(DDD)V"),
            index = 1
    )
    private double betterClimbing$up(double vanillaClimbSpeed) {
        if (!level().isClientSide()) {
            return vanillaClimbSpeed;
        }

        if (!Config.get().fastClimbing && !Config.get().climbingJump) {
            return vanillaClimbSpeed;
        }

        climbingUpThisTick = true;
        double climbYSpeed = vanillaClimbSpeed;

        if (Config.get().fastClimbing) {
            double increased = vanillaClimbSpeed * 1.25;
            climbYSpeed = Mth.clampedMap(climbUpTicks, 0, 60, increased, increased * 2);
        }

        if (Config.get().climbingJump) {
            return Math.max(getDeltaMovement().y, climbYSpeed);
        }
        return climbYSpeed;
    }

    @WrapOperation(
            method = "handleRelativeFrictionAndCalculateMovement(Lnet/minecraft/world/phys/Vec3;F)Lnet/minecraft/world/phys/Vec3;",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;horizontalCollision:Z", opcode = Opcodes.GETFIELD)
    )
    private boolean betterClimbing$collision(LivingEntity entity, Operation<Boolean> original) {
        if (level().isClientSide()
                && Config.get().cancelUnintentionalCollision
                && entity instanceof LocalPlayer player) {
            return original.call(entity) && player.input.getMoveVector().length() > 0;
        }
        return original.call(entity);
    }
}
