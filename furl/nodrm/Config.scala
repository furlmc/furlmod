package furl.nodrm

import furl.Log

import scala.collection.mutable.HashMap
import scala.util.parsing.combinator._
import java.io.File

import net.minecraftforge.common.Configuration

object Config {
	var replaceBlocks = List[((Int, Int), (Int, Int))]()

	def load(path: String): Unit = {
		val config = new Configuration(new File(path))
		config.load

		val replaceBlocksString = config.get("worldgen", "block replace map", "",
			"old_id[:old_meta],new_id;..."
		).getString
		val armorWeightsString = config.get("armor", "armor to weight map", "",
			"Default weight value is 0"
		).getString

		Log.info(s"config = $replaceBlocksString, $armorWeightsString")
		replaceBlocks = BlocksParser.exec(replaceBlocksString)

		config.save
	}
}

object BlocksParser extends RegexParsers {
	def id = "\\d+".r ^^ (_.toInt)
	def block = id ~ (":" ~> id).? ^^ {
		case block ~ data => (block, data.fold(0)(n=>n))
	}
	def pair = block ~ ("," ~> block) ^^ {
		case source ~ dest => (source -> dest)
	}
	def list = repsep(pair, ";")
	def exec(s: String): List[((Int, Int), (Int, Int))] = {
		def emptyFailure(msg: String) = {
			Log.error(msg)
			List[((Int, Int), (Int, Int))]()
		}
		parseAll(list, s) match {
			case Success(out, _) => out
			case Failure(msg, _) => emptyFailure(msg)
			case Error(msg, _) => emptyFailure(msg)
		}
	}
}
