# Portal Command

`/dim portal [add | remove]`

## Add

`/dim portal add <name: String> <destination: Dimension> <frame_block: Block> [options: Portal Options Argument Type]`

`name`: The name of the portal link

`destination`: Where the portal should end up

`frame_block`: The block used in the frame of the portal

`options`: Additional options for portal creation. See [Options](#Options)

### Options

#### Syntax

This argument takes a series of properties in the format below:

`property:my_value key:value`

#### Properties

`color`: Formatting - Color of the portal for when a compatible client mod is used

`source_world`: Identifier - the world that the portal is initially lit from. Defaults to overworld

## Remove

`/dim portal remove <name>`

Deletes the portal link with the name specified at the next restart