package com.leozhu.battletower.event;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MouseHelper;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class UngrabMouseEvent {

	private Minecraft m;
	private KeyBinding ungrabKey;
	private boolean isUngrabbed;
	private boolean originalFocusPauseSetting;
	private MouseHelper oldMouseHelper, newMouseHelper;
	private boolean doesGameWantUngrabbed;

	public UngrabMouseEvent() {
		m = Minecraft.getMinecraft();
		ungrabKey = new KeyBinding("Ungrab Mouse", Keyboard.KEY_U, "BattleTower");
		ClientRegistry.registerKeyBinding(ungrabKey);
		isUngrabbed = false;
		newMouseHelper =  new MouseHelper(){
			@Override
			public void mouseXYChange(){}
			@Override
			public void grabMouseCursor(){doesGameWantUngrabbed=false;}
			@Override
			public void ungrabMouseCursor(){doesGameWantUngrabbed=true;}
		};
	}


	void ungrabMouse()
	{
		if(!m.inGameHasFocus || isUngrabbed) return;

		originalFocusPauseSetting = m.gameSettings.pauseOnLostFocus;
		m.gameSettings.pauseOnLostFocus = false;
		if(oldMouseHelper==null) oldMouseHelper = m.mouseHelper;

		doesGameWantUngrabbed = !Mouse.isGrabbed();
		oldMouseHelper.ungrabMouseCursor();
		m.inGameHasFocus = true;
		m.mouseHelper = newMouseHelper;
		isUngrabbed = true;
	}


	void regrabMouse()
	{
		if(!isUngrabbed)return;		
		m.gameSettings.pauseOnLostFocus = originalFocusPauseSetting;
		m.mouseHelper = oldMouseHelper;
		if(!doesGameWantUngrabbed)m.mouseHelper.grabMouseCursor();
		oldMouseHelper = null;
		isUngrabbed = false;
	}


	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event)
	{
		if(ungrabKey.isPressed()) {
			if(!isUngrabbed)ungrabMouse();
			else regrabMouse();
		}
	}
}
