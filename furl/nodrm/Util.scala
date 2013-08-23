package furl.nodrm

import java.util.logging.Level
import java.util.logging.Logger

import cpw.mods.fml.common.FMLLog

import net.minecraft.world.World

object Log {
	val logger = Logger.getLogger("furl")
	logger.setParent(FMLLog.getLogger())

	def log(msg: String, level: Level) = logger.log(level, msg)
	def debug(msg: String) = log("[Debug] " + msg, Level.INFO)
	def info(msg: String) = log(msg, Level.INFO)
	def warn(msg: String) = log(msg, Level.WARNING)
	def error(msg: String) = log(msg, Level.SEVERE)
}

object ReplaceBlocks {
	def apply(world: World, point: (Int,Int,Int),
		a: (Int, Int), b: (Int, Int)
	) = {
		val (i, j, k) = point
		val block = world.getBlockId(i, j, k)
		val data = world.getBlockMetadata(i, j, k)
		if (block == a._1 && data == a._2) {
			world.setBlock(i, j, k, b._1, b._2, 2)
		}
	}
}
