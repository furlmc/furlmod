package furl.nodrm

import java.util.EnumSet
import scala.collection.mutable.HashMap
import scala.math

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
	var tickCount = 0

	def apply(player: EntityPlayer): Unit = {
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

		if (tickCount % 60 == 0) {
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

		tickCount += 1
	}
}
