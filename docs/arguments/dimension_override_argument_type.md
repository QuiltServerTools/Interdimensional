# Dimension Override Argument Type

A series of key/value pairs seperated by colons.

### Syntax example

`key:value second_key:second_value`

## Properties

`seed` - Long - World seed of the dimension. Generator specified seeds override this.

`type` - Dimension - Customises the appearance of the world (fog, sky colour, etc), but does not change any world generation

`generator` - Dimension - Sets the generator of the world to the same as the dimension specified

`custom_generator` - Boolean - Controls whether to use the generator specified with the [generator command](../commands/generator.md). Is overridden by `generator`

`difficulty` - (peaceful | easy | normal | hard) - Sets the difficulty in a world