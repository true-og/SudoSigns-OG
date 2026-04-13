package dev.mylesmor.sudosigns.listeners;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import dev.mylesmor.sudosigns.SudoSigns;
import dev.mylesmor.sudosigns.data.SudoSign;
import dev.mylesmor.sudosigns.data.SudoUser;
import dev.mylesmor.sudosigns.menus.SignEditor;
import dev.mylesmor.sudosigns.util.Permissions;
import dev.mylesmor.sudosigns.util.Util;
import net.trueog.diamondbankog.api.DiamondBankAPIJava;
import net.trueog.utilitiesog.UtilitiesOG;

public class SignListener implements Listener {

    private final DiamondBankAPIJava diamondBankAPI;

    public SignListener(DiamondBankAPIJava diamondBankAPI) {

        this.diamondBankAPI = diamondBankAPI;

    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent playerInteractEvent) {

        final Player p = playerInteractEvent.getPlayer();
        if (playerInteractEvent.getClickedBlock() == null) {

            return;

        }

        final SudoUser user = SudoSigns.users.get(p.getUniqueId());
        if (user != null) {

            final Block b = playerInteractEvent.getClickedBlock();
            if (user.isCreate()) {

                playerInteractEvent.setCancelled(true);
                create(p, user, b);

            } else if (user.isSelectToCopy()) {

                playerInteractEvent.setCancelled(true);
                selectToCopy(p, user, b);

            } else if (user.isCopy()) {

                playerInteractEvent.setCancelled(true);
                copy(p, user, b);

            } else if (user.isEdit()) {

                playerInteractEvent.setCancelled(true);
                edit(p, user, b);

            } else if (user.isDelete()) {

                playerInteractEvent.setCancelled(true);
                delete(p, user, b);

            } else if (user.isRun()) {

                playerInteractEvent.setCancelled(true);
                runRemote(p, user, b);

            } else if (user.isView()) {

                playerInteractEvent.setCancelled(true);
                view(p, user, b);

            } else {

                runSign(p, playerInteractEvent);

            }

        } else {

            runSign(p, playerInteractEvent);

        }

    }

    private void selectToCopy(Player p, SudoUser user, Block b) {

        final Sign sign = Util.getSign(b);
        if (sign != null) {

            final String name = Util.findSign(sign);
            if (name != null) {

                user.setSelectToCopy(false);
                p.performCommand("ss copy " + name + " " + user.getPassThru());

            } else {

                UtilitiesOG.trueogMessage(p, "&cERROR: Failed to copy: that is not a SudoSign!");

            }

        } else {

            UtilitiesOG.trueogMessage(p, "&cERROR: A sign wasn't clicked! &6Cancelling...");

        }

        user.setSelectToCopy(false);

    }

    private void runSign(Player p, PlayerInteractEvent e) {

        final boolean condition = Util.isSignBlock(Objects.requireNonNull(e.getClickedBlock()))
                && (p.hasPermission(Permissions.SELECT) && e.getAction() == Action.RIGHT_CLICK_BLOCK
                        || !p.hasPermission(Permissions.SELECT));
        if (!condition) {

            return;

        }

        final Sign sign = Util.getSign(e.getClickedBlock());
        if (sign == null) {

            return;

        }

        final String name = Util.findSign(sign);
        if (name != null) {

            SudoSigns.signs.get(name).executeCommands(p);

        }

    }

    private void create(Player p, SudoUser user, Block b) {

        final Sign sign = Util.getSign(b);
        if (sign != null) {

            final String name = Util.findSign(sign);
            if (name != null) {

                user.setCreate(false);
                SudoSigns.signs.remove(user.getPassThru());
                UtilitiesOG.trueogMessage(p, "&cERROR: That SudoSign already exists! &6Cancelling...");
                return;

            }

            SudoSigns.signs.get(user.getPassThru()).setSign(sign);
            SudoSigns.signs.get(user.getPassThru()).addLines();
            if (p.hasPermission(Permissions.EDIT)) {

                final SignEditor editor = new SignEditor(p, SudoSigns.signs.get(user.getPassThru()), user,
                        diamondBankAPI);
                user.setEditor(editor);

            }

            SudoSigns.config.saveSign(SudoSigns.signs.get(user.getPassThru()), true, p);

        } else {

            UtilitiesOG.trueogMessage(p, "&cERROR: A sign was not clicked! Cancelling...");
            user.setCreate(false);
            SudoSigns.signs.remove(user.getPassThru());

        }

        user.setCreate(false);

    }

    private void copy(Player p, SudoUser user, Block b) {

        final Sign newSign = Util.getSign(b);
        if (newSign != null) {

            final String name = Util.findSign(newSign);
            if (name != null) {

                user.setCopy(false);
                SudoSigns.signs.remove(user.getPassThru());
                UtilitiesOG.trueogMessage(p, "&cERROR: That sign is already a SudoSign! Cancelling...");
                return;

            }

            SudoSigns.signs.get(user.getPassThru()).setSign(newSign);
            SudoSigns.signs.get(user.getPassThru()).addLines();
            SudoSigns.config.saveSign(SudoSigns.signs.get(user.getPassThru()), true, p);
            UtilitiesOG.trueogMessage(p, "&aSign has been copied to sign &e" + user.getPassThru() + " &asuccessfully!");

        } else {

            UtilitiesOG.trueogMessage(p, "&cERROR: A sign was not clicked! &6Cancelling...");
            SudoSigns.signs.remove(user.getPassThru());

        }

        user.setCopy(false);

    }

    private void runRemote(Player p, SudoUser user, Block b) {

        final Sign sign = Util.getSign(b);
        if (sign != null) {

            final String name = Util.findSign(sign);
            if (name != null) {

                p.performCommand("ss run " + name);

            } else {

                UtilitiesOG.trueogMessage(p, "&cERROR: That is not a SudoSign! &6Use: &d/ss create <name> &6instead.");

            }

        } else {

            UtilitiesOG.trueogMessage(p, "&cERROR: A sign was not clicked! Cancelling...");

        }

        user.setRun(false);

    }

    private void edit(Player p, SudoUser user, Block b) {

        final Sign sign = Util.getSign(b);
        if (sign != null) {

            final String name = Util.findSign(sign);
            if (name != null) {

                p.performCommand("ss edit " + name);

            } else {

                UtilitiesOG.trueogMessage(p, "&cERROR: That is not a SudoSign! &6Use: &d/ss create <name> &6instead.");

            }

        } else {

            UtilitiesOG.trueogMessage(p, "&cERROR: A sign was not clicked! Cancelling...");

        }

        user.setEdit(false);

    }

    private void delete(Player p, SudoUser user, Block b) {

        final Sign sign = Util.getSign(b);
        if (sign != null) {

            final String name = Util.findSign(sign);
            if (name != null) {

                p.performCommand("ss delete " + name);

            } else {

                UtilitiesOG.trueogMessage(p, "&cERROR: That is not a SudoSign!");

            }

        } else {

            UtilitiesOG.trueogMessage(p, "&cERROR: A sign was not clicked! &6Cancelling...");

        }

        user.setDelete(false);

    }

    private void view(Player p, SudoUser user, Block b) {

        final Sign sign = Util.getSign(b);
        if (sign != null) {

            final String name = Util.findSign(sign);
            if (name != null) {

                p.performCommand("ss view " + name);

            } else {

                UtilitiesOG.trueogMessage(p, "&cERROR: That is not a SudoSign! &6Use: &d/ss create <name> &6instead.");

            }

        } else {

            UtilitiesOG.trueogMessage(p, "&cERROR: A sign was not clicked! &6Cancelling...");

        }

        user.setView(false);

    }

    @EventHandler
    public void onDestroy(BlockBreakEvent blockBreakEvent) {

        final Player p = blockBreakEvent.getPlayer();
        final Sign sign = Util.getSign(blockBreakEvent.getBlock());
        if (sign != null) {

            SudoSigns.signs.entrySet().stream()
                    .filter(entry -> Util.sameBlockLocation(entry.getValue(), sign.getLocation())).forEach(entry ->
                    {

                        blockBreakEvent.setCancelled(true);
                        if (p.hasPermission(Permissions.SELECT)) {

                            Util.sendSelectMenus(p, entry.getKey());

                        } else {

                            Util.sudoSignsPermissionsError(p);

                        }

                    });

        } else {

            final BlockFace[] faces = { BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH,
                    BlockFace.WEST };
            for (BlockFace face : faces) {

                final Block br = blockBreakEvent.getBlock().getRelative(face);
                final BlockState bs = br.getState();
                if (Util.isSignState(bs)) {

                    final Sign nearbySign = (Sign) bs;
                    for (Map.Entry<String, SudoSign> entry : SudoSigns.signs.entrySet()) {

                        if (Util.sameBlockLocation(entry.getValue(), nearbySign.getLocation())) {

                            if (br.getBlockData() instanceof Sign) {

                                if (blockBreakEvent.getBlock()
                                        .equals(nearbySign.getBlock().getRelative(BlockFace.DOWN)))
                                {

                                    blockBreakEvent.setCancelled(true);
                                    UtilitiesOG.trueogMessage(p,
                                            "&cERROR: You cannot destroy a block that is attached to a SudoSign!");
                                    return;

                                }

                            } else if (br.getBlockData() instanceof WallSign) {

                                final WallSign signData = (WallSign) nearbySign.getBlock().getState().getBlockData();
                                final BlockFace attached = signData.getFacing().getOppositeFace();
                                final Block blockAttached = nearbySign.getBlock().getRelative(attached);
                                if (blockBreakEvent.getBlock().equals(blockAttached)) {

                                    blockBreakEvent.setCancelled(true);
                                    UtilitiesOG.trueogMessage(p,
                                            "&cERROR: You cannot destroy a block that is attached to a SudoSign!");
                                    return;

                                }

                            }

                        }

                    }

                }

            }

        }

    }

    // The following events all attempts to prevent a SudoSign from being broken.
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent entityExplodeEvent) {

        final ArrayList<Block> blocksToRemove = new ArrayList<>();
        entityExplodeEvent.blockList().forEach(block -> {

            final Sign sign = Util.getSign(block);
            if (sign != null) {

                SudoSigns.signs.entrySet().stream()
                        .filter(entry -> Util.sameBlockLocation(entry.getValue(), sign.getLocation()))
                        .forEach(entry -> blocksToRemove.add(block));

            }

            final BlockFace[] faces = { BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH,
                    BlockFace.WEST };
            for (BlockFace face : faces) {

                final BlockState b = block.getRelative(face).getState();
                if (Util.isSignState(b)) {

                    final Sign nearbySign = (Sign) b;
                    SudoSigns.signs.entrySet().stream()
                            .filter(entry -> Util.sameBlockLocation(entry.getValue(), nearbySign.getLocation()))
                            .forEach(entry -> blocksToRemove.add(block));

                }

            }

        });

        blocksToRemove.forEach(b -> entityExplodeEvent.blockList().remove(b));

    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent blockBurnEvent) {

        final BlockFace[] faces = { BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
        for (BlockFace face : faces) {

            final BlockState b = blockBurnEvent.getBlock().getRelative(face).getState();
            if (Util.isSignState(b)) {

                final Sign sign = (Sign) b;
                SudoSigns.signs.entrySet().stream()
                        .filter(entry -> Util.sameBlockLocation(entry.getValue(), sign.getLocation()))
                        .forEach(entry -> blockBurnEvent.setCancelled(true));

            }

        }

    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent blockFadeEvent) {

        final BlockFace[] faces = { BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
        for (BlockFace face : faces) {

            final BlockState b = blockFadeEvent.getBlock().getRelative(face).getState();
            if (Util.isSignState(b)) {

                final Sign sign = (Sign) b;
                SudoSigns.signs.entrySet().stream()
                        .filter(entry -> Util.sameBlockLocation(entry.getValue(), sign.getLocation()))
                        .forEach(entry -> blockFadeEvent.setCancelled(true));

            }

        }

    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent leavesDecayEvent) {

        final BlockFace[] faces = { BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
        for (BlockFace face : faces) {

            final BlockState b = leavesDecayEvent.getBlock().getRelative(face).getState();
            if (Util.isSignState(b)) {

                final Sign sign = (Sign) b;
                SudoSigns.signs.entrySet().stream()
                        .filter(entry -> Util.sameBlockLocation(entry.getValue(), sign.getLocation()))
                        .forEach(entry -> leavesDecayEvent.setCancelled(true));

            }

        }

    }

}
