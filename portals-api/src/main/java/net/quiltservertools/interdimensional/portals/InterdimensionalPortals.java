package net.quiltservertools.interdimensional.portals;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.quiltservertools.interdimensional.portals.api.CustomPortalBuilder;
import net.quiltservertools.interdimensional.portals.networking.NetworkManager;
import net.quiltservertools.interdimensional.portals.portal.PortalIgnitionSource;
import net.quiltservertools.interdimensional.portals.portal.PortalPlacer;
import net.quiltservertools.interdimensional.portals.portal.frame.CustomAreaHelper;
import net.quiltservertools.interdimensional.portals.portal.frame.FlatPortalAreaHelper;
import net.quiltservertools.interdimensional.portals.portal.linking.PortalLinkingStorage;
import net.minecraft.block.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;

import java.util.HashMap;

public class InterdimensionalPortals implements ModInitializer {
    public static final String MOD_ID = "interdimensional-portals";
    public static PortalBlock portalBlock;
    public static HashMap<Identifier, RegistryKey<World>> dims = new HashMap<>();
    public static Identifier VANILLA_NETHERPORTAL_FRAMETESTER = new Identifier(MOD_ID, "vanillanether");
    public static Identifier FLATPORTAL_FRAMETESTER = new Identifier(MOD_ID, "flat");
    public static PortalLinkingStorage portalLinkingStorage;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            for (RegistryKey<World> registryKey : server.getWorldRegistryKeys()) {
                dims.put(registryKey.getValue(), registryKey);
            }
            portalLinkingStorage = (PortalLinkingStorage) server.getWorld(World.OVERWORLD).getPersistentStateManager().getOrCreate(PortalLinkingStorage::fromNbt, PortalLinkingStorage::new, MOD_ID);
        });

        CustomPortalApiRegistry.registerPortalFrameTester(VANILLA_NETHERPORTAL_FRAMETESTER, CustomAreaHelper::new);
        CustomPortalApiRegistry.registerPortalFrameTester(FLATPORTAL_FRAMETESTER, FlatPortalAreaHelper::new);


        UseItemCallback.EVENT.register(((player, world, hand) -> {
            ItemStack stack = player.getStackInHand(hand);
            if (!world.isClient) {
                Item item = stack.getItem();
                if (PortalIgnitionSource.isRegisteredIgnitionSourceWith(item)) {
                    HitResult hit = player.raycast(6, 1, false);
                    if (hit.getType() == HitResult.Type.BLOCK) {
                        BlockHitResult blockHit = (BlockHitResult) hit;
                        BlockPos usedBlockPos = blockHit.getBlockPos();
                        if (PortalPlacer.attemptPortalLight(world, usedBlockPos.offset(blockHit.getSide()), usedBlockPos, PortalIgnitionSource.ItemUseSource(item))) {
                            return TypedActionResult.success(stack);
                        }
                    }
                }
            }
            return TypedActionResult.pass(stack);
        }));
        CustomPortalBuilder.beginPortal().frameBlock(Blocks.GLOWSTONE).destDimID(new Identifier("the_end")).lightWithWater().tintColor(46, 5, 25).registerPortal();
    }

    public static void logError(String message) {
        LogManager.getLogger().error(message);
    }


    public static boolean isInstanceOfCustomPortal(BlockView world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() instanceof PortalBlock;
    }

    public static boolean isInstanceOfCustomPortal(BlockState state) {
        return state.getBlock() instanceof PortalBlock;
    }

    public static Block getDefaultPortalBlock() {
        return portalBlock;
    }

    public static Block getPortalBase(BlockView world, BlockPos pos) {
        if (isInstanceOfCustomPortal(world, pos))
            return ((PortalBlock) world.getBlockState(pos).getBlock()).getPortalBase(world, pos);
        else return null;
    }

    public static Block defaultPortalBaseFinder(BlockView world, BlockPos pos) {
        if (InterdimensionalPortals.isInstanceOfCustomPortal(world, pos)) {
            Direction.Axis axis = getAxisFrom(world.getBlockState(pos));

            if (!InterdimensionalPortals.isInstanceOfCustomPortal(world, moveTowardsFrame(pos, axis, false)))
                return world.getBlockState(moveTowardsFrame(pos, axis, false)).getBlock();
            if (!InterdimensionalPortals.isInstanceOfCustomPortal(world, moveTowardsFrame(pos, axis, true)))
                return world.getBlockState(moveTowardsFrame(pos, axis, true)).getBlock();

            if (axis == Direction.Axis.Y) axis = Direction.Axis.Z;

            if (!InterdimensionalPortals.isInstanceOfCustomPortal(world, pos.offset(axis, 1)))
                return world.getBlockState(pos.offset(axis, 1)).getBlock();
            if (!InterdimensionalPortals.isInstanceOfCustomPortal(world, pos.offset(axis, -1)))
                return world.getBlockState(pos.offset(axis, -1)).getBlock();
        }
        if (pos.getY() < 0 || world.getBlockState(pos).isAir()) {
            return null;
        }
        Direction.Axis axis = getAxisFrom(world.getBlockState(pos));
        return InterdimensionalPortals.getPortalBase(world, moveTowardsFrame(pos, axis, false));
    }

    private static BlockPos moveTowardsFrame(BlockPos pos, Direction.Axis portalAxis, boolean positiveMove) {
        if (portalAxis.isHorizontal())
            return pos.offset(positiveMove ? Direction.UP : Direction.DOWN);
        return pos.offset(positiveMove ? Direction.EAST : Direction.WEST);
    }

    public static Direction.Axis getAxisFrom(BlockState state) {
        if (state.getBlock() instanceof NetherPortalBlock)
            return state.get(NetherPortalBlock.AXIS);
        if (state.getBlock() instanceof EndPortalBlock)
            return Direction.Axis.Y;
        return Direction.Axis.X;
    }

    public static BlockState blockWithAxis(BlockState state, Direction.Axis axis) {
        if (state.getBlock() instanceof NetherPortalBlock)
            return state.with(NetherPortalBlock.AXIS, axis);
        return state;
    }

    static {
        portalBlock = new PortalBlock(Block.Settings.of(Material.PORTAL).noCollision().strength(-1).sounds(BlockSoundGroup.GLASS).luminance(state -> 11));
        Registry.register(Registry.BLOCK, new Identifier(InterdimensionalPortals.MOD_ID, "portal_block"), portalBlock);
    }
}