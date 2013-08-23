package furl.nodrm

import net.minecraftforge.event.ForgeSubscribe
import net.minecraftforge.event.terraingen.DecorateBiomeEvent
import net.minecraft.world.World

object EventHandler {
  @ForgeSubscribe
  def postDecorate(event: DecorateBiomeEvent.Post): Unit = {
    for {
      y <- 0 to 127
      z <- (0 to 15).map(_+event.chunkZ)
      x <- (0 to 15).map(_+event.chunkX)
    } Config.replaceBlocks.map(
      n => ReplaceBlocks(event.world, (x, y, z), n._1, n._2)
    )
  }
}
