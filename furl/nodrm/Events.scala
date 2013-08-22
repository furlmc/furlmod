package furl.nodrm

import net.minecraftforge.event.ForgeSubscribe
import net.minecraftforge.event.terraingen.DecorateBiomeEvent

object EventHandler {
  @ForgeSubscribe
  def postDecorate(event: DecorateBiomeEvent.Post): Unit = {
    def replaceAt(point: (Int,Int,Int), a: (Int, Int), b: (Int, Int)) = {
      val (i, j, k) = point
      val block = event.world.getBlockId(i, j, k)
      val data = event.world.getBlockMetadata(i, j, k)
      if (block == a._1 && data == a._2) {
        event.world.setBlock(i, j, k, b._1, b._2, 2)
      }
    }

    for {
      y <- 0 to 127
      z <- (0 to 15).map(_+event.chunkZ)
      x <- (0 to 15).map(_+event.chunkX)
    } Config.replaceBlocks.map(n => replaceAt((x, y, z), n._1, n._2))
  }
}
