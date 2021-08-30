# Generator Argument Type

This argument takes a series of properties in the format below:

`property:my_value key:value`

## Properties

`seed` - Long - Seed of generator

### Biome Source

Only one of these options can be specified at a time

`single_biome` - Biome (Identifier) - Sets generator to generate a world of one biome

`vanilla_layered` - Boolean - Sets generator to generate a world of normal vanilla biomes

`the_end_biome_source` - Boolean - Sets generator to generate a world of normal vanilla end biomes

`multi_noise_biome_source` - Boolean - Sets generator to multi noise biome source

`biome_seed` - Long - Sets biome generator to use seed specified

### World Options

`large_biomes` - Boolean - Sets generator to generate a world with larger biomes

`superflat` - Boolean - Sets generator to generate a superflat world

### Structures

`generate_structures` - Boolean - Controls whether to generate structures

`generate_strongholds` - Boolean - Controls whether to generate strongholds

`exclude_structures` - Structure List - Excludes structures included by `generate_structures` when set to true

`include_structures` - Structure List - Includes structures excluded by `generate_structures` when set to false

#### Structure List Format

Structure name, separated by commas

Examples:

`monument,buried_treasure`

`mineshaft`