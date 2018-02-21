package com.leozhu.battletower.util;

import java.util.ArrayList;
import java.util.HashMap;

import com.leozhu.battletower.pokemon.PokeType;
import com.leozhu.battletower.pokemon.Pokedex;

import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

public class PokeUtil {
	private static ArrayList<String> priorityMove = new ArrayList<String>() {
		{
			add("Extreme Speed");
			add("Bullet Punch");
		}
	};

	private static HashMap<Pokedex, PokeType> immunePokemon = new HashMap<Pokedex, PokeType>() {
		{
			put(Pokedex.GASTLY, PokeType.GROUND);
			put(Pokedex.HAUNTER, PokeType.GROUND);
			put(Pokedex.KOFFING, PokeType.GROUND);
			put(Pokedex.WEEZING, PokeType.GROUND);
			put(Pokedex.MISDREAVUS, PokeType.GROUND);
			put(Pokedex.UNOWN, PokeType.GROUND);
			put(Pokedex.VIBRAVA, PokeType.GROUND);
			put(Pokedex.FLYGON, PokeType.GROUND);
			put(Pokedex.LUNATONE, PokeType.GROUND);
			put(Pokedex.SOLROCK, PokeType.GROUND);
			put(Pokedex.BALTOY, PokeType.GROUND);
			put(Pokedex.CLAYDOL, PokeType.GROUND);
			put(Pokedex.DUSKULL, PokeType.GROUND);
			put(Pokedex.CHIMECHO, PokeType.GROUND);
			put(Pokedex.LATIAS, PokeType.GROUND);
			put(Pokedex.LATIAS, PokeType.GROUND);
			put(Pokedex.LATIOS, PokeType.GROUND);
			put(Pokedex.LATIOS, PokeType.GROUND);
			put(Pokedex.MISMAGIUS, PokeType.GROUND);
			put(Pokedex.CHINGLING, PokeType.GROUND);
			put(Pokedex.BRONZOR, PokeType.GROUND);
			put(Pokedex.BRONZONG, PokeType.GROUND);
			put(Pokedex.CARNIVINE, PokeType.GROUND);
			put(Pokedex.WATER_ROTOM, PokeType.GROUND);
			put(Pokedex.ICE_ROTOM, PokeType.GROUND);
			put(Pokedex.FLYING_ROTOM, PokeType.GROUND);
			put(Pokedex.FIRE_ROTOM, PokeType.GROUND);
			put(Pokedex.GHOST_ROTOM, PokeType.GROUND);
			put(Pokedex.GRASS_ROTOM, PokeType.GROUND);
			put(Pokedex.UXIE, PokeType.GROUND);
			put(Pokedex.MESPRIT, PokeType.GROUND);
			put(Pokedex.AZELF, PokeType.GROUND);
			put(Pokedex.GIRATINA, PokeType.GROUND);
			put(Pokedex.CRESSELIA, PokeType.GROUND);
			put(Pokedex.TYNAMO, PokeType.GROUND);
			put(Pokedex.EELEKTRIK, PokeType.GROUND);
			put(Pokedex.EELEKTROSS, PokeType.GROUND);
			put(Pokedex.CRYOGONAL, PokeType.GROUND);
			put(Pokedex.HYDREIGON, PokeType.GROUND);
			put(Pokedex.VIKAVOLT, PokeType.GROUND);

			put(Pokedex.POLIWAG, PokeType.WATER);
			put(Pokedex.POLIWHIRL, PokeType.WATER);
			put(Pokedex.POLIWRATH, PokeType.WATER);
			put(Pokedex.LAPRAS, PokeType.WATER);
			put(Pokedex.VAPOREON, PokeType.WATER);
			put(Pokedex.POLITOED, PokeType.WATER);
			put(Pokedex.WOOPER, PokeType.WATER);
			put(Pokedex.QUAGSIRE, PokeType.WATER);
			put(Pokedex.MANTINE, PokeType.WATER);
			put(Pokedex.MANTYKE, PokeType.WATER);
			put(Pokedex.MARACTUS, PokeType.WATER);
			put(Pokedex.FRILLISH, PokeType.WATER);
			put(Pokedex.JELLICENT, PokeType.WATER);
			put(Pokedex.VOLCANION, PokeType.WATER);
		}
	};


	public static int chooseMove(Pokedex myPokemon, Pokedex targetPokemon, Container openContainer, HashMap<String, Integer> battleAttrib) {
		System.out.println(myPokemon);
		System.out.println(targetPokemon);
		if(myPokemon == null || targetPokemon == null || openContainer == null || battleAttrib == null) return 0;
		int bestMoveSlot = 0;
		double highestPower = 0D;
		for(int moveSlot : LoreUtil.moveSlots) {
			ItemStack is = openContainer.getSlot(moveSlot).getStack();
			Integer ppLeft = LoreUtil.getPP(is);
			if(ppLeft == null || ppLeft > 0) {
				String moveName = GeneralUtil.unformat(is.getDisplayName());
				Integer power = LoreUtil.getPower(is);
				double health = LoreUtil.getHealth(openContainer.getSlot(
						LoreUtil.pokemonSlots[battleAttrib.getOrDefault("currPokemon", 0)]).getStack());
				if(power != null) {
					Double finalPower = (double) power;
					PokeType moveType = LoreUtil.getMoveType(is);
					if (moveType == myPokemon.getType1() || moveType == myPokemon.getType2()) {
						finalPower *= 1.2D;
					}
					finalPower *= PokeType.getEffect(moveType, targetPokemon.getType1());
					if(targetPokemon.getType2() != null) finalPower *= PokeType.getEffect(moveType, targetPokemon.getType2());

					if(immunePokemon.containsKey(targetPokemon) && immunePokemon.get(targetPokemon) == moveType) finalPower = 0D;
					if(priorityMove.contains(moveName)) {
						if(health < 0.2) {
							finalPower *= 5D;
						} else if(health < 0.6) {
							finalPower *= 2.3D;
						} else {
							finalPower *= 1.8D;
						}
					}

					if(finalPower > highestPower) {
						bestMoveSlot = moveSlot;
						highestPower = finalPower;
					}
				} else {
					if(moveName.contains("Dance")) {
						int dancedTime = battleAttrib.getOrDefault(myPokemon.getName() + "Danced", 0);
						if(dancedTime == 0) {
							bestMoveSlot = moveSlot;
							highestPower = 300D;
						} else if (dancedTime > 0 && dancedTime < 6) {
							if(health > 0.8D) {
								bestMoveSlot = moveSlot;
								highestPower = 200D;
							}
						}
					}
				}
			}
		}

		//System.out.println(bestMoveSlot);
		//bestMoveSlot = 0;
		return bestMoveSlot;
	}
}
