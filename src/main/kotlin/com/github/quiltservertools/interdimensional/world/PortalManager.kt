package com.github.quiltservertools.interdimensional.world

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.kyrptonaught.customportalapi.api.CustomPortalBuilder
import net.kyrptonaught.customportalapi.portal.PortalIgnitionSource
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

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

            // Source parsing
            val sourceObject = jsonObject.get("source").asJsonObject

            val ignitionSource: PortalIgnitionSource = if (sourceObject.has("item")) {
                PortalIgnitionSource.ItemUseSource(Registry.ITEM.get(Identifier(jsonObject.get("item").asString)))
            } else if (sourceObject.has("fluid")) {
                PortalIgnitionSource.FluidSource(Registry.FLUID.get(Identifier(jsonObject.get("fluid").asString)))
            } else if (sourceObject.has("custom")) {
                PortalIgnitionSource.CustomSource(Identifier(jsonObject.get("custom").asString))
            } else {
                PortalIgnitionSource.FIRE
            }

            val portal = Portal(name, frameBlock, destination, r, g, b, flat, ignitionSource)
            portals.add(portal)

            val builder = CustomPortalBuilder.beginPortal()
            builder.frameBlock(frameBlock)
            builder.ignitionSource(ignitionSource)
            if (flat) builder.flatPortal()
            builder.tintColor(r, g, b)
            builder.destDimID(destination)
            builder.registerPortal()
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
}