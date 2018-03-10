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
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;




public class BattleTowerEvent {
	private enum State{
		UNKNOWN,
		START,
		FIND_PATH,
		IN_BATTLE,
		BATTLE_END
	};
	private Minecraft m;
	private KeyBinding battleTowerKey;
	private KeyBinding testKey;
	private State state;
	private int counter;
	private int chatcd;
	private HashMap<String, Integer> battleAttrib;
	private Entity target;
	private Pokedex myPokemon, targetPokemon;
	private boolean advertising = false;

	public void forceStop() { this.state = State.UNKNOWN; }

	public BattleTowerEvent() {
		m = Minecraft.getMinecraft();
		battleTowerKey = new KeyBinding("Start/Stop", Keyboard.KEY_LBRACKET, "BattleTower");
		testKey = new KeyBinding("Test", Keyboard.KEY_RBRACKET, "BattleTower");
		ClientRegistry.registerKeyBinding(battleTowerKey);
		ClientRegistry.registerKeyBinding(testKey);
		setState(State.UNKNOWN);
		counter = 0;
		chatcd = 0;
		battleAttrib = new HashMap<>();
		Pokedex.addPokemonNickname("ANGRY_BIRDS", Pokedex.SCIZOR);
		Pokedex.addPokemonNickname("ANGRY_DRAGON", Pokedex.GYARADOS);
		Pokedex.addPokemonNickname("PINK_BARNEY", Pokedex.DRAGONITE);
		Pokedex.addPokemonNickname("SANTA_SNOWMAN", Pokedex.SWAMPERT);
		Pokedex.addPokemonNickname("TRICK_OR_TREAT", Pokedex.ALAKAZAM);
		Pokedex.addPokemonNickname("PEELED_BANANA", Pokedex.TYRANITAR);
		Pokedex.addPokemonNickname("ICY_YETI", Pokedex.ANNI_BLAZIKEN);
		Pokedex.addPokemonNickname("WITCH_MONSTER", Pokedex.ANNI_SNORLAX);
		Pokedex.addPokemonNickname("MOON_GUARDIAN", Pokedex.ABSOL);
		Pokedex.addPokemonNickname("SLOW_CANNON", Pokedex.PORYGON2);
		Pokedex.addPokemonNickname("GOD_ZEUS", Pokedex.ZAPDOS);
	}

	private void init() {
		if(state == State.UNKNOWN) {
			setState(State.START);
			counter = 0;
			chatcd = 0;
			target = null;
			initBattleEnd();
		} else {
			setState(State.UNKNOWN);
		}

		GeneralUtil.notifyPlayer(m.player, "Battle Tower: " + (state == State.UNKNOWN ? "Off" : "On"));
	}

	private void initBattleEnd() {
		battleAttrib.clear();
		myPokemon = null;
		targetPokemon = null;
	}

	@SubscribeEvent
	public void onReceiveChat(ClientChatReceivedEvent event) {
		if (state == null || state == State.UNKNOWN ) return;
		String message = event.getMessage().getFormattedText();
		if(message.equals("§7Teleporting to §r§eBattle Tower§r§7...§r")) {
			setState(State.FIND_PATH);
			m.player.inventory.currentItem = 5;
		} else if(message.equals("§c§lAnnouncer> §r§fWelcome to Battle Tower§r")) {
			setState(State.IN_BATTLE);
		} else if(message.equals("§c§lAnnouncer> §r§fThat was a good run §r§5§lLEGEND §r§5Matchless_army§r§f!§r")) {
			setState(State.BATTLE_END);
			initBattleEnd();
		} else if(message.equals("§aRestored your Pokémon to full health!§r")) {
			setState(State.FIND_PATH);
		} else {
			message = GeneralUtil.unformat(message).trim();
			if(message.contains("> Go ") && message.contains("!")) {
				targetPokemon = Pokedex.getPokemon(message.substring(message.indexOf("> Go ") + 5, message.indexOf("!")));
			} else if(message.contains("Go! ") && message.charAt(message.length()-1) == '!') {
				myPokemon = Pokedex.getPokemon(message.substring(message.indexOf("Go! ") + 4, message.length()-1));
				System.out.println(myPokemon);
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
			} else if(message.contains(" sent out ")) {
				targetPokemon = Pokedex.getPokemon(message.substring(message.indexOf(" sent out ") + 10, message.length()));
			} else if(message.equals("Battle over")) {
				initBattleEnd();
			} else if(message.equals("But it failed!")) {
				if(myPokemon != null && (myPokemon == Pokedex.ANNI_SNORLAX  || myPokemon == Pokedex.ABSOL)) {
					PokeUtil.noSuckerPunch.add(targetPokemon);
				}
			} else if(advertising && (message.contains("From") || message.contains("mm") || message.contains("would like to trade") || message.contains("requested to teleport"))){
				Toolkit.getDefaultToolkit().beep();
			}
		}
	}


	@SubscribeEvent
	public void tick(TickEvent event) {
		if (state != null && state != State.UNKNOWN ) {
			++counter;
			if(counter % 200 == 0) {
				++chatcd;
				tick();
				// System.out.println("Tick");
				// m.player.closeScreenAndDropStack();
				// playerSendMessage("/warp");
			}
			if(advertising) {
				// System.out.println(counter);
				if (counter % 350000 == 0) {
					playerSendMessage("Selling Xmas Salamance, Aura Gardevoir. Only Looking for Anni Snorlax and Perf Nature Egged Skinned.");
					counter -= 350000;
				} else if(counter % 347000 == 0) {
					playerSendMessage(".");
				}
			} else {
				if(counter % 200 == 0) {
					counter -= 200;
				}
			}
		}
	}

	private void tick() {
		if(state == State.START) {
			playerSendMessage("/warp");
			m.playerController.windowClick(m.player.openContainer.windowId, 1, 1, ClickType.PICKUP, m.player);
		} else if (state == State.FIND_PATH) {
			if(target == null) {
				target = GeneralUtil.findEntity(m, "Registrar");
			}else {
				Vec3d targetPos = target.getPositionVector();
				Vec3d myPos = m.player.getPositionVector();
				double distance = targetPos.distanceTo(myPos);
				unpressAll();
				if(distance < 3) {
					GeneralUtil.lookAt(m.player, myPos, targetPos, distance);
					if(m.player.openContainer.getSlot(0).inventory.getName().equals("Battle Tower")) {
						m.playerController.windowClick(m.player.openContainer.windowId, 20, 1, ClickType.PICKUP, m.player);
					} else {
						m.playerController.attackEntity(m.player, target);
					}
				} else if(distance > 18) {
					KeyBinding.setKeyBindState(m.gameSettings.keyBindForward.getKeyCode(), true);
				} else {
					GeneralUtil.lookAt(m.player, myPos, targetPos, distance);
					KeyBinding.setKeyBindState(m.gameSettings.keyBindForward.getKeyCode(), true);
				}
			}
		} else if (state == State.IN_BATTLE) {
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
		}
	}

	private void unpressAll() {
		KeyBinding.unPressAllKeys();
		KeyBinding.setKeyBindState(m.gameSettings.keyBindSprint.getKeyCode(), true);
	}

	private void playerSendMessage(String message) {
		if (chatcd > 6) {
			m.player.sendChatMessage(message);
			chatcd = 0;
		}
	}

	private void setState(State state) {
		if(state == State.UNKNOWN || (this.state == State.UNKNOWN && state == State.START)
				|| (this.state == State.START && state == State.FIND_PATH) || (this.state == State.FIND_PATH && state == State.IN_BATTLE)
				|| (this.state == State.IN_BATTLE && state == State.BATTLE_END) || (this.state == State.BATTLE_END && state == State.FIND_PATH)) {
			this.state = state;
			System.out.println("Changed State to: " + state.toString());
		} else {
			GeneralUtil.printWarning("Failed to set state from " + this.state.toString() + " to " + state.toString());
		}
	}

	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event) {
		if(battleTowerKey.isPressed()) {
			init();
		}else if(testKey.isPressed()) {
			if(!m.player.isPotionActive(Potion.getPotionById(16))) {
				PotionEffect nightVision = new PotionEffect(Potion.getPotionById(16), Integer.MAX_VALUE, 0);
				nightVision.setPotionDurationMax(true);
				m.player.addPotionEffect(nightVision);
			} else {
				m.player.removePotionEffect(Potion.getPotionById(16));
			}
			Vec3d targetPos = null;
			Vec3d myPos = m.player.getPositionVector();
			Entity target = null;
			double minD = 999;
			GeneralUtil.notifyPlayer(m.player, "Test");
			for(Entity entity : m.world.loadedEntityList){
				/*
				if(entity.getName().contains("Growlithe") && entity.getName().contains("§c")) {
					targetPos = entity.getPositionVector();
					if(targetPos.distanceTo(myPos) < minD) {
						target = entity;
						minD = targetPos.distanceTo(myPos);
					}
				}
				 */
				if(entity.getName().contains("BoBo07")) {
					target = entity;
					targetPos = entity.getPositionVector();
				}
			}
			if(target != null) {
				GeneralUtil.notifyPlayer(m.player, target.toString());
				double distance = targetPos.distanceTo(myPos);
				GeneralUtil.lookAt(m.player, myPos, targetPos, distance);
			}
		}
	}

}
