package furl.nodrm

import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.Mod.Init
import cpw.mods.fml.common.Mod.PostInit
import cpw.mods.fml.common.Mod.PreInit
import cpw.mods.fml.common.event.FMLInitializationEvent
import cpw.mods.fml.common.event.FMLPostInitializationEvent
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.common.registry.TickRegistry
import cpw.mods.fml.relauncher.Side

import net.minecraftforge.common.MinecraftForge

@Mod(
	modid = "FurlMod",
	name = "NO:DRM Glue",
	version = "what",
	modLanguage = "scala"
)
object FurlMod {
	@PreInit
	def preInit(e: FMLPreInitializationEvent): Unit = {
		Config.load(e.getModConfigurationDirectory.getAbsolutePath + "/furlmod.cfg")
	}

	@Init
	def init(e: FMLInitializationEvent): Unit = {
		MinecraftForge.EVENT_BUS.register(EventHandler)
		MinecraftForge.TERRAIN_GEN_BUS.register(EventHandler)
		MinecraftForge.ORE_GEN_BUS.register(EventHandler)
		WailaTip.init
		JungleSpiders.remove
	}

	@PostInit
	def postInit(e: FMLPostInitializationEvent): Unit = {
		TickRegistry.registerTickHandler(TickHandler, Side.SERVER)
		GameRegistry.registerWorldGenerator(MoreGas)
		GameRegistry.registerWorldGenerator(MoreFish)
	}
}
