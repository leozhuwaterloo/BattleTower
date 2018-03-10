package com.leozhu.battletower.util;

import com.leozhu.battletower.pokemon.PokeType;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class LoreUtil {

	public static int[] moveSlots = new int[] {1,5,28,32};

	public static int[] pokemonSlots = new int[] {45, 46, 47, 48, 49, 50};

	public static PokeType getMoveType(ItemStack is) {
		NBTTagList nbttaglist = getTagList(is);
		if(nbttaglist != null) {
			String moveTypeString = GeneralUtil.unformat(nbttaglist.get(0).toString());
			return PokeType.getType(moveTypeString.substring(1, moveTypeString.indexOf(" ")));
		}
		return null;
	}

	public static Integer getPower(ItemStack is) {
		NBTTagList nbttaglist = getTagList(is);
		if(nbttaglist != null) {
			String powerString = nbttaglist.get(4).toString();
			try {
				return Integer.parseInt(powerString.substring(powerString.indexOf("§f") + 2, powerString.length()-1));
			}catch(NumberFormatException e) {
				return null;
			}
		}
		return null;
	}

	public static Integer getPP(ItemStack is) {
		NBTTagList nbttaglist = getTagList(is);
		if(nbttaglist != null) {
			String ppString = nbttaglist.get(2).toString();
			if(ppString.contains("/")) {
				try {
					String keyString = "§f";
					if(!ppString.contains(keyString)) {
						keyString = "§c";
					}
					return Integer.parseInt(ppString.substring(ppString.indexOf(keyString) + 2, ppString.indexOf("/")-1));
				}catch(NumberFormatException e) {
					System.out.println(ppString);
					return null;
				}
			}
		}
		return null;
	}

	public static double getHealth(ItemStack is) {
		NBTTagList nbttaglist = getTagList(is);
		if(nbttaglist != null) {
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
	
	private static NBTTagList getTagList(ItemStack is) {
		NBTTagCompound nbttagcompound;
		NBTTagList nbttaglist;
		if(is != null && is.getTagCompound() != null && (nbttagcompound = is.getTagCompound().getCompoundTag("display")) != null
				&& nbttagcompound.getTagId("Lore") == 9 && (nbttaglist = nbttagcompound.getTagList("Lore", 8)).tagCount() >= 5) {
			return nbttaglist;
		}
		return null;
	}

}
