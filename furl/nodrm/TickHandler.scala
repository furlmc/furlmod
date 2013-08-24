package furl.nodrm

import java.util.EnumSet
import scala.collection.mutable.HashMap
import scala.math
import scala.util.Random

import cpw.mods.fml.common.ITickHandler
import cpw.mods.fml.common.TickType

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.potion.PotionEffect

object TickHandler extends ITickHandler {
	def tickStart(types: EnumSet[TickType], data: Object*) = ()
	def tickEnd(types: EnumSet[TickType], data: Object*) = {
		if (types.contains(TickType.PLAYER)) {
			PlayerTickHandler(data(0).asInstanceOf[EntityPlayer])
		}
	}
	def ticks = EnumSet.of(TickType.PLAYER)
	def getLabel = "FurlTicks"
}

object PlayerTickHandler {
	// #yoloswag #statevariables
	val playerExhaustion = HashMap[String, Float]()
	val tickCounts = HashMap[String, Int]()

	def apply(player: EntityPlayer): Unit = {
		val name = player.getEntityName
		doWeightHunger(player)

		val tickCount = tickCounts.getOrElse(name, 0)

		if (tickCount % 60 == 0) {
			doArmorEffects(player)
			doBorderEffects(player)
		}

		tickCounts += name -> (tickCount + 1)
	}

	def doWeightHunger(player: EntityPlayer): Unit = {
		val foodStats = player.getFoodStats
		val foodExhaustionField = foodStats
			.getClass
			.getDeclaredField("field_75126_c") // foodExhaustionLevel

		foodExhaustionField.setAccessible(true)
		val foodExhaustion = foodExhaustionField.getFloat(foodStats)

		val name = player.getEntityName
		if (!playerExhaustion.contains(name))
			playerExhaustion += name -> foodExhaustion

		val diff = foodExhaustion - playerExhaustion(name)
		if (diff > 0) {
			val newExhaustion = foodExhaustion + diff * ArmorWeight.mult(player)
			foodExhaustionField.setFloat(foodStats, newExhaustion)
			playerExhaustion += name -> newExhaustion
		} else {
			playerExhaustion += name -> foodExhaustion
		}
	}

	def doArmorEffects(player: EntityPlayer): Unit = {
		val weight = ArmorWeight(player)
		if (weight >= Config.miningFatigueWeight) {
			val level = Config.miningFatigueLevel(weight)
			val miningFatigue = new PotionEffect(4, 120, level)
			player.addPotionEffect(miningFatigue)
		}
		if (weight >= Config.slownessWeight) {
			val level = Config.slownessLevel(weight)
			val slowness = new PotionEffect(2, 120, level)
			player.addPotionEffect(slowness)
		}
	}

	def doBorderEffects(player: EntityPlayer): Unit = {
		if (List(0, -1).contains(player.dimension)) {
			val x = player.posX
			val z = player.posZ
			val dist = math.sqrt(x * x + z * z)
			val border = Config.borderRadius
			List(
				(1.0, 0.2, 4, 4),  // Mining Fatigue
				(1.2, 0.4, 4, 18), // Weakness
				(1.5, 0.1, 10, 17) // Hunger
			).foreach(n => if (dist > border * n._1) {
				val excess = dist / border - 1
				val level = math.min(excess / n._2, n._3).toInt
				val effect = new PotionEffect(n._4, 120, level)
				player.addPotionEffect(effect)
			})
		}
	}
}
