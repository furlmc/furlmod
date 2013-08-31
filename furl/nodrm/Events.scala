package furl.nodrm

import scala.math

import biomesoplenty.world.ChunkProviderBOP

import net.minecraftforge.event.ForgeSubscribe
import net.minecraftforge.event.Event
import net.minecraftforge.event.terraingen.ChunkProviderEvent
import net.minecraftforge.event.terraingen.DecorateBiomeEvent
import net.minecraftforge.event.terraingen.OreGenEvent
import net.minecraftforge.event.terraingen.PopulateChunkEvent
import net.minecraft.world.gen.ChunkProviderGenerate
import net.minecraft.world.World

object EventHandler {
	@ForgeSubscribe
	def postDecorate(event: DecorateBiomeEvent.Post): Unit = {
		for {
			i <- (-1 to 1).map(_*16+event.chunkX)
			k <- (-1 to 1).map(_*16+event.chunkZ)
		} {
			if (event.world.blockExists(i, 0, k)) {
				for {
					x <- 0+i to 15+i
					z <- 0+k to 15+k
				} {
					for (y <- 0 to event.world.getHeightValue(x, z))
						process(event.world, x, y, z)
				}
			}
		}
	}

	def process(world: World, x: Int, y: Int, z: Int): Unit = {
		val dist = math.sqrt(x * x + z * z)
		if (world.provider.dimensionId == 0 && dist > Config.borderRadius) {
			val excess = dist - Config.borderRadius
			// Taper the sea level
			val waterLevel = 63 - math.min(excess / 8, 63)
			// Also taper flowing water
			def water = {
				val excessWater = y - waterLevel
				if (excessWater < 1) {
					val data = (excessWater * 9).toInt
					if (data < 8) {
						(8, data)
					} else {
						(0, 0)
					}
				} else {
					(0, 0)
				}
			}
			// Replace deadlands water
			if (y > waterLevel)
				ReplaceBlocks(world, x, y, z, 9, 0, water._1, water._2)

			// Replace blocks outside the world border
			Config.replaceBlocksDeadland.map(
				n => ReplaceBlocks(world, x, y, z, n._1._1, n._1._2, n._2._1, n._2._2)
			)
		}

		// Replace blocks for all chunks
		Config.replaceBlocks.map(
			n => ReplaceBlocks(world, x, y, z, n._1._1, n._1._2, n._2._1, n._2._2)
		)

		// Replace biome-specific blocks
		Config.biomeReplaceBlocks.map(
			n => if (world.getBiomeGenForCoords(x, z).biomeID == n._1)
				ReplaceBlocks(world, x, y, z, n._2._1, n._2._2, n._3._1, n._3._2)
		)
	}

	@ForgeSubscribe
	def preBiomeReplace(event: ChunkProviderEvent.ReplaceBiomeBlocks) =
		if (event.chunkProvider.isInstanceOf[ChunkProviderGenerate] ||
				event.chunkProvider.isInstanceOf[ChunkProviderBOP])
			// Convert all biomes outside the world border to wasteland
			BiomeBorder(event.biomeArray, event.chunkX, event.chunkZ)

	@ForgeSubscribe
	def oreGen(event: OreGenEvent.GenerateMinable): Unit = {
		val x = event.worldX
		val z = event.worldZ
		// Prevent ore generation outside of the world border
		if (math.sqrt(x * x + z * z) > Config.borderRadius - 8)
			event.setResult(Event.Result.DENY)
	}

	val denyTypesPopulate = List(
		PopulateChunkEvent.Populate.EventType.LAKE,
		PopulateChunkEvent.Populate.EventType.LAVA,
		PopulateChunkEvent.Populate.EventType.DUNGEON
	)
	@ForgeSubscribe
	def onPopulate(event: PopulateChunkEvent.Populate): Unit = {
		val x = event.chunkX * 16
		val z = event.chunkZ * 16
		// Prevent populating outside of the world border
		if (math.sqrt(x * x + z * z) > Config.borderRadius - 8 &&
			denyTypesPopulate.contains(event.`type`)
		) event.setResult(Event.Result.DENY)
	}
}
