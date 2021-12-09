package net.quiltservertools.interdimensional.portals;

import eu.pb4.polymer.api.block.PlayerAwarePolymerBlock;
import eu.pb4.polymer.api.block.PolymerBlock;
import eu.pb4.polymer.api.client.PolymerKeepModel;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.quiltservertools.interdimensional.portals.client.ClientManager;
import net.quiltservertools.interdimensional.portals.client.InterdimensionalPortalsClient;
import net.quiltservertools.interdimensional.portals.interfaces.ClientPlayerInColoredPortal;
import net.quiltservertools.interdimensional.portals.interfaces.EntityInCustomPortal;
import net.quiltservertools.interdimensional.portals.networking.NetworkManager;
import net.quiltservertools.interdimensional.portals.portal.frame.PortalFrameTester;
import net.quiltservertools.interdimensional.portals.util.CustomTeleporter;
import net.quiltservertools.interdimensional.portals.util.PortalLink;

import java.util.Random;

@SuppressWarnings("deprecation")
public class PortalBlock extends Block implements PlayerAwarePolymerBlock, PolymerKeepModel {

    @Override
    public Block getPolymerBlock(BlockState blockState) {
        return Blocks.NETHER_PORTAL;
    }

    @Override
    public Block getPolymerBlock(ServerPlayerEntity player, BlockState state) {
        return NetworkManager.isVanilla(player)
                ? getPolymerBlock(state)
                : this;
    }

    public BlockState getPolymerBlockState(BlockState state) {
        return Blocks.NETHER_PORTAL.getDefaultState().with(NetherPortalBlock.AXIS, state.get(PortalBlock.AXIS));
    }
    
    @Override
    public BlockState getPolymerBlockState(ServerPlayerEntity player, BlockState state) {
        return NetworkManager.isVanilla(player)
                ? getPolymerBlockState(state)
                : state;
    }

    @Override
    public void onPolymerBlockSend(ServerPlayerEntity player, BlockPos.Mutable pos, BlockState blockState) {
        var portal = CustomPortalApiRegistry.getPortalLinkFromBase(InterdimensionalPortals.getPortalBase(player.world, pos));
        if (portal != null) {
            if (portal.colorID != 0) {
                NetworkManager.sendPortalInfo(player, pos, blockState.get(AXIS), portal.colorID);
            }
        }
    }

    public static final EnumProperty<Direction.Axis> AXIS = Properties.HORIZONTAL_AXIS;
    protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
    protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);

    public PortalBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(AXIS, Direction.Axis.X));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(AXIS).equals(Direction.Axis.X)) {
            return X_SHAPE;
        }
        return Z_SHAPE;
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        Block block = getPortalBase(world, pos);
        PortalLink link = CustomPortalApiRegistry.getPortalLinkFromBase(block);
        if (link != null) {
            PortalFrameTester portalFrameTester = link.getFrameTester().createInstanceOfPortalFrameTester().init(world, pos, InterdimensionalPortals.getAxisFrom(state), block);
            if (portalFrameTester.wasAlreadyValid())
                return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
        }

        return Blocks.AIR.getDefaultState();
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient()) {
            if (!entity.hasVehicle() && !entity.hasPassengers() && entity.canUsePortals()) {
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
        } else {
            if (!ClientManager.getInstance().contains(pos)) {
                ((ClientPlayerInColoredPortal) MinecraftClient.getInstance().player).setLastUsedPortalColor(ClientManager.getInstance().getColorAtPosition(pos));
            }
        }
    }

    public Block getPortalBase(BlockView world, BlockPos pos) {
        return InterdimensionalPortals.defaultPortalBaseFinder(world, pos);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (random.nextInt(100) == 0) {
            world.playSound((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.BLOCKS, 0.5F, random.nextFloat() * 0.4F + 0.8F, false);
        }

        for(int i = 0; i < 4; ++i) {
            double d = (double)pos.getX() + random.nextDouble();
            double e = (double)pos.getY() + random.nextDouble();
            double f = (double)pos.getZ() + random.nextDouble();
            double g = ((double)random.nextFloat() - 0.5D) * 0.5D;
            double h = ((double)random.nextFloat() - 0.5D) * 0.5D;
            double j = ((double)random.nextFloat() - 0.5D) * 0.5D;
            int k = random.nextInt(2) * 2 - 1;
            if (!world.getBlockState(pos.west()).isOf(this) && !world.getBlockState(pos.east()).isOf(this)) {
                d = (double)pos.getX() + 0.5D + 0.25D * (double)k;
                g = random.nextFloat() * 2.0F * (float)k;
            } else {
                f = (double)pos.getZ() + 0.5D + 0.25D * (double)k;
                j = random.nextFloat() * 2.0F * (float)k;
            }

            world.addParticle(new BlockStateParticleEffect(InterdimensionalPortalsClient.CUSTOMPORTALPARTICLE, state), d, e, f, g, h, j);
        }
    }
}
