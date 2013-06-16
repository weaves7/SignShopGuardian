
package org.wargamer2010.signshopguardian.listeners;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
    private static Map<String, List<ItemStack>> savedStacks = new LinkedHashMap<String, List<ItemStack>>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        if(!event.getDrops().isEmpty()) {
            SignShopPlayer player = new SignShopPlayer(event.getEntity());
            List<ItemStack> storeStacks = new LinkedList<ItemStack>();
            storeStacks.addAll(event.getDrops());
            savedStacks.put(event.getEntity().getName(), storeStacks);
            if(GuardianUtil.getPlayerGuardianCount(player) > 0)
                event.getDrops().clear(); // Clear the drops as we'll give it back to player on respawn
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerSpawn(PlayerRespawnEvent event) {
        SignShopPlayer player = new SignShopPlayer(event.getPlayer());

        if(savedStacks.containsKey(player.getName())) {
            if(GuardianUtil.getPlayerGuardianCount(player) > 0) {
                ItemStack[] stacks = new ItemStack[savedStacks.get(player.getName()).size()];
                Integer guardiansLeft = GuardianUtil.incrementPlayerGuardianCounter(player, -1);

                player.givePlayerItems(savedStacks.get(player.getName()).toArray(stacks));

                Map<String, String> messageParts = new LinkedHashMap<String, String>();
                messageParts.put("!guardians", guardiansLeft.toString());
                if(guardiansLeft == 0)
                    player.sendMessage(SignShopConfig.getError("player_used_last_guardian", messageParts));
                else
                    player.sendMessage(SignShopConfig.getError("player_has_guardians_left", messageParts));
            } else {
                player.sendMessage(SignShopConfig.getError("player_has_no_guardian", null));
            }
            savedStacks.remove(event.getPlayer().getName());
        }
    }
}
