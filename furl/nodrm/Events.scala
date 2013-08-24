package furl.nodrm

import scala.math

import net.minecraftforge.event.ForgeSubscribe
import net.minecraftforge.event.Event
import net.minecraftforge.event.terraingen.ChunkProviderEvent
import net.minecraftforge.event.terraingen.DecorateBiomeEvent
import net.minecraftforge.event.terraingen.OreGenEvent
import net.minecraftforge.event.terraingen.PopulateChunkEvent
import net.minecraft.world.World

object EventHandler {
	@ForgeSubscribe
	def postDecorate(event: DecorateBiomeEvent.Post): Unit = {
		for {
			y <- 0 to 127
			z <- (0 to 15).map(_+event.chunkZ)
			x <- (0 to 15).map(_+event.chunkX)
		} {
			val dist = math.sqrt(x * x + z * z)
			if (dist > Config.borderRadius) {
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
					ReplaceBlocks(event.world, (x, y, z), (9, 0), water)

				// Replace blocks outside the world border
				Config.replaceBlocksDeadland.map(
					n => ReplaceBlocks(event.world, (x, y, z), n._1, n._2)
				)
			}

			// Replace blocks for all chunks
			Config.replaceBlocks.map(
				n => ReplaceBlocks(event.world, (x, y, z), n._1, n._2)
			)
		}
	}

	@ForgeSubscribe
	def preBiomeReplace(event: ChunkProviderEvent.ReplaceBiomeBlocks) =
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
