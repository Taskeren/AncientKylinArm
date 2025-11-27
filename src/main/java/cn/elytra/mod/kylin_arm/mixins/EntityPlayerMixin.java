package cn.elytra.mod.kylin_arm.mixins;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import cn.elytra.mod.kylin_arm.KylinArmItem;

@Mixin(EntityPlayer.class)
public class EntityPlayerMixin {

    @WrapOperation(
        method = "getBreakSpeed(Lnet/minecraft/block/Block;ZIIII)F",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/EntityPlayer;isInsideOfMaterial(Lnet/minecraft/block/material/Material;)Z"))
    private boolean inWaterCheck(EntityPlayer instance, Material material, Operation<Boolean> original) {
        if (KylinArmItem.isKylinArmEquipped(instance)) {
            return false;
        }
        return original.call(instance, material);
    }

    @WrapOperation(
        method = "getBreakSpeed(Lnet/minecraft/block/Block;ZIIII)F",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/player/EntityPlayer;onGround:Z",
            opcode = Opcodes.GETFIELD))
    private boolean onGroundCheck(EntityPlayer instance, Operation<Boolean> original) {
        if (KylinArmItem.isKylinArmEquipped(instance)) {
            return true;
        }
        return original.call(instance);
    }

}
