package pl.drownek.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class LocationUtil {
    private LocationUtil() {
    }

    public static boolean isSimilarExceptRotation(final Location first, final Location second) {
        return first.getX() == second.getX() && first.getY() == second.getY() && first.getZ() == second.getZ();
    }

    public static double distance(final Location first, final Location second) {
        return Math.max(Math.abs(first.getX() - second.getX()), Math.abs(first.getZ() - second.getZ()));
    }

    public static boolean isInSpawn(final Location location) {
        return distance(location, location.getWorld().getSpawnLocation()) <= 70;
    }

    public static boolean isInProtectionArea(final Location location) {
        return distance(location, location.getWorld().getSpawnLocation()) < 175;
    }

    public static boolean isInRadius(final Location entityLocation, final Location center, final int radius) {
        return distance(center, entityLocation) < radius;
    }

    public static List<Player> getPlayersInRadius(final Location location, final int size, final Material material) {
        final List<Player> players = new ArrayList<>();
        for (final Player player : location.getWorld().getPlayers()) {
            if (location.distance(player.getLocation()) <= size) {
                final Block block = player.getLocation().getBlock();
                final Block relative = block.getRelative(BlockFace.DOWN);
                if (!block.getType().equals(material) && !relative.getType().equals(material)) {
                    continue;
                }
                players.add(player);
                if (players.size() == 2) {
                    return players;
                }
            }
        }
        return players;
    }

    public static boolean loc(final int minX, final int maxX, final int minZ, final int maxZ, final Location l) {
        final double[] dim = {minX, maxX};
        Arrays.sort(dim);
        if (l.getX() >= dim[1] || l.getX() <= dim[0]) {
            return false;
        }
        dim[0] = minZ;
        dim[1] = maxZ;
        Arrays.sort(dim);
        return l.getZ() < dim[1] && l.getZ() > dim[0];
    }

    public static Location toCenter(final Location location) {
        final Location centerLoc = location.clone();
        centerLoc.setX(location.getBlockX() + 0.5);
        centerLoc.setY(location.getBlockY() + 0.5);
        centerLoc.setZ(location.getBlockZ() + 0.5);
        return centerLoc;
    }

    public static List<? extends Player> getPlayersWithin(final Location location, final float radius) {
        return Bukkit.getServer().getOnlinePlayers().stream()
            .filter(player -> player.getLocation().distance(location) <= radius)
            .toList();
    }
}
