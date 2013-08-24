package furl.nodrm

import java.util.Random

import cpw.mods.fml.common.IWorldGenerator

import emasher.gas.EmasherGas

import net.minecraft.block.Block
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkProvider

object MoreGas extends IWorldGenerator {
	def generate(
		r: Random, chunkX: Int, chunkZ: Int,
		world: World, p: IChunkProvider, p2: IChunkProvider
	): Unit = {
		val worldX = chunkX * 16
		val worldZ = chunkZ * 16
		def spawn(spawns: Int, y: Int): Unit = for (_ <- 1 to spawns) {
			val x = r.nextInt(16) + worldX
			val z = r.nextInt(16) + worldZ
			ReplaceBlocks(world, (x, y, z),
				(Block.stone.blockID, 0), (EmasherGas.mineGas.blockID, 0)
			)
		}

		val dist = math.sqrt(worldX * worldX + worldZ * worldZ)
		if (dist > Config.borderRadius) {
			val spawns = Config.mineGasSpawns * 100
			def y = r.nextInt(63)
			spawn(spawns, y)
		} else {
			val spawns = Config.mineGasSpawns
			def y = if (r.nextInt(1) == 0) 8 + r.nextInt(32) else 10 + r.nextInt(10)
			spawn(spawns, y)
		}
	}
}

object MoreFish extends IWorldGenerator {
	def generate(
		r: Random, chunkX: Int, chunkZ: Int,
		world: World, p: IChunkProvider, p2: IChunkProvider
	): Unit = {
		for (_ <- 1 to Config.silverfishSpawns) {
			val y = if (r.nextInt(1) == 0) 8 + r.nextInt(32) else 10 + r.nextInt(10)
			val x = r.nextInt(16) + chunkX * 16
			val z = r.nextInt(16) + chunkZ * 16
			ReplaceBlocks(world, (x, y, z),
				(Block.stone.blockID, 0), (Block.silverfish.blockID, 0)
			)
		}
	}
}
