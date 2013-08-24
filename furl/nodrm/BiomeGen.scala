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
		if (!inside(worldX, worldZ))
			biomeArray(z*16+x) = wasteland
	}

	def inside(x: Int, z: Int) = math.sqrt(x * x + z * z) < Config.borderRadius
}
