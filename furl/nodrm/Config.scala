package furl.nodrm

import furl.Log

import scala.collection.mutable.HashMap
import scala.util.parsing.combinator._
import java.io.File

import net.minecraftforge.common.Configuration

object Config {
	type BlockPair = ((Int, Int), (Int, Int))
	type ArmorWeight = (Int, Float)

	var replaceBlocks = List[BlockPair]()
	var armorWeights = List[ArmorWeight]()
	var enchantWeights = List[ArmorWeight]()

	object BlocksParser extends RegexParsers {
		def apply(s: String): List[BlockPair] = {
			def emptyFailure(msg: String) = {
				Log.error(msg)
				List[BlockPair]()
			}
			parseAll(list, s) match {
				case Success(out, _) => out
				case Failure(msg, _) => emptyFailure(msg)
				case Error(msg, _) => emptyFailure(msg)
			}
		}
		def list = repsep(pair, ";")
		def pair = block ~ ("," ~> block) ^^ {
			case source ~ dest => (source -> dest)
		}
		def block = id ~ (":" ~> id).? ^^ {
			case block ~ data => (block, data.fold(0)(n=>n))
		}
		def id = "\\d+".r ^^ (_.toInt)
	}

	object WeightParser extends JavaTokenParsers {
		def apply(s: String): List[ArmorWeight] = {
			def emptyFailure(msg: String) = {
				Log.error(msg)
				List[ArmorWeight]()
			}
			parseAll(list, s) match {
				case Success(out, _) => out
				case Failure(msg, _) => emptyFailure(msg)
				case Error(msg, _) => emptyFailure(msg)
			}
		}
		def list = repsep(pair, ";")
		def pair = id ~ ("," ~> weight) ^^ {
			case source ~ dest => (source -> dest)
		}
		def id = "\\d+".r ^^ (_.toInt)
		def weight = floatingPointNumber ^^ (_.toFloat)
	}

	def load(path: String): Unit = {
		val config = new Configuration(new File(path))
		config.load

		val replaceBlocksString = config.get("worldgen", "block replace map", "",
			"old_id[:old_meta],new_id;..."
		).getString
		val armorWeightsString = config.get("armor", "armor to weight map", "",
			"(sum <= 10)=> normal hunger depletion; (sum = 20)=> 2x hunger depletion"
		).getString
		val enchantWeightsString = config.get("armor", "enchant to weight map", "",
			"these are multipliers applied to armor base weight"
		).getString

		replaceBlocks = BlocksParser(replaceBlocksString)
		armorWeights = WeightParser(armorWeightsString)
		enchantWeights = WeightParser(enchantWeightsString)

		config.save
	}
}
