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
		for (_ <- 1 to Config.mineGasSpawns) {
			val y = if (r.nextInt(1) == 0) 8 + r.nextInt(32) else 10 + r.nextInt(10)
			val x = r.nextInt(16) + chunkX * 16
			val z = r.nextInt(16) + chunkZ * 16
			ReplaceBlocks(world, (x, y, z),
				(Block.stone.blockID, 0), (EmasherGas.mineGas.blockID, 0)
			)
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
