package net.quiltservertools.interdimensional.world

import net.quiltservertools.interdimensional.Interdimensional
import com.google.gson.JsonObject
import net.minecraft.server.MinecraftServer
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import xyz.nucleoid.fantasy.RuntimeWorldConfig
import xyz.nucleoid.fantasy.RuntimeWorldHandle
import java.util.function.Consumer

object RuntimeWorldManager {
    private val runtimeDimensionHandlers: MutableList<RuntimeWorldHandle> = ArrayList()
    fun add(config: RuntimeWorldConfig, identifier: Identifier) {
        val handle = Interdimensional.FANTASY.getOrOpenPersistentWorld(identifier, config)
        runtimeDimensionHandlers.add(handle)
    }

    fun remove(handle: RuntimeWorldHandle) {
        runtimeDimensionHandlers.remove(handle)
        Interdimensional.LOGGER.info("Removed dimension${handle.asWorld().registryKey.value}")
        handle.delete()
    }

    fun closeAll(): List<JsonObject> {
        val list = ArrayList<JsonObject>()
        runtimeDimensionHandlers.forEach(Consumer { handle: RuntimeWorldHandle ->
            val `object` = JsonObject()
            `object`.addProperty("identifier", handle.asWorld().registryKey.value.toString())
            list.add(`object`)
        })
        return list
    }

    operator fun get(identifier: Identifier, server: MinecraftServer): ServerWorld {
        val result = runtimeDimensionHandlers.stream().filter { h: RuntimeWorldHandle ->
            h.asWorld().registryKey.value == identifier
        }.findFirst()
        return if (result.isPresent) {
            result.get().asWorld()
        } else {
            server.overworld
        }
    }

    fun getHandle(identifier: Identifier): RuntimeWorldHandle {
        val result = runtimeDimensionHandlers.first { h: RuntimeWorldHandle ->
            h.asWorld().registryKey.value == identifier
        }
        return result
    }
}