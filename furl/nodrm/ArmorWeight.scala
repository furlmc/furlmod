package furl.nodrm

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

object ArmorWeight {
	def apply(s: ItemStack): Float = {
		val armorId = if (s == null) 0 else s.itemID
		val enchantWeight = {
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
		}
		val weight = Config.armorWeights
			.filter(_._1 == armorId)
			.map(_._2 * enchantWeight)
		if (weight.isEmpty) 0 else weight.head
	}

	def apply(player: EntityPlayer): Float = {
		val armor = player.inventory.armorInventory
		(armor.map(ArmorWeight(_)) :\ 0f)(_+_) // sum armor pieces
	}

	def mult(player: EntityPlayer): Float = {
		val weight = ArmorWeight(player)
		if (weight <= 10) 0 else (weight - 10) / 10
	}
}
