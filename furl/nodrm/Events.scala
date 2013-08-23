package furl.nodrm

import scala.math

import net.minecraftforge.event.ForgeSubscribe
import net.minecraftforge.event.terraingen.ChunkProviderEvent
import net.minecraftforge.event.terraingen.DecorateBiomeEvent
import net.minecraft.world.World

object EventHandler {
	@ForgeSubscribe
	def postDecorate(event: DecorateBiomeEvent.Post): Unit = {
		for {
			y <- 0 to 127
			z <- (0 to 15).map(_+event.chunkZ)
			x <- (0 to 15).map(_+event.chunkX)
		} {
			Config.replaceBlocks.map(
				n => ReplaceBlocks(event.world, (x, y, z), n._1, n._2)
			)

			val dist = math.sqrt(x * x + z * z)
			if (dist > Config.borderRadius) {
				val excess = dist - Config.borderRadius
				val waterLevel = 63 - math.min(excess / 8, 63)
				if (y > waterLevel)
					ReplaceBlocks(event.world, (x, y, z), (9, 0), (0, 0))
			}
		}
	}

	@ForgeSubscribe
	def preBiomeReplace(event: ChunkProviderEvent.ReplaceBiomeBlocks) =
		BiomeBorder(event.biomeArray, event.chunkX, event.chunkZ)
}
