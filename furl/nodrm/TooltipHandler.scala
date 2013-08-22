package furl.nodrm

import java.util.List

import codechicken.nei.api.IConfigureNEI
import codechicken.nei.forge.GuiContainerManager
import codechicken.nei.forge.IContainerTooltipHandler

import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.item.ItemStack

class NEIConfig extends IConfigureNEI {
	def loadConfig: Unit = {
		GuiContainerManager.addTooltipHandler(new TooltipHandler)
	}
	def getName = "FurlMod Tooltips"
	def getVersion = "what"
}

class TooltipHandler extends IContainerTooltipHandler {
	def handleTooltipFirst(
		gui: GuiContainer, x: Int, y: Int, tip: List[_]
	): List[_] = tip

	def handleItemTooltip(
		gui: GuiContainer,
		stack: ItemStack,
		tip: List[_]
	): List[_] = {
		val weight = ArmorWeight(stack)
		val tipString = tip.asInstanceOf[List[String]]
		if (weight > 0)
			tipString.add("§7§oWeight: §r§6§i%.1f§r".format(weight))
		tip
	}
}
