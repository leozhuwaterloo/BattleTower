package com.leozhu.battletower.event;

import org.lwjgl.input.Keyboard;

import com.leozhu.battletower.util.GeneralUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoRespondEvent {
	private Minecraft m;
	private KeyBinding autoRespondKey;
	private BattleTowerEvent battleTowerEvent;
	private int counter;
	private boolean enabled;
	private int triggerCounter;


	public AutoRespondEvent(BattleTowerEvent battleTowerEvent) {
		m = Minecraft.getMinecraft();
		autoRespondKey = new KeyBinding("Auto Respond", Keyboard.KEY_O, "BattleTower");
		counter = 0;
		triggerCounter = -1;
		enabled = false;
		this.battleTowerEvent = battleTowerEvent;
	}

	@SubscribeEvent
	public void onReceiveChat(ClientChatReceivedEvent event) {
		String message = event.getMessage().getFormattedText().toLowerCase();
		if(message.contains("from") && (message.contains("mod") || message.contains("dev") || message.contains("owner"))) {
			triggerCounter = 20;
			GeneralUtil.printWarning(message);
		}
	}


	@SubscribeEvent
	public void tick(TickEvent event) {
		if (enabled) {
			++counter;
			if(counter % 200 == 0) {
				counter -= 200;
				--triggerCounter;
				if(triggerCounter < -100) {
					triggerCounter += 100;
				}
				tick();
			}
		}
	}

	private void tick() {
		if(triggerCounter == 19) {
			m.player.sendChatMessage(".");
		} else if(triggerCounter == 10) {
			m.player.sendChatMessage("/r hmm?");
		} else if(triggerCounter == 5) {
			this.battleTowerEvent.forceStop();
		} else if(triggerCounter == 1) {
			m.player.connection.getNetworkManager().channel().disconnect();
		}
	}

	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event)
	{
		if(autoRespondKey.isPressed()) {
			enabled = !enabled;
			System.out.println("Auto Respond: " + (enabled ? "On" : "Off"));
		}
	}
}
