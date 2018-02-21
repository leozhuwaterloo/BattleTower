package com.leozhu.battletower;

import com.leozhu.battletower.event.AutoRespondEvent;
import com.leozhu.battletower.event.BattleTowerEvent;
import com.leozhu.battletower.event.UngrabMouseEvent;
import com.leozhu.battletower.proxy.CommonProxy;
import com.leozhu.battletower.util.Reference;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid= Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION)
public class Main {

	@Instance
	public static Main instance;

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.COMMON_PROXY_CLASS)
	public static CommonProxy proxy;


	@EventHandler
	public void preInit(FMLPreInitializationEvent event)  {
		BattleTowerEvent battleTowerEvent = new BattleTowerEvent();
		MinecraftForge.EVENT_BUS.register(battleTowerEvent);
		MinecraftForge.EVENT_BUS.register(new UngrabMouseEvent());
		MinecraftForge.EVENT_BUS.register(new AutoRespondEvent(battleTowerEvent));
	}
}
