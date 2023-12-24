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


public class SignListener implements Listener {

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (e.getClickedBlock() != null) {
			SudoUser user = SudoSigns.users.get(p.getUniqueId());
			if (user != null) {
				Block b = e.getClickedBlock();
				if (user.isCreate()) {
					e.setCancelled(true);
					create(p, user, b);
				} else if (user.isSelectToCopy()) {
					e.setCancelled(true);
					selectToCopy(p, user, b);
				} else if (user.isCopy()) {
					e.setCancelled(true);
					copy(p, user, b);
				} else if (user.isEdit()) {
					e.setCancelled(true);
					edit(p, user, b);
				} else if (user.isDelete()) {
					e.setCancelled(true);
					delete(p, user, b);
				} else if (user.isRun()) {
					e.setCancelled(true);
					runRemote(p, user, b);
				} else if (user.isView()) {
					e.setCancelled(true);
					view(p, user, b);
				} else {
					runSign(p, e);
				}
			} else {
				runSign(p, e);
			}
		}
	}

	private void selectToCopy(Player p, SudoUser user, Block b) {
		if (b.getState() instanceof Sign || b.getState() instanceof WallSign) {
			Sign sign = (Sign) b.getState();
			String name = Util.findSign(sign);
			if (name != null) {
				user.setSelectToCopy(false);
				p.performCommand("ss copy " + name + " " + user.getPassThru());
			} else {
				Util.sudoSignsMessage(p, "&cERROR: Failed to copy: that is not a SudoSign!");
			}
		} else {
			Util.sudoSignsMessage(p, "&cERROR: A sign wasn't clicked! &6Cancelling...");
		}
		user.setSelectToCopy(false);
	}

	private void runSign(Player p, PlayerInteractEvent e) {
		if (Objects.requireNonNull(e.getClickedBlock()).getState() instanceof Sign || e.getClickedBlock().getState() instanceof WallSign) {
			if (p.hasPermission(Permissions.SELECT) && e.getAction() == Action.RIGHT_CLICK_BLOCK || ! p.hasPermission(Permissions.SELECT)) {
				Sign sign = (Sign) e.getClickedBlock().getState();
				String name = Util.findSign(sign);
				if (name != null) {
					SudoSigns.signs.get(name).executeCommands(p);
				}
			}
		}
	}

	private void create(Player p, SudoUser user, Block b) {
		if (b.getState() instanceof Sign || b.getState() instanceof WallSign) {
			Sign sign = (Sign) b.getState();
			String name = Util.findSign(sign);
			if (name != null) {
				user.setCreate(false);
				SudoSigns.signs.remove(user.getPassThru());
				Util.sudoSignsMessage(p, "&cERROR: That SudoSign already exists! &6Cancelling...");
				return;
			}
			SudoSigns.signs.get(user.getPassThru()).setSign(sign);
			SudoSigns.signs.get(user.getPassThru()).addLines();
			if (p.hasPermission(Permissions.EDIT)) {
				SignEditor editor = new SignEditor(p, SudoSigns.signs.get(user.getPassThru()), user);
				user.setEditor(editor);
			}
			SudoSigns.config.saveSign(SudoSigns.signs.get(user.getPassThru()), true, p);
		}
		else {
			Util.sudoSignsMessage(p, "&cERROR: A sign was not clicked! Cancelling...");
			user.setCreate(false);
			SudoSigns.signs.remove(user.getPassThru());
		}
		user.setCreate(false);
	}

	private void copy(Player p, SudoUser user, Block b) {
		if (b.getState() instanceof Sign || b.getState() instanceof WallSign) {
			Sign newSign = (Sign) b.getState();
			String name = Util.findSign(newSign);
			if (name != null) {
				user.setCopy(false);
				SudoSigns.signs.remove(user.getPassThru());
				Util.sudoSignsMessage(p, "&cERROR: That sign is already a SudoSign! Cancelling...");
				return;
			}
			SudoSigns.signs.get(user.getPassThru()).setSign(newSign);
			SudoSigns.signs.get(user.getPassThru()).addLines();
			SudoSigns.config.saveSign(SudoSigns.signs.get(user.getPassThru()), true, p);
			Util.sudoSignsMessage(p, "&aSign has been copied to sign &e%NAME% &asuccessfully!", user.getPassThru());
		} else {
			Util.sudoSignsMessage(p, "&cERROR: A sign was not clicked! &6Cancelling...");
			SudoSigns.signs.remove(user.getPassThru());
		}
		user.setCopy(false);
	}

	private void runRemote(Player p, SudoUser user, Block b) {
		if (b.getState() instanceof Sign || b.getState() instanceof WallSign) {
			Sign sign = (Sign) b.getState();
			String name = Util.findSign(sign);
			if (name != null) {
				p.performCommand("ss run " +name);

			} else {
				Util.sudoSignsMessage(p, "&cERROR: That is not a SudoSign! &6Use: &d/ss create <name> &6instead.");
			}
		} else {
			Util.sudoSignsMessage(p, "&cERROR: A sign was not clicked! Cancelling...");
		}
		user.setRun(false);
	}

	private void edit(Player p, SudoUser user, Block b) {
		if (b.getState() instanceof Sign || b.getState() instanceof WallSign) {
			Sign sign = (Sign) b.getState();
			String name = Util.findSign(sign);
			if (name != null) {
				p.performCommand("ss edit " + name);
			} else {
				Util.sudoSignsMessage(p, "&cERROR: That is not a SudoSign! &6Use: &d/ss create <name> &6instead.");
			}
		} else {
			Util.sudoSignsMessage(p, "&cERROR: A sign was not clicked! Cancelling...");
		}
		user.setEdit(false);
	}

	private void delete(Player p, SudoUser user, Block b) {
		if (b.getState() instanceof Sign || b.getState() instanceof WallSign) {
			Sign sign = (Sign) b.getState();
			String name = Util.findSign(sign);
			if (name != null) {
				p.performCommand("ss delete " + name);
			} else {
				Util.sudoSignsMessage(p, "&cERROR: That is not a SudoSign!");
			}
		} else {
			Util.sudoSignsMessage(p, "&cERROR: A sign was not clicked! &6Cancelling...");
		}
		user.setDelete(false);
	}

	private void view(Player p, SudoUser user, Block b) {
		if (b.getState() instanceof Sign || b.getState() instanceof WallSign) {
			Sign sign = (Sign) b.getState();
			String name = Util.findSign(sign);
			if (name != null) {
				p.performCommand("ss view " + name);
			} else {
				Util.sudoSignsMessage(p, "&cERROR: That is not a SudoSign! &6Use: &d/ss create <name> &6instead.");
			}
		} else {
			Util.sudoSignsMessage(p, "&cERROR: A sign was not clicked! &6Cancelling...");
		}
		user.setView(false);
	}

	@EventHandler
	public void onDestroy(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if (e.getBlock().getState() instanceof Sign || e.getBlock().getState() instanceof WallSign) {
			Sign sign = (Sign) e.getBlock().getState();
			for (Map.Entry<String, SudoSign> entry : SudoSigns.signs.entrySet()) {
				if (entry.getValue().getSign().equals(sign)) {
					e.setCancelled(true);
					if (p.hasPermission(Permissions.SELECT)) {
						Util.sendSelectMenus(p, entry.getKey());
					} else {
						Util.sudoSignsMessage(p, "&cERROR: You do not have permission to destroy that sign!");
					}
				}
			}
		} else {
			final BlockFace[] faces = {BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
			for (BlockFace face : faces) {
				Block br = e.getBlock().getRelative(face);
				BlockState bs = br.getState();
				if (bs instanceof Sign || bs instanceof WallSign) {
					Sign sign = (Sign) bs;
					for (Map.Entry<String, SudoSign> entry : SudoSigns.signs.entrySet()) {
						if (entry.getValue().getSign().equals(sign)) {
							if (br.getBlockData() instanceof Sign) {
								if (e.getBlock().equals(sign.getBlock().getRelative(BlockFace.DOWN))) {
									e.setCancelled(true);
									Util.sudoSignsMessage(p, "&cERROR: You cannot destroy a block that is attached to a SudoSign!");
									return;
								}
							} else if (br.getBlockData() instanceof WallSign) {
								WallSign signData = (WallSign) sign.getBlock().getState().getBlockData();
								BlockFace attached = signData.getFacing().getOppositeFace();
								Block blockAttached = sign.getBlock().getRelative(attached);
								if (e.getBlock().equals(blockAttached)) {
									e.setCancelled(true);
									Util.sudoSignsMessage(p, "&cERROR: You cannot destroy a block that is attached to a SudoSign!");
									return;
								}

							}
						}
					}
				}
			}
		}
	}

	// The following events all attempt to prevent a SudoSign being broken
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent e) {
		ArrayList<Block> blocksToRemove = new ArrayList<>();
		for (Block block : e.blockList()) {
			if (block.getState() instanceof Sign || block.getState() instanceof WallSign) {
				Sign sign = (Sign) block.getState();
				for (Map.Entry<String, SudoSign> entry : SudoSigns.signs.entrySet()) {
					if (entry.getValue().getSign().equals(sign)) {
						blocksToRemove.add(block);
					}
				}
			}
			final BlockFace[] faces = {BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
			for (BlockFace face : faces) {
				BlockState b = block.getRelative(face).getState();
				if (b instanceof Sign || b instanceof WallSign) {
					Sign sign = (Sign) b;
					for (Map.Entry<String, SudoSign> entry : SudoSigns.signs.entrySet()) {
						if (entry.getValue().getSign().equals(sign)) {
							blocksToRemove.add(block);
						}
					}
				}
			}
		}
		for (Block b : blocksToRemove) {
			e.blockList().remove(b);
		}
	}

	@EventHandler
	public void onBlockBurn(BlockBurnEvent e) {
		final BlockFace[] faces = {BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
		for (BlockFace face : faces) {
			BlockState b = e.getBlock().getRelative(face).getState();
			if (b instanceof Sign || b instanceof WallSign) {
				Sign sign = (Sign) b;
				for (Map.Entry<String, SudoSign> entry : SudoSigns.signs.entrySet()) {
					if (entry.getValue().getSign().equals(sign)) {
						e.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onBlockFade(BlockFadeEvent e) {
		final BlockFace[] faces = {BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
		for (BlockFace face : faces) {
			BlockState b = e.getBlock().getRelative(face).getState();
			if (b instanceof Sign || b instanceof WallSign) {
				Sign sign = (Sign) b;
				for (Map.Entry<String, SudoSign> entry : SudoSigns.signs.entrySet()) {
					if (entry.getValue().getSign().equals(sign)) {
						e.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onLeavesDecay(LeavesDecayEvent e) {
		final BlockFace[] faces = {BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
		for (BlockFace face : faces) {
			BlockState b = e.getBlock().getRelative(face).getState();
			if (b instanceof Sign || b instanceof WallSign) {
				Sign sign = (Sign) b;
				for (Map.Entry<String, SudoSign> entry : SudoSigns.signs.entrySet()) {
					if (entry.getValue().getSign().equals(sign)) {
						e.setCancelled(true);
					}
				}
			}
		}
	}

}