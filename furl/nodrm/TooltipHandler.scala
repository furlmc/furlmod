package furl.nodrm

import java.util.List

import codechicken.nei.api.IConfigureNEI
import codechicken.nei.forge.GuiContainerManager
import codechicken.nei.forge.IContainerTooltipHandler
import emasher.gas.EmasherGas
import mcp.mobius.waila.api._

import cpw.mods.fml.common.event.FMLInterModComms

import net.minecraft.block.Block
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

object WailaTip {
	def init: Unit = FMLInterModComms.sendMessage(
		"Waila", "register", "furl.nodrm.WailaTip.callback"
	)
	def callback(reg: IWailaRegistrar): Unit = {
		reg.registerStackProvider(new MineGas, EmasherGas.mineGas.blockID)
	}

	class MineGas extends IWailaDataProvider {
		def getWailaStack(
			acc: IWailaDataAccessor, conf: IWailaConfigHandler
		): ItemStack = new ItemStack(Block.stone)
		def getWailaHead(
			stack: ItemStack, tip: List[String],
			acc: IWailaDataAccessor, conf: IWailaConfigHandler
		): List[String] = tip
		def getWailaBody(
			stack: ItemStack, tip: List[String],
			acc: IWailaDataAccessor, conf: IWailaConfigHandler
		): List[String] = tip
	}
}
