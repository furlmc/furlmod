package furl.nodrm

import furl.Log

import java.util.EnumSet
import scala.collection.mutable.HashMap

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

	def tick(player: EntityPlayer): Unit = {
		val foodStats = player.getFoodStats
		val foodExhaustionField = foodStats
			.getClass
			.getDeclaredField("field_75126_c") // foodStats

		foodExhaustionField.setAccessible(true)
		val foodExhaustion = foodExhaustionField.getFloat(foodStats)

		val name = player.getEntityName
		if (!playerExhaustion.contains(name))
			playerExhaustion += name -> foodExhaustion

		val diff = foodExhaustion - playerExhaustion(name)
		if (diff > 0) {
			def weight: Float = {
				val armorIds = player.inventory.armorInventory
					.map(s => if (s == null) 0 else s.itemID)
				Log.debug("armor test: " + armorIds.mkString("[", ",", "]"))
				0
			}
			val newExhaustion = foodExhaustion + diff * weight
			foodExhaustionField.setFloat(foodStats, newExhaustion)
			playerExhaustion += name -> newExhaustion
		} else {
			playerExhaustion += name -> foodExhaustion
		}
	}
}
