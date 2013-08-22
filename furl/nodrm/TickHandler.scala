package furl.nodrm

import furl.Log

import java.util.EnumSet
import scala.collection.mutable.HashMap
import scala.math

import cpw.mods.fml.common.ITickHandler
import cpw.mods.fml.common.TickType

import net.minecraft.entity.player.EntityPlayer

object TickHandler extends ITickHandler {
	def tickStart(types: EnumSet[TickType], data: Object*) = ()
	def tickEnd(types: EnumSet[TickType], data: Object*) = {
		if (types.contains(TickType.PLAYER)) {
			PlayerTickHandler.tick(data(0).asInstanceOf[EntityPlayer])
		}
	}
	def ticks = EnumSet.of(TickType.PLAYER)
	def getLabel = "logspamlol"
}

object PlayerTickHandler {
	val playerExhaustion = HashMap[String, Float]()
	var tickCount = 0

	def tick(player: EntityPlayer): Unit = {
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
			val weight = ArmorWeight(player)
			Log.debug("%f".format(weight))
			val newExhaustion = foodExhaustion + diff * weight
			foodExhaustionField.setFloat(foodStats, newExhaustion)
			playerExhaustion += name -> newExhaustion
		} else {
			playerExhaustion += name -> foodExhaustion
		}

		if (tickCount % 30 == 0) {
			// Handle debuffs...
		}

		tickCount += 1
	}
}
