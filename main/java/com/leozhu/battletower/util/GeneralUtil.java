package com.leozhu.battletower.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class GeneralUtil {
	public static void printWarning(String warning) {
		System.out.println("##################################");
		System.out.println(warning);
		System.out.println("##################################");
	}
	
	
	public static Entity findEntity(Minecraft m, String name) {
		for(Entity entity : m.world.loadedEntityList){
			if(entity.getName().contains(name)) {
				System.out.println(entity);
				return entity;
			}
		}
		printWarning("Failed to find entity: " + name);
		return null;
	}
	
	public static void lookAt(EntityPlayerSP player, Vec3d myPos, Vec3d targetPos, double distance) {
		double diffX = myPos.x - targetPos.x;
		double diffY = myPos.y - targetPos.y;
		double diffZ = myPos.z - targetPos.z;
		
		float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) + 90.0F;
		float pitch = (float) (Math.atan2(diffY, distance) * 180.0D / Math.PI);
		
		player.rotationYaw = yaw;
		player.rotationPitch = pitch;
	}
	
	public static void notifyPlayer(EntityPlayerSP player, String str) {
		player.sendStatusMessage(new TextComponentString(TextFormatting.GREEN + str), false);
	}
	
	public static String unformat(String str) {
		return str.replaceAll("§.", "");
	}
}
