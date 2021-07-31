package com.github.quiltservertools.interdimensional.world

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.kyrptonaught.customportalapi.api.CustomPortalBuilder
import net.kyrptonaught.customportalapi.portal.PortalIgnitionSource
import net.kyrptonaught.customportalapi.util.ColorUtil
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

            val portal = Portal(name, frameBlock, destination, ColorUtil.getColorFromRGB(r, g, b), flat, ignitionSource)
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
            val r: Int = portal.color shr 16 and 0xFF
            val g: Int = portal.color shr 8 and 0xFF
            val b: Int = portal.color and 0xFFC
            jsonObject.addProperty("r", r)
            jsonObject.addProperty("g", g)
            jsonObject.addProperty("b", b)
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

    fun addPortal(portal: Portal) {
        val builder = CustomPortalBuilder.beginPortal()
        builder.frameBlock(portal.frameBlock)
        builder.ignitionSource(portal.source)
        if (portal.horizontal) builder.flatPortal()
        builder.tintColor(portal.color)
        builder.destDimID(portal.destination)
        builder.registerPortal()
        portals.add(portal)
    }
}