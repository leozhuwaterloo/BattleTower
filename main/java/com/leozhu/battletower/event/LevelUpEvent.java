package com.leozhu.battletower.event;

import java.awt.Toolkit;
import java.util.HashMap;

import org.lwjgl.input.Keyboard;

import com.leozhu.battletower.pokemon.Pokedex;
import com.leozhu.battletower.util.GeneralUtil;
import com.leozhu.battletower.util.LoreUtil;
import com.leozhu.battletower.util.PokeUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.ClickType;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class LevelUpEvent {
	private enum State{
		UNKNOWN,
		START,
		FIND_NPC,
		IN_BATTLE,
		GIVE_UP,
		BATTLE_END
	};
	private Minecraft m;
	private KeyBinding levelupKey;
	private State state = State.UNKNOWN;
	private int counter = 0;
	private int chatcd = 0;
	private Pokedex myPokemon, targetPokemon;
	private Entity target;
	private HashMap<String, Integer> battleAttrib = new HashMap<>();

	public LevelUpEvent() {
		m = Minecraft.getMinecraft();
		levelupKey = new KeyBinding("Level Up", Keyboard.KEY_BACKSLASH, "BattleTower");
		ClientRegistry.registerKeyBinding(levelupKey);
		
		Pokedex.addPokemonNickname("FATTY_DADDY", Pokedex.SNORLAX);
		Pokedex.addPokemonNickname("BOILED_EGG", Pokedex.CHANSEY);
		Pokedex.addPokemonNickname("ROCK_BIRD", Pokedex.SKARMORY);
		Pokedex.addPokemonNickname("WITCH_MONSTER", Pokedex.ANNI_SNORLAX);
		Pokedex.addPokemonNickname("MOON_GUARDIAN", Pokedex.ABSOL);
		Pokedex.addPokemonNickname("HARD_STONE", Pokedex.GOLEM);
		Pokedex.addPokemonNickname("BUILD_A_WALL", Pokedex.SNORLAX);
		Pokedex.addPokemonNickname("HELL_SATAN", Pokedex.UMBREON);
		Pokedex.addPokemonNickname("HARD_ROCK", Pokedex.SHUCKLE);
	}

	private void init() {
		if(state == State.UNKNOWN) {
			setState(State.START);
			counter = 0;
			chatcd = 0;
			initBattleEnd();
		} else {
			setState(State.UNKNOWN);
		}

		GeneralUtil.notifyPlayer(m.player, "Level Mode: " + (state == State.UNKNOWN ? "Off" : "On"));
	}

	private void initBattleEnd() {
		battleAttrib.clear();
		myPokemon = null;
		targetPokemon = null;
	}

	@SubscribeEvent
	public void onReceiveChat(ClientChatReceivedEvent event) {
		if(state == null || state == State.UNKNOWN) return;
		String message = event.getMessage().getFormattedText();
		if(message.equals("§aRestored your Pokémon to full health!§r")) {
			setState(State.FIND_NPC);
		} else if(message.equals("§c§lMatthew > §r§fHeh. Get lost, twerp.§r")) {
			setState(State.BATTLE_END);
			initBattleEnd();
		} else if(message.equals("§c§lMatthew > §r§fGuess it’s my turn, I won’t let you get this last piece, it’s worth a lot of coin!§r")) {
			setState(State.IN_BATTLE);
			targetPokemon = Pokedex.CLAYDOL;
		} else {
			message = GeneralUtil.unformat(message).trim();
			if(message.contains(" sent out ")) {
				targetPokemon = Pokedex.getPokemon(message.substring(message.indexOf(" sent out ") + 10, message.length()));
				if(targetPokemon == Pokedex.CRADILY || targetPokemon == Pokedex.RELICANTH) {
					setState(State.GIVE_UP);
				}
			} else if(message.contains("Go! ") && message.charAt(message.length()-1) == '!') {
				myPokemon = Pokedex.getPokemon(message.substring(message.indexOf("Go! ") + 4, message.length()-1));
			} else if(message.contains("Dance!")  && message.contains("used")) {
				Pokedex dancedPokemon = Pokedex.getPokemon(message.substring(0, message.indexOf(" ")));
				if(dancedPokemon == myPokemon) {
					if(dancedPokemon == Pokedex.SCIZOR || dancedPokemon == Pokedex.ANNI_SNORLAX || dancedPokemon == Pokedex.ABSOL) {
						battleAttrib.put(myPokemon.getName() + "Danced",
								battleAttrib.getOrDefault(myPokemon.getName() + "Danced", 0) + 2);
					} else {
						battleAttrib.put(myPokemon.getName() + "Danced",
								battleAttrib.getOrDefault(myPokemon.getName() + "Danced", 0) + 1);
					}
				}
			} else if(message.contains("reached Lv. 100")) {
				Toolkit.getDefaultToolkit().beep();
			}
		}
	}

	@SubscribeEvent
	public void tick(TickEvent event) {
		if (state != null && state != State.UNKNOWN) {
			++counter;
			if(counter % 200 == 0) {
				++chatcd;
				tick();
			}
		}
	}

	private void tick() {
		if(state == State.START) {
			target = GeneralUtil.findEntity(m, "Matthew");
			if(target != null) setState(State.FIND_NPC);
		} else if(state == State.FIND_NPC) {
			Vec3d targetPos = target.getPositionVector();
			Vec3d myPos = m.player.getPositionVector();
			double distance = targetPos.distanceTo(myPos);
			GeneralUtil.lookAt(m.player, myPos, targetPos, distance);
			m.player.inventory.currentItem = 0;
			m.playerController.attackEntity(m.player, target);
		} else if(state == State.IN_BATTLE) {
			String invName = m.player.openContainer.getSlot(0).inventory.getName();
			if(invName.contains("What will")) {
				m.playerController.windowClick(m.player.openContainer.windowId,
						PokeUtil.chooseMove(myPokemon, targetPokemon, m.player.openContainer, battleAttrib), 1, ClickType.PICKUP, m.player);
			}else if (invName.equals("Fight Or Run?")) {
				int nextPokemon = battleAttrib.getOrDefault("currPokemon", 0) + 1;
				nextPokemon %= 3;
				m.playerController.windowClick(m.player.openContainer.windowId,
						LoreUtil.pokemonSlots[nextPokemon], 1, ClickType.PICKUP, m.player);
				battleAttrib.put("currPokemon", nextPokemon);
			}
		} else if(state == State.BATTLE_END) {
			playerSendMessage("/pheal");
		} else if(state == State.GIVE_UP) {
			m.playerController.windowClick(m.player.openContainer.windowId, 35, 1, ClickType.PICKUP, m.player);
		}
	}

	private void playerSendMessage(String message) {
		if (chatcd > 6) {
			m.player.sendChatMessage(message);
			chatcd = 0;
		}
	}

	private void setState(State state) {
		if(state == State.UNKNOWN || (this.state == State.UNKNOWN && state == State.START)
				|| (this.state == State.START && state == State.FIND_NPC) || (this.state == State.FIND_NPC && state == State.IN_BATTLE)
				|| (this.state == State.IN_BATTLE && state == State.GIVE_UP) || (this.state == State.GIVE_UP && state == State.BATTLE_END)
				|| (this.state == State.BATTLE_END && state == State.FIND_NPC)) {
			this.state = state;
			System.out.println("Changed State to: " + state.toString());
		} else {
			GeneralUtil.printWarning("Failed to set state from " + this.state.toString() + " to " + state.toString());
		}
	}

	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event) {
		if(levelupKey.isPressed()) {
			init();
		}
	}
}
