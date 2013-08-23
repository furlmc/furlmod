package furl.nodrm

import biomesoplenty.api.Biomes

import net.minecraft.world.biome.BiomeGenBase

object BiomeBorder {
	val wasteland = Biomes.wasteland.get.asInstanceOf[BiomeGenBase]
	def apply(
		biomeArray: Array[BiomeGenBase], chunkX: Int, chunkZ: Int
	): Unit = for {
			z <- 0 to 15
			x <- 0 to 15
		} {
			val worldX = chunkX * 16 + x
			val worldZ = chunkZ * 16 + z
			val dist = math.sqrt(worldX * worldX + worldZ * worldZ)
			if (dist > Config.borderRadius)
				biomeArray(z*16+x) = wasteland
		}
}
