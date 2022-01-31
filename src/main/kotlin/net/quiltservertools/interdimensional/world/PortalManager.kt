package net.quiltservertools.interdimensional.world

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import me.lucko.fabric.api.permissions.v0.Permissions
import net.quiltservertools.interdimensional.portals.api.CustomPortalBuilder
import net.quiltservertools.interdimensional.portals.portal.PortalIgnitionSource
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.quiltservertools.interdimensional.portals.CustomPortalApiRegistry
import net.quiltservertools.interdimensional.portals.portal.PortalIgnitionSource.ItemUseSource

object PortalManager {
    var portals: MutableList<Portal> = mutableListOf()

    fun fromJson(array: JsonArray) {
        array.forEach { arrayItem ->
            val jsonObject = arrayItem.asJsonObject
            val name = jsonObject.get("name_identifier").asString
            val frameBlock = Registry.BLOCK.get(Identifier(jsonObject.get("frame_block").asString))
            val destination = Identifier(jsonObject.get("destination").asString)
            val r = jsonObject.get("r").asInt
            val g = jsonObject.get("g").asInt
            val b = jsonObject.get("b").asInt
            val flat = jsonObject.get("horizontal").asBoolean
            val sourceWorld = if (!jsonObject.has("source_world")) {
                Identifier("minecraft", "overworld")
            } else {
                Identifier(jsonObject.get("source_world").asString)
            }

            val permission = if (jsonObject.has("permissions")) {
                jsonObject.get("permissions").asInt
            } else {
                0
            }

            // Source parsing
            val sourceObject = jsonObject.get("source").asJsonObject

            val ignitionSource: PortalIgnitionSource = if (sourceObject.has("item")) {
                ItemUseSource(Registry.ITEM.get(Identifier(jsonObject.get("item").asString)))
            } else if (sourceObject.has("fluid")) {
                PortalIgnitionSource.FluidSource(Registry.FLUID.get(Identifier(jsonObject.get("fluid").asString)))
            } else if (sourceObject.has("custom")) {
                PortalIgnitionSource.CustomSource(Identifier(jsonObject.get("custom").asString))
            } else {
                PortalIgnitionSource.FIRE
            }

            val portal = Portal(name, frameBlock, destination, sourceWorld, r, g, b, flat, ignitionSource, permission)
            addPortal(portal)
        }
    }

    fun toJson(): JsonArray {
        val array = JsonArray()
        portals.forEach { portal ->
            val jsonObject = JsonObject()
            jsonObject.addProperty("name_identifier", portal.name)
            jsonObject.addProperty("frame_block", Registry.BLOCK.getId(portal.frameBlock).toString())
            jsonObject.addProperty("destination", portal.destination.toString())
            jsonObject.addProperty("r", portal.r)
            jsonObject.addProperty("g", portal.g)
            jsonObject.addProperty("b", portal.b)
            jsonObject.addProperty("horizontal", portal.horizontal)
            jsonObject.addProperty("source_world", portal.sourceWorld.toString())
            jsonObject.addProperty("permissions", portal.permission)

            val sourceObject = JsonObject()

            when (portal.source.sourceType) {
                PortalIgnitionSource.SourceType.USEITEM -> {
                    sourceObject.addProperty("item", portal.source.ignitionSourceID.toString())
                }
                PortalIgnitionSource.SourceType.FLUID -> {
                    sourceObject.addProperty("fluid", portal.source.ignitionSourceID.toString())
                }
                PortalIgnitionSource.SourceType.CUSTOM -> {
                    sourceObject.addProperty("custom", portal.source.ignitionSourceID.toString())
                }
                else -> {
                    sourceObject.addProperty("fire", true)
                }
            }

            jsonObject.add("source", sourceObject)
            array.add(jsonObject)
        }
        return array
    }

    fun addPortal(portal: Portal) {
        val builder = CustomPortalBuilder.beginPortal()

        builder.frameBlock(portal.frameBlock)
        builder.ignitionSource(portal.source)
        builder.returnDim(portal.sourceWorld, true)
        if (portal.horizontal) builder.flatPortal()
        builder.tintColor(portal.r, portal.g, portal.b)
        builder.destDimID(portal.destination)

        if (portal.permission > 0) {
            builder.setPermission(Permissions.require("interdimensional.visit.${portal.destination}", portal.permission))
        }

        builder.registerPortal()
        portals.add(portal)
    }

    fun removePortal(name: String) {
        val portal = this.portals.first { it.name == name }
        this.portals.remove(portal)
        CustomPortalApiRegistry.removePortal(portal.frameBlock)
    }
}