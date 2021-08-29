package net.quiltservertools.interdimensional.portals.portal;

import net.quiltservertools.interdimensional.portals.CustomPortalApiRegistry;
import net.quiltservertools.interdimensional.portals.InterdimensionalPortals;
import net.quiltservertools.interdimensional.portals.portal.frame.PortalFrameTester;
import net.quiltservertools.interdimensional.portals.util.PortalLink;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockLocating.Rectangle;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;

import java.util.Iterator;
import java.util.Optional;

public class PortalPlacer {
    public static boolean attemptPortalLight(World world, BlockPos portalPos, BlockPos framePos, PortalIgnitionSource ignitionSource) {
        Block foundationBlock = world.getBlockState(framePos).getBlock();
        PortalLink link = CustomPortalApiRegistry.getPortalLinkFromBase(foundationBlock);

        if (link == null || !link.doesIgnitionMatch(ignitionSource) || !link.canLightInDim(world.getRegistryKey().getValue()))
            return false;
        return createPortal(link, world, portalPos, foundationBlock);
    }

    private static boolean createPortal(PortalLink link, World world, BlockPos pos, Block foundationBlock) {
        Optional<PortalFrameTester> optional = link.getFrameTester().createInstanceOfPortalFrameTester().getNewPortal(world, pos, Direction.Axis.X, foundationBlock);
        //is valid frame, and is correct size(if applicable)
        if (optional.isPresent()) {
            if (optional.get().isRequestedSize(link.forcedWidth, link.forcedHeight))
                optional.get().createPortal(foundationBlock);
            return true;
        }
        return false;
    }

    public static Optional<Rectangle> createDestinationPortal(World world, BlockPos blockPos, BlockState frameBlock, Direction.Axis axis) {
        Direction direction = axis == Direction.Axis.X ? Direction.WEST : Direction.SOUTH;
        double d = -1.0D;
        BlockPos blockPos2 = null;
        double e = -1.0D;
        BlockPos blockPos3 = null;
        WorldBorder worldBorder = world.getWorldBorder();
        int i = world.getTopY() - 1;
        BlockPos.Mutable mutable = blockPos.mutableCopy();
        Iterator var13 = BlockPos.iterateInSquare(blockPos, 16, Direction.EAST, Direction.SOUTH).iterator();

        while (true) {
            BlockPos.Mutable mutable2;
            int p;
            do {
                do {
                    if (!var13.hasNext()) {
                        if (d == -1.0D && e != -1.0D) {
                            blockPos2 = blockPos3;
                            d = e;
                        }

                        int o;
                        if (d == -1.0D) {
                            blockPos2 = (new BlockPos(blockPos.getX(), MathHelper.clamp(blockPos.getY(), 70, world.getTopY() - 10), blockPos.getZ())).toImmutable();
                            Direction direction2 = direction.rotateYClockwise();
                            if (!worldBorder.contains(blockPos2)) {
                                return Optional.empty();
                            }

                            for (o = -1; o < 2; ++o) {
                                for (p = 0; p < 2; ++p) {
                                    for (int q = -1; q < 3; ++q) {
                                        BlockState blockState = q < 0 ? frameBlock : Blocks.AIR.getDefaultState();
                                        mutable.set(blockPos2, p * direction.getOffsetX() + o * direction2.getOffsetX(), q, p * direction.getOffsetZ() + o * direction2.getOffsetZ());
                                        world.setBlockState(mutable, blockState);
                                    }
                                }
                            }
                        }

                        for (int r = -1; r < 3; ++r) {
                            for (o = -1; o < 4; ++o) {
                                if (r == -1 || r == 2 || o == -1 || o == 3) {
                                    mutable.set(blockPos2, r * direction.getOffsetX(), o, r * direction.getOffsetZ());
                                    world.setBlockState(mutable, frameBlock, 3);
                                }
                            }
                        }
                        PortalLink link = CustomPortalApiRegistry.getPortalLinkFromBase(frameBlock.getBlock());
                        BlockState blockState2 = InterdimensionalPortals.blockWithAxis(link != null ? link.getPortalBlock().getDefaultState() : InterdimensionalPortals.getDefaultPortalBlock().getDefaultState(), axis);

                        for (o = 0; o < 2; ++o) {
                            for (p = 0; p < 3; ++p) {
                                mutable.set(blockPos2, o * direction.getOffsetX(), p, o * direction.getOffsetZ());
                                world.setBlockState(mutable, blockState2, 18);
                            }
                        }

                        return Optional.of(new Rectangle(blockPos2.toImmutable(), 2, 3));
                    }

                    mutable2 = (BlockPos.Mutable) var13.next();
                    p = Math.min(i, world.getTopY(Heightmap.Type.MOTION_BLOCKING, mutable2.getX(), mutable2.getZ()));
                } while (!worldBorder.contains(mutable2));
            } while (!worldBorder.contains(mutable2.move(direction, 1)));

            mutable2.move(direction.getOpposite(), 1);

            for (int l = p; l >= 0; --l) {
                mutable2.setY(l);
                if (world.isAir(mutable2)) {
                    int m;
                    for (m = l; l > 0 && world.isAir(mutable2.move(Direction.DOWN)); --l) {
                    }
                    if (l + 4 <= i) {
                        int n = m - l;
                        if (n <= 0 || n >= 3) {
                            mutable2.setY(l);
                            if (canHostFrame(world, mutable2, mutable, direction, 0)) {
                                double f = blockPos.getSquaredDistance(mutable2);
                                if (canHostFrame(world, mutable2, mutable, direction, -1) && canHostFrame(world, mutable2, mutable, direction, 1) && (d == -1.0D || d > f)) {
                                    d = f;
                                    blockPos2 = mutable2.toImmutable();
                                }

                                if (d == -1.0D && (e == -1.0D || e > f)) {
                                    e = f;
                                    blockPos3 = mutable2.toImmutable();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean canHostFrame(World world, BlockPos blockPos, BlockPos.Mutable mutable, Direction direction, int i) {
        Direction direction2 = direction.rotateYClockwise();
        for (int j = -1; j < 3; ++j) {
            for (int k = -1; k < 4; ++k) {
                mutable.set(blockPos, direction.getOffsetX() * j + direction2.getOffsetX() * i, k, direction.getOffsetZ() * j + direction2.getOffsetZ() * i);
                if (k < 0 && !world.getBlockState(mutable).getMaterial().isSolid()) {
                    return false;
                }

                if (k >= 0 && !world.isAir(mutable)) {
                    return false;
                }
            }
        }
        return true;
    }
}
