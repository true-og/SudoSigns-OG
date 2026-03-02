package dev.mylesmor.sudosigns.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import dev.mylesmor.sudosigns.SudoSigns;
import net.trueog.utilitiesog.UtilitiesOG;

public class InvalidEntriesManager {

    /**
     * The class for managing invalid sign entries in the config.
     * 
     * @author MylesMor
     * @author https://mylesmor.dev
     */
    ArrayList<String> invalidEntries = new ArrayList<>();

    ConfigManager configManager;
    FileConfiguration config;

    InvalidEntriesManager(ConfigManager configManager, FileConfiguration config) {

        this.configManager = configManager;
        this.config = config;

    }

    public void setConfig(FileConfiguration config) {

        this.config = config;

    }

    /**
     * Attempts to a fix an invalid entry by placing a sign in the correct position.
     * 
     * @param s The sign's name.
     * @return True if sign successfully fixed, otherwise False.
     */
    private boolean fixEntry(String s) {

        try {

            final ConfigurationSection locSec = config.getConfigurationSection("signs." + s + ".location");
            final String world = locSec.getString("world");
            final double x = locSec.getDouble("x");
            final double y = locSec.getDouble("y");
            final double z = locSec.getDouble("z");
            String matString = config.getString("signs." + s + ".blocktype");
            if (StringUtils.contains(SudoSigns.version, "1.13")) {

                matString = "SIGN";

            }

            final Material material = Material.valueOf(matString);
            final BlockFace blockFace = BlockFace.valueOf(locSec.getString("rotation"));
            final World w = Bukkit.getServer().getWorld(world);
            if (w != null) {

                final Location loc = new Location(Bukkit.getServer().getWorld(world), x, y, z);
                w.getBlockAt(loc).setType(material);
                final Block b = w.getBlockAt(loc);
                if (b.getBlockData() instanceof org.bukkit.block.data.type.Sign) {

                    final org.bukkit.block.data.type.Sign facing = (org.bukkit.block.data.type.Sign) b.getBlockData();
                    facing.setRotation(blockFace);
                    b.setBlockData(facing);

                } else if (b.getBlockData() instanceof org.bukkit.block.data.type.WallSign) {

                    final org.bukkit.block.data.type.WallSign facing = (org.bukkit.block.data.type.WallSign) b
                            .getBlockData();
                    facing.setFacing(blockFace);
                    b.setBlockData(facing);

                }

                final Sign newSign = (Sign) b.getState();
                final List<String> lines = getSignText(s);
                int i = 0;
                for (String line : lines) {

                    newSign.line(i, UtilitiesOG.trueogColorize(line));

                    i++;

                }

                newSign.update();

            }

        } catch (Exception error) {

            error.printStackTrace();
            return false;

        }

        return true;

    }

    public ArrayList<String> getInvalidEntries() {

        return invalidEntries;

    }

    public void setInvalidEntries(ArrayList<String> invalid) {

        invalidEntries = invalid;

    }

    public void addInvalidEntry(String entry) {

        invalidEntries.add(entry);

    }

    /**
     * Retrieves the invalid sign's text from the config.
     * 
     * @param name Name of the sign.
     * @return The sign's message or null if not found.
     */
    public List<String> getSignText(String name) {

        final List<String> signSec = config.getStringList("signs." + name + ".text");
        signSec.forEach(item -> item = UtilitiesOG.trueogColorize(item).content());

        if (signSec.size() != 0) {

            return signSec;

        }

        return null;

    }

    /**
     * Removes a sign's entry in the config.
     * 
     * @param name Name of the sign.
     * @param all  True if all invalid entries should be purged, or False for just
     *             one.
     * @return The sign's message or null if not found.
     */
    public boolean purgeInvalidEntry(String name, boolean all) {

        if (all) {

            invalidEntries.forEach(s -> {

                Bukkit.getLogger().warning(Boolean.toString(config.isConfigurationSection("signs." + s)));
                config.set("signs." + s, null);

            });

            configManager.loadSigns();
            configManager.save();
            return true;

        } else {

            for (String s : invalidEntries) {

                if (StringUtils.equalsIgnoreCase(name, s)) {

                    Bukkit.getLogger().warning(Boolean.toString(config.isConfigurationSection("signs." + s)));
                    config.set("signs." + name, null);
                    configManager.loadSigns();
                    configManager.save();
                    return true;

                }

            }

        }

        return false;

    }

    public boolean fixInvalidEntry(String name, boolean all) {

        if (all) {

            invalidEntries.forEach(this::fixEntry);

            configManager.save();
            return true;

        } else {

            for (String s : invalidEntries) {

                if (name.equals(s) && fixEntry(s)) {

                    configManager.save();
                    return true;

                }

            }

        }

        return false;

    }

}