
package org.wargamer2010.signshopguardian.listeners;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.wargamer2010.signshop.SignShop;
import org.wargamer2010.signshop.configuration.SignShopConfig;
import org.wargamer2010.signshop.player.SignShopPlayer;
import org.wargamer2010.signshopguardian.SignShopGuardian;
import org.wargamer2010.signshopguardian.util.GuardianUtil;

public class SignShopGuardianListener implements Listener {
    private static Map<String, SavedInventory> savedStacks = new LinkedHashMap<String, SavedInventory>();
    private boolean hasKeepInventory;

    public SignShopGuardianListener() {
        hasKeepInventory = GuardianUtil.hasMethod(PlayerDeathEvent.class, "setKeepInventory");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        if(!SignShopGuardian.isEnabledForWorld(event.getEntity().getWorld()))
            return;

        if(!event.getDrops().isEmpty()) {
            Player player = event.getEntity();
            SignShopPlayer ssPlayer = new SignShopPlayer(player);

            SavedInventory inv = new SavedInventory(player.getInventory().getContents(), player.getInventory().getArmorContents());
            savedStacks.put(event.getEntity().getName(), inv);
            if(GuardianUtil.getPlayerGuardianCount(ssPlayer) > 0) {
                if(hasKeepInventory) {
                    // New event method since 1.7.10, let Bukkit do the hard work
                    event.setKeepInventory(true);
                } else {
                    // Clear the drops as we'll give it back to player on respawn
                    event.getDrops().clear();
                    if(SignShopGuardian.isEnableSaveXP()) {
                        event.setKeepLevel(true);
                        event.setDroppedExp(0);
                    }
                }

            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerSpawn(PlayerRespawnEvent event) {
        if(!SignShopGuardian.isEnabledForWorld(event.getPlayer().getWorld()))
            return;

        Player player = event.getPlayer();
        SignShopPlayer ssPlayer = new SignShopPlayer(event.getPlayer());

        if(savedStacks.containsKey(ssPlayer.getName())) {
            if(GuardianUtil.getPlayerGuardianCount(ssPlayer) > 0) {
                Integer guardiansLeft = GuardianUtil.incrementPlayerGuardianCounter(ssPlayer, -1);
                String message;
                Map<String, String> messageParts = new LinkedHashMap<String, String>();
                messageParts.put("!guardians", guardiansLeft.toString());
                if(guardiansLeft == 0)
                    message = SignShopConfig.getError("player_used_last_guardian", messageParts);
                else
                    message = SignShopConfig.getError("player_has_guardians_left", messageParts);

                DelayedGiver delay;
                if(!hasKeepInventory) {
                    SavedInventory saved = savedStacks.get(ssPlayer.getName());

                    // Restoring the inventory straight on the spawn event won't work anymore, so wait a bit
                    if(saved.getInventory() != null) {
                        delay = new DelayedGiver(ssPlayer, saved, message);
                    } else {
                        delay = new DelayedGiver(ssPlayer, null, message);
                    }

                    if(saved.getArmor() != null)
                        player.getInventory().setArmorContents(saved.getArmor());
                } else {
                    // Sending messages straight on the spawn event won't work, so delay it a few seconds
                    delay = new DelayedGiver(ssPlayer, null, message);
                }

                Bukkit.getServer().getScheduler().runTaskLater(SignShop.getInstance(), delay, 20*2);
            } else {
                ssPlayer.sendMessage(SignShopConfig.getError("player_has_no_guardian", null));
            }
            savedStacks.remove(event.getPlayer().getName());
        }
    }

    private class DelayedGiver implements Runnable {
        private SignShopPlayer player;
        private SavedInventory saved;
        private String message;

        private DelayedGiver(SignShopPlayer player, SavedInventory saved, String message) {
            this.player = player;
            this.saved = saved;
            this.message = message;
        }

        @Override
        public void run() {
            if(player != null && saved != null)
                player.givePlayerItems(saved.getInventory());
            if(message != null)
                player.sendMessage(message);
        }
    }

    private class SavedInventory {
        private ItemStack[] Inventory = new ItemStack[0];
        private ItemStack[] Armor = new ItemStack[0];

        private SavedInventory(ItemStack[] inv, ItemStack[] armor) {
            if(inv != null)
                Inventory = getNotNullItems(inv);
            if(armor != null)
                Armor = getNotNullItems(armor);
        }

        private ItemStack[] getNotNullItems(ItemStack[] stacks) {
            if(stacks == null)
                return new ItemStack[0];
            List<ItemStack> tempStacks = new LinkedList<ItemStack>();
            for(ItemStack stack : stacks)
                if(stack != null)
                    tempStacks.add(stack);
            ItemStack[] returnedStacks = new ItemStack[tempStacks.size()];
            return tempStacks.toArray(returnedStacks);
        }

        public ItemStack[] getInventory() {
            return Inventory;
        }

        public ItemStack[] getArmor() {
            return Armor;
        }
    }
}
