package com.leozhu.battletower.util;

import com.leozhu.battletower.pokemon.PokeType;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class LoreUtil {
	
	public static int[] moveSlots = new int[] {0,0,0,0};

	public static PokeType getMoveType(ItemStack is) {

		return null;
	}

	public static Integer getPower(ItemStack is) {

		return null;
	}

	public static Integer getPP(ItemStack is) {

		return null;
	}

	public static double getHealth(ItemStack is) {
		NBTTagCompound nbttagcompound;
		NBTTagList nbttaglist;
		if(is != null && is.getTagCompound() != null && (nbttagcompound = is.getTagCompound().getCompoundTag("display")) != null
				&& nbttagcompound.getTagId("Lore") == 9 && (nbttaglist = nbttagcompound.getTagList("Lore", 8)).tagCount() == 25) {
			String healthString = nbttaglist.get(3).toString();
			if(healthString.contains("/")) {
				try {
					int split = healthString.indexOf("/");
					return (double) Integer.parseInt(healthString.substring(healthString.indexOf("§f") + 2, split-1)) /
							Integer.parseInt(healthString.substring(split+2, healthString.indexOf("§o")-3));
				} catch(NumberFormatException e) {
					return 0D;
				}
			}
		}
		return 0D;
	}

}
