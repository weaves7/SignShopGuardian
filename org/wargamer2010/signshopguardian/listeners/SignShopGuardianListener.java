
package org.wargamer2010.signshopguardian.listeners;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.wargamer2010.signshop.configuration.SignShopConfig;
import org.wargamer2010.signshop.player.SignShopPlayer;
import org.wargamer2010.signshopguardian.util.GuardianUtil;

public class SignShopGuardianListener implements Listener {
    private static Map<String, SavedInventory> savedStacks = new LinkedHashMap<String, SavedInventory>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        if(!event.getDrops().isEmpty()) {
            Player player = event.getEntity();
            SignShopPlayer ssPlayer = new SignShopPlayer(player);
            savedStacks.put(event.getEntity().getName(), new SavedInventory(player.getInventory().getContents(), player.getInventory().getArmorContents()));
            if(GuardianUtil.getPlayerGuardianCount(ssPlayer) > 0)
                event.getDrops().clear(); // Clear the drops as we'll give it back to player on respawn
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerSpawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        SignShopPlayer ssPlayer = new SignShopPlayer(event.getPlayer());

        if(savedStacks.containsKey(ssPlayer.getName())) {
            if(GuardianUtil.getPlayerGuardianCount(ssPlayer) > 0) {
                Integer guardiansLeft = GuardianUtil.incrementPlayerGuardianCounter(ssPlayer, -1);

                // player.givePlayerItems(savedStacks.get(player.getName()).toArray(stacks));
                SavedInventory saved = savedStacks.get(ssPlayer.getName());
                if(saved.getInventory() != null)
                    ssPlayer.givePlayerItems(saved.getInventory());
                if(saved.getArmor() != null)
                    player.getInventory().setArmorContents(saved.getArmor());

                Map<String, String> messageParts = new LinkedHashMap<String, String>();
                messageParts.put("!guardians", guardiansLeft.toString());
                if(guardiansLeft == 0)
                    ssPlayer.sendMessage(SignShopConfig.getError("player_used_last_guardian", messageParts));
                else
                    ssPlayer.sendMessage(SignShopConfig.getError("player_has_guardians_left", messageParts));
            } else {
                ssPlayer.sendMessage(SignShopConfig.getError("player_has_no_guardian", null));
            }
            savedStacks.remove(event.getPlayer().getName());
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
