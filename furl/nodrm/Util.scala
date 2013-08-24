package furl.nodrm

import java.util.logging.Level
import java.util.logging.Logger

import cpw.mods.fml.common.FMLLog

object Log {
	val logger = Logger.getLogger("furl")
	logger.setParent(FMLLog.getLogger())

	def log(msg: String, level: Level) = logger.log(level, msg)
	def debug(msg: String) = log("[Debug] " + msg, Level.INFO)
	def info(msg: String) = log(msg, Level.INFO)
	def warn(msg: String) = log(msg, Level.WARNING)
	def error(msg: String) = log(msg, Level.SEVERE)
}
