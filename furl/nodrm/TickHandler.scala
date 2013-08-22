package furl.nodrm

import furl.Log

import java.util.EnumSet
import scala.collection.mutable.HashMap
import scala.math

import cpw.mods.fml.common.ITickHandler
import cpw.mods.fml.common.TickType

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound

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
			val weight = armorWeight(player)
			Log.debug("%f".format(weight))
			val newExhaustion = foodExhaustion + diff * weight
			foodExhaustionField.setFloat(foodStats, newExhaustion)
			playerExhaustion += name -> newExhaustion
		} else {
			playerExhaustion += name -> foodExhaustion
		}
	}

	def armorWeight(player: EntityPlayer): Float = {
		val armor = player.inventory.armorInventory

		val armorIds = armor.map(s => if (s == null) 0 else s.itemID)
		val enchantWeights = armor.map(s => {
			if (s == null || s.getEnchantmentTagList == null) {
				1
			} else {
				val enchants = s.getEnchantmentTagList
				((0 until enchants.tagCount).map(i => {
						val compound = enchants.tagAt(i).asInstanceOf[NBTTagCompound]
						val id = compound.getShort("id")
						val level = compound.getShort("lvl")
						Config.enchantWeights
							.filter(_._1 == id)
							.map(n => math.pow(n._2, level).toFloat)
					}).flatten :\ 1f)(_*_) // multiplicative weights!
			}
		})

		val weight = ((armorIds zip enchantWeights)
			.map(n => Config.armorWeights
				.filter(n._1 == _._1)
				.map(n._2 * _._2)
			).flatten :\ 0f)(_+_)
		if (weight <= 10) 0 else (weight - 10) / 10
	}
}
