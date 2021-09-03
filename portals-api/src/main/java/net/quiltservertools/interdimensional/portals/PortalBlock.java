package net.quiltservertools.interdimensional.portals;

import eu.pb4.polymer.block.VirtualBlock;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.quiltservertools.interdimensional.portals.interfaces.EntityInCustomPortal;
import net.quiltservertools.interdimensional.portals.portal.frame.PortalFrameTester;
import net.quiltservertools.interdimensional.portals.util.CustomTeleporter;
import net.quiltservertools.interdimensional.portals.util.PortalLink;

public class PortalBlock extends Block implements VirtualBlock {
    @Override
    public Block getVirtualBlock() {
        return Blocks.NETHER_PORTAL;
    }

    @Override
    public BlockState getVirtualBlockState(BlockState state) {
        return Blocks.NETHER_PORTAL.getDefaultState().with(NetherPortalBlock.AXIS, state.get(PortalBlock.AXIS));
    }

    @Override
    public void sendPacketsAfterCreation(ServerPlayerEntity player, BlockPos pos, BlockState blockState) {
        VirtualBlock.super.sendPacketsAfterCreation(player, pos, blockState);
    }

    public static final EnumProperty<Direction.Axis> AXIS = Properties.AXIS;
    protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
    protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);
    protected static final VoxelShape Y_SHAPE = Block.createCuboidShape(0.0D, 6.0D, 0.0D, 16.0D, 10.0D, 16.0D);

    public PortalBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(AXIS, Direction.Axis.X));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(AXIS)) {
            case Z -> Z_SHAPE;
            case Y -> Y_SHAPE;
            default -> X_SHAPE;
        };
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        Block block = getPortalBase(world, pos);
        PortalLink link = CustomPortalApiRegistry.getPortalLinkFromBase(block);
        if (link != null) {
            PortalFrameTester portalFrameTester = link.getFrameTester().createInstanceOfPortalFrameTester().init(world, pos, InterdimensionalPortals.getAxisFrom(state), block);
            if (portalFrameTester.wasAlreadyValid())
                return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
        }
        //todo handle unknown portallink

        return Blocks.AIR.getDefaultState();
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        EntityInCustomPortal entityInPortal = (EntityInCustomPortal) entity;
        entityInPortal.increaseCooldown();
        if (!entityInPortal.didTeleport()) {
            entityInPortal.setInPortal(true);
            if (entityInPortal.getTimeInPortal() >= entity.getMaxNetherPortalTime()) {
                entityInPortal.setDidTP(true);
                if (!world.isClient)
                    CustomTeleporter.TPToDim(world, entity, getPortalBase(world, pos), pos);
            }
        }
    }

    public Block getPortalBase(BlockView world, BlockPos pos) {
        return InterdimensionalPortals.defaultPortalBaseFinder(world, pos);
    }
}
