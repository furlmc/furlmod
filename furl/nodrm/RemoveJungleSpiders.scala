package furl.nodrm

import biomesoplenty.api.Biomes
import biomesoplenty.api.Entities

import cpw.mods.fml.common.registry.EntityRegistry

import net.minecraft.entity.EntityLiving
import net.minecraft.entity.EnumCreatureType
import net.minecraft.world.biome.BiomeGenBase

object JungleSpiders {
	def remove = {
		Log.info("Removing BoP Jungle Spiders.")
		EntityRegistry.removeSpawn(
			Entities.JungleSpider.asInstanceOf[Class[EntityLiving]],
			EnumCreatureType.monster,
			Biomes.jungleNew.get.asInstanceOf[BiomeGenBase],
			Biomes.tropicalRainforest.get.asInstanceOf[BiomeGenBase],
			Biomes.oasis.get.asInstanceOf[BiomeGenBase],
			Biomes.tropics.get.asInstanceOf[BiomeGenBase]
		)
		EntityRegistry.removeSpawn(
			Entities.JungleSpider.asInstanceOf[Class[EntityLiving]],
			EnumCreatureType.creature,
			Biomes.bog.get.asInstanceOf[BiomeGenBase],
			Biomes.deadSwamp.get.asInstanceOf[BiomeGenBase],
			Biomes.fen.get.asInstanceOf[BiomeGenBase],
			Biomes.moor.get.asInstanceOf[BiomeGenBase],
			Biomes.quagmire.get.asInstanceOf[BiomeGenBase],
			Biomes.swamplandNew.get.asInstanceOf[BiomeGenBase]
		)
	}
}
