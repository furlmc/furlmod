package furl.nodrm

import scala.collection.mutable.HashMap
import scala.math
import scala.util.parsing.combinator._
import java.io.File

import net.minecraftforge.common.Configuration

object Config {
	type BlockPair = ((Int, Int), (Int, Int))
	type BiomeBlockPair = (Int, (Int, Int), (Int, Int))
	type ArmorWeight = (Int, Float)

	var replaceBlocks = List[BlockPair]()
	var biomeReplaceBlocks = List[BiomeBlockPair]()
	var replaceBlocksDeadland = List[BlockPair]()
	var borderRadius = 1000d

	var mineGasSpawns = 20
	var silverfishSpawns = 50

	var armorWeights = List[ArmorWeight]()
	var enchantWeights = List[ArmorWeight]()

	var miningFatigueWeight = 10d
	var weaknessWeight = 12d
	var miningFatigueWeightPerLevel = 3d
	var weaknessWeightPerLevel = 4d

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

	object BiomeBlocksParser extends RegexParsers {
		def apply(s: String): List[BiomeBlockPair] = {
			def emptyFailure(msg: String) = {
				Log.error(msg)
				List[BiomeBlockPair]()
			}
			parseAll(biomelist, s) match {
				case Success(out, _) => out
				case Failure(msg, _) => emptyFailure(msg)
				case Error(msg, _) => emptyFailure(msg)
			}
		}
		def biomelist = repsep(biomepair, ";")
		def biomepair = id ~ pair ^^ {
			case id ~ pair => (id, pair._1, pair._2)
		}
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

		// WorldGen stuff
		val replaceBlocksString = config.get("worldgen",
			"block replace map", "",
			"old_id[:old_meta],new_id;..."
		).getString

		replaceBlocks = BlocksParser(replaceBlocksString)

		val replaceBlocksDeadlandString = config.get("worldgen",
			"deadlands block replace map", "",
			"these are applied outside the world border radius"
		).getString

		replaceBlocksDeadland = BlocksParser(replaceBlocksDeadlandString)

		borderRadius = config.get("worldgen",
			"world border radius", "",
			"after this point, the world becomes barren"
		).getDouble(1000d)

		val biomeReplaceBlocksString = config.get("worldgen",
			"biome block replace map", "",
			"biome_id,old_id,new_id;..."
		).getString

		biomeReplaceBlocks = BiomeBlocksParser(biomeReplaceBlocksString)

		// GasCraft stuff
		mineGasSpawns = config.get("worldgen",
			"mine gas spawns per chunk", "",
			"replaces stone with mine gas").getInt(100)
		silverfishSpawns = config.get("worldgen",
			"silverfish spawns per chunk", "",
			"replaces stone with silverfish").getInt(20)

		// Armor stuff
		val armorWeightsString = config.get("armor",
			"armor to weight map", "",
			"(sum <= 10)=> normal hunger depletion; (sum = 20)=> 2x hunger depletion"
		).getString
		val enchantWeightsString = config.get("armor",
			"enchant to weight map", "",
			"these are multipliers applied to armor base weight"
		).getString

		armorWeights = WeightParser(armorWeightsString)
		enchantWeights = WeightParser(enchantWeightsString)

		miningFatigueWeight = config.get("armor",
			"mining fatigue weight", "",
			"while total weight of a player is >= this, mining fatigue is applied"
		).getDouble(10)
		miningFatigueWeightPerLevel = config.get("armor",
			"mining fatigue weight per level", "",
			"amount over the weight limit corresponds to higher levels of fatigue"
		).getDouble(3)

		weaknessWeight = config.get("armor",
			"weakness weight", "",
			"..., ... weakness"
		).getDouble(12)
		weaknessWeightPerLevel = config.get("armor",
			"weakness weight per level", "",
			"... slowness"
		).getDouble(4)

		config.save
	}

	def weaknessLevel(weight: Float) = math.min((
			(weight - weaknessWeight) / weaknessWeightPerLevel
		).toInt, 3)
	def miningFatigueLevel(weight: Float) = math.min((
			(weight - miningFatigueWeight) / miningFatigueWeightPerLevel
		).toInt, 3)
}
