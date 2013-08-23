package furl.nodrm

import scala.collection.mutable.HashMap
import scala.math
import scala.util.parsing.combinator._
import java.io.File

import net.minecraftforge.common.Configuration

object Config {
	type BlockPair = ((Int, Int), (Int, Int))
	type ArmorWeight = (Int, Float)

	var replaceBlocks = List[BlockPair]()

	var mineGasSpawns = 20
	var silverfishSpawns = 50

	var armorWeights = List[ArmorWeight]()
	var enchantWeights = List[ArmorWeight]()

	var miningFatigueWeight = 10d
	var slownessWeight = 12d
	var miningFatigueWeightPerLevel = 3d
	var slownessWeightPerLevel = 4d

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
		Log.info("Loading config...")
		val config = new Configuration(new File(path))
		config.load

		val replaceBlocksString = config.get("worldgen",
			"block replace map", "",
			"old_id[:old_meta],new_id;..."
		).getString

		// GasCraft stuff
		mineGasSpawns = config.get("worldgen",
			"mine gas spawns per chunk", "",
			"replaces stone with mine gas").getInt(20)
		silverfishSpawns = config.get("worldgen",
			"silverfish spawns per chunk", "",
			"replaces stone with silverfish").getInt(50)

		val armorWeightsString = config.get("armor",
			"armor to weight map", "",
			"(sum <= 10)=> normal hunger depletion; (sum = 20)=> 2x hunger depletion"
		).getString
		val enchantWeightsString = config.get("armor",
			"enchant to weight map", "",
			"these are multipliers applied to armor base weight"
		).getString

		replaceBlocks = BlocksParser(replaceBlocksString)
		armorWeights = WeightParser(armorWeightsString)
		enchantWeights = WeightParser(enchantWeightsString)

		miningFatigueWeight = config.get("armor",
			"mining fatigue weight", "",
			"while total weight of a player is >= this, mining fatigue is applied"
		).getDouble(10)
		slownessWeight = config.get("armor",
			"slowness weight", "",
			"..., ... slowness"
		).getDouble(12)

		miningFatigueWeightPerLevel = config.get("armor",
			"mining fatigue weight per level", "",
			"amount over the weight limit corresponds to higher levels of fatigue"
		).getDouble(3)
		slownessWeightPerLevel = config.get("armor",
			"slowness weight per level", "",
			"... slowness"
		).getDouble(4)

		config.save
	}

	def slownessLevel(weight: Float) = math.min((
			(weight - slownessWeight) / slownessWeightPerLevel
		).toInt, 3)
	def miningFatigueLevel(weight: Float) = math.min((
			(weight - miningFatigueWeight) / miningFatigueWeightPerLevel
		).toInt, 3)
}
