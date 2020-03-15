package org.wargamer2010.signshopguardian.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.wargamer2010.signshop.SignShop;
import org.wargamer2010.signshop.configuration.SignShopConfig;
import org.wargamer2010.signshop.player.SignShopPlayer;
import org.wargamer2010.signshopguardian.SignShopGuardian;
import org.wargamer2010.signshopguardian.util.GuardianUtil;

import java.util.*;


public class SignShopGuardianListener implements Listener {
    private static Set<UUID> trackedPlayers = new HashSet<>();


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        if (SignShopGuardian.isNotEnabledForWorld(event.getEntity().getWorld()))
            return;

        if (!event.getDrops().isEmpty()) {
            Player player = event.getEntity();
            SignShopPlayer ssPlayer = new SignShopPlayer(player);
            trackedPlayers.add(player.getUniqueId());
            if (GuardianUtil.getPlayerGuardianCount(ssPlayer) > 0) {
                event.setKeepInventory(true);
                event.getDrops().clear();
                if (SignShopGuardian.isEnableSaveXP()) {
                    event.setKeepLevel(true);
                    event.setDroppedExp(0);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerSpawn(PlayerRespawnEvent event) {
        if (SignShopGuardian.isNotEnabledForWorld(event.getPlayer().getWorld()))
            return;

        Player player = event.getPlayer();
        SignShopPlayer ssPlayer = new SignShopPlayer(event.getPlayer());

        if (trackedPlayers.contains(player.getUniqueId())) {
            if (GuardianUtil.getPlayerGuardianCount(ssPlayer) > 0) {
                Integer guardiansLeft = GuardianUtil.incrementPlayerGuardianCounter(ssPlayer, -1);
                String message;
                Map<String, String> messageParts = new LinkedHashMap<>();
                messageParts.put("!guardians", guardiansLeft.toString());
                if (guardiansLeft == 0)
                    message = SignShopConfig.getError("player_used_last_guardian", messageParts);
                else
                    message = SignShopConfig.getError("player_has_guardians_left", messageParts);

                Bukkit.getServer().getScheduler().runTaskLater(SignShop.getInstance(), new DelayedMessage(ssPlayer, message), 20 * 2);
            }
            else {
                ssPlayer.sendMessage(SignShopConfig.getError("player_has_no_guardian", null));
            }
            trackedPlayers.remove(event.getPlayer().getUniqueId());
        }
    }

    private static class DelayedMessage implements Runnable {
        private SignShopPlayer player;
        private String message;

        private DelayedMessage(SignShopPlayer player, String message) {
            this.player = player;
            this.message = message;
        }

        @Override
        public void run() {
            if (message != null)
                player.sendMessage(message);
        }
    }

}

