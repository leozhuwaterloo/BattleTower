package com.leozhu.battletower.event;

import java.util.HashMap;

import org.lwjgl.input.Keyboard;

import com.leozhu.battletower.util.GeneralUtil;
import com.leozhu.battletower.util.LoreUtil;

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




public class BattleTowerEvent {
	private enum State{
		UNKNOWN,
		START,
		FIND_PATH,
		IN_BATTLE,
		BATTLE_END
	};
	private Minecraft m;
	private State state;
	private KeyBinding battleTowerKey;
	private KeyBinding testKey;
	private int counter;
	private int chatcd;
	private Entity target;
	private HashMap<String, Integer> battleAttrib;

	public BattleTowerEvent() {
		m = Minecraft.getMinecraft();
		battleTowerKey = new KeyBinding("Start/Stop", Keyboard.KEY_P, "BattleTower");
		testKey = new KeyBinding("Test", Keyboard.KEY_LBRACKET, "BattleTower");
		ClientRegistry.registerKeyBinding(battleTowerKey);
		ClientRegistry.registerKeyBinding(testKey);
		battleAttrib = new HashMap<>();
		counter = 0;
		chatcd = 0;
		setState(State.UNKNOWN);
	}

	private void init() {
		if(state == State.UNKNOWN) {
			setState(State.START);
			battleAttrib.clear();
		} else {
			setState(State.UNKNOWN);
		}
		
		System.out.println("Battle Tower: " + (state == State.UNKNOWN ? "Off" : "On"));
	}

	@SubscribeEvent
	public void onReceiveChat(ClientChatReceivedEvent event) {
		String message = event.getMessage().getFormattedText();
		if(message.equals("§7Teleporting to §r§eBattle Tower§r§7...§r")) {
			setState(State.FIND_PATH);
		} else if(message.equals("§c§lAnnouncer> §r§fWelcome to Battle Tower§r")) {
			setState(State.IN_BATTLE);
		} else if(message.equals("§c§lAnnouncer> §r§fThat was a good run §r§5§lLEGEND §r§5Matchless_army§r§f!§r")) {
			setState(State.BATTLE_END);
		} else if(message.equals("§aRestored your Pokémon to full health!§r")) {
			setState(State.FIND_PATH);
		}
		
		System.out.println(message);
	}
	
	
	@SubscribeEvent
	public void tick(TickEvent event) {
		if (state != null) {
			++counter;
			if(counter % 200 == 0) {
				counter -= 200;
				++chatcd;
				tick();
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
					m.playerController.attackEntity(m.player, target);
					m.playerController.windowClick(m.player.openContainer.windowId, 20, 1, ClickType.PICKUP, m.player);
				} else if(distance > 18) {
					KeyBinding.setKeyBindState(m.gameSettings.keyBindForward.getKeyCode(), true);
				} else {
					GeneralUtil.lookAt(m.player, myPos, targetPos, distance);
					KeyBinding.setKeyBindState(m.gameSettings.keyBindForward.getKeyCode(), true);
				}
			}
		} else if (state == State.IN_BATTLE) {
			System.out.println(LoreUtil.getHealth(m.player.openContainer.getSlot(45).getStack()));
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
			System.out.println("Test");
			state = State.IN_BATTLE;
		}
	}

}
