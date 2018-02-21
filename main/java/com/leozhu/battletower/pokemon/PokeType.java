package com.leozhu.battletower.pokemon;

import java.util.HashMap;

public enum PokeType {
	NORMAL(0, "Normal"),
	FIRE(1, "Fire"),
	WATER(2, "Water"),
	ELECTRIC(3, "Electric"),
	GRASS(4, "Grass"),
	ICE(5, "Ice"),
	FIGHTING(6, "Fighting"),
	POISON(7, "Poison"),
	GROUND(8, "Ground"),
	FLYING(9, "Flying"),
	PSYCHIC(10, "Psychic"),
	BUG(11, "Bug"),
	ROCK(12, "Rock"),
	GHOST(13, "Ghost"),
	DRAGON(14, "Dragon"),
	DARK(15, "Dark"),
	STEEL(16, "Steel"),
	FAIRY(17, "Fairy");

	private int id;
	private String name;
	private PokeType(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {return id;}
	public String getName() {return name;}

	public static double getEffect(PokeType attack, PokeType target) {
		return effectiveness[attack.getId()][target.getId()];
	}

	private static double[][] effectiveness = new double[][]{
		new double[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0.5, 0, 1, 1, 0.5, 1},
		new double[]{1, 0.5, 0.5, 1, 2, 2, 1, 1, 1, 1, 1, 2, 0.5, 1, 0.5, 1, 2, 1},
		new double[]{1, 2, 0.5, 1, 0.5, 1, 1, 1, 2, 1, 1, 1, 2, 1, 0.5, 1, 1, 1},
		new double[]{1, 1, 2, 0.5, 0.5, 1, 1, 1, 0, 2, 1, 1, 1, 1, 0.5, 1, 1, 1},
		new double[]{1, 0.5, 2, 1, 0.5, 1, 1, 0.5, 2, 0.5, 1, 0.5, 2, 1, 0.5, 1, 0.5, 1},
		new double[]{1, 0.5, 0.5, 1, 2, 0.5, 1, 1, 2, 2, 1, 1, 1, 1, 2, 1, 0.5, 1},
		new double[]{2, 1, 1, 1, 1, 2, 1, 0.5, 1, 0.5, 0.5, 0.5, 2, 0, 1, 2, 2, 0.5},
		new double[]{1, 1, 1, 1, 2, 1, 1, 0.5, 0.5, 1, 1, 1, 0.5, 0.5, 1, 1, 0, 2},
		new double[]{1, 2, 1, 2, 0.5, 1, 1, 2, 1, 0, 1, 0.5, 2, 1, 1, 1, 2, 1},
		new double[]{1, 1, 1, 0.5, 2, 1, 2, 1, 1, 1, 1, 2, 0.5, 1, 1, 1, 0.5, 1},
		new double[]{1, 1, 1, 1, 1, 1, 2, 2, 1, 1, 0.5, 1, 1, 1, 1, 0, 0.5, 1},
		new double[]{1, 0.5, 1, 1, 2, 1, 0.5, 0.5, 1, 0.5, 2, 1, 1, 0.5, 1, 2, 0.5, 0.5},
		new double[]{1, 2, 1, 1, 1, 2, 0.5, 1, 0.5, 2, 1, 2, 1, 1, 1, 1, 0.5, 1},
		new double[]{0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 2, 1, 0.5, 1, 1},
		new double[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 0.5, 0},
		new double[]{1, 1, 1, 1, 1, 1, 0.5, 1, 1, 1, 2, 1, 1, 2, 1, 0.5, 1, 0.5},
		new double[]{1, 0.5, 0.5, 0.5, 1, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 0.5, 2},
		new double[]{1, 0.5, 1, 1, 1, 1, 2, 0.5, 1, 1, 1, 1, 1, 1, 2, 2, 0.5, 1}
	};

	public static PokeType getType(String pokeType) {
		return pokeTypeMap.getOrDefault(pokeType, null);
	}

	private static HashMap<String, PokeType> pokeTypeMap = initMap();
	private static HashMap<String, PokeType> initMap(){
		HashMap<String, PokeType> map = new HashMap<>();
		for(PokeType pokeType : PokeType.values()) {
			map.put(pokeType.getName(), pokeType);
		}
		return map;
	}
}
