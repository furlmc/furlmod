package furl.nodrm

import furl.Log

import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.Mod.Init
import cpw.mods.fml.common.Mod.PostInit
import cpw.mods.fml.common.Mod.PreInit
import cpw.mods.fml.common.event.FMLInitializationEvent
import cpw.mods.fml.common.event.FMLPostInitializationEvent
import cpw.mods.fml.common.event.FMLPreInitializationEvent
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
	def preInit(e: FMLPreInitializationEvent) = {
		Log.debug("i like logspam, don't you?")
		// Armor Weight: Configged ids -> weight;
		// weight amounts => faster hunger depletion, then mining fatigue, then
		// slowness. Tick Handler.
		// GasCraft: more worldgen.  So, yeah.  Register our own, reflect for ids,
		// win the game.
		// More worldgen shit (biome-specific topblock replacement~)
		Config.load(e.getModConfigurationDirectory.getAbsolutePath + "/furlmod.cfg")
	}

	@Init
	def init(e: FMLInitializationEvent) = {
		MinecraftForge.EVENT_BUS.register(EventHandler)
	}

	@PostInit
	def postInit(e: FMLPostInitializationEvent) = {
		TickRegistry.registerTickHandler(TickHandler, Side.SERVER)
	}
}
