package net.quiltservertools.interdimensional

import net.quiltservertools.interdimensional.world.PortalManager
import net.quiltservertools.interdimensional.world.RuntimeWorldManager
import com.google.gson.*
import net.minecraft.util.Identifier
import xyz.nucleoid.fantasy.RuntimeWorldConfig
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.function.Consumer

class Config private constructor(json: JsonElement, newPath: Path) {
    private val configVersion: Int

    fun shutdown() {
        val json = JsonObject()
        val worlds = JsonArray()
        RuntimeWorldManager.closeAll().forEach(Consumer { element: JsonObject ->
            worlds.add(
                element
            )
        })
        json.addProperty("version", configVersion)
        json.add("worlds", worlds)
        json.add("portals", PortalManager.toJson())
        val gson = GsonBuilder().setPrettyPrinting().create()
        try {
            Files.write(path, gson.toJson(json).toByteArray())
        } catch (e: IOException) {
            Interdimensional.LOGGER.error("Unable to save Interdimensional config file")
        }
    }

    companion object {
        private lateinit var path: Path
        fun createConfig(path: Path): Config {
            var json: JsonObject
            try {
                json = JsonParser.parseString(Files.readString(path)).asJsonObject
            } catch (e: IOException) {
                try {
                    Files.copy(
                        Objects.requireNonNull(Interdimensional::class.java.getResourceAsStream("/data/interdimensional/default_config.json")),
                        path
                    )
                } catch (ioException: IOException) {
                    ioException.printStackTrace()
                    Interdimensional.LOGGER.error("Unable to create default config file for Interdimensional")
                }
                json = JsonObject()
                json.addProperty("version", 1)
                json.add("worlds", JsonArray())
                json.add("portals", JsonArray())
            }
            return Config(json, path)
        }
    }

    init {
        val jsonObject = json.asJsonObject
        val worlds = jsonObject["worlds"].asJsonArray
        worlds.forEach(Consumer { e: JsonElement ->
            // Loading logic
            val o = e.asJsonObject
            val identifier =
                Identifier(o["identifier"].asString)
            RuntimeWorldManager.add(RuntimeWorldConfig(), identifier)
        })
        configVersion = jsonObject["version"].asInt
        PortalManager.fromJson(jsonObject["portals"].asJsonArray)
        path = newPath
    }
}