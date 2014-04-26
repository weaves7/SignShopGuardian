
package org.wargamer2010.signshopguardian;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.wargamer2010.signshop.configuration.SignShopConfig;
import org.wargamer2010.signshop.player.PlayerIdentifier;
import org.wargamer2010.signshop.player.SignShopPlayer;
import org.wargamer2010.signshop.util.signshopUtil;
import org.wargamer2010.signshopguardian.util.GuardianUtil;

public class CommandHandler {
    private static final String addGuardiansUsage = "Usage: addguardians [player] [amount]";


    private CommandHandler() {

    }

    private static void sendMessage(CommandSender sender, String message) {
        if(sender instanceof Player)
            new SignShopPlayer((Player)sender).sendMessage(message);
        else
            SignShopGuardian.log(message, Level.INFO);
    }

    private static String checkPlayer(String playername) {
        OfflinePlayer offline = Bukkit.getServer().getOfflinePlayer(playername);
        Player online = Bukkit.getServer().getPlayer(playername);

        if(online == null && (offline == null || !offline.hasPlayedBefore()))
            return null;

        return Bukkit.getServer().getPlayer(playername) == null
                    ? Bukkit.getServer().getOfflinePlayer(playername).getName()
                    : Bukkit.getServer().getPlayer(playername).getName();
    }

    public static void handleGuardianQuery(CommandSender sender, String args[]) {
        SignShopPlayer inspectPlayer;

        if(args.length > 0) {
            String playername = checkPlayer(args[0]);

            if(playername == null) {
                sendMessage(sender, "Player does not exist on this server");
                return;
            }

            inspectPlayer = PlayerIdentifier.getByName(playername);
        } else {
            if(!(sender instanceof Player)) {
                sendMessage(sender, "Specify a player to use this command on the console");
                return;
            }

            inspectPlayer = new SignShopPlayer((Player)sender);
        }

        Map<String, String> parts = new HashMap<String, String>();
        parts.put("!player", inspectPlayer.getName());
        parts.put("!guardians", GuardianUtil.getPlayerGuardianCount(inspectPlayer).toString());

        if(args.length > 0)
            sendMessage(sender, SignShopConfig.getError("other_player_has_guardians_left", parts));
        else
            sendMessage(sender, SignShopConfig.getError("player_has_guardians_left", parts));
    }

    public static boolean handleAddGuardians(CommandSender sender, String[] args) {
        if(sender instanceof Player) {
            SignShopPlayer player = new SignShopPlayer((Player) sender);
            if(!signshopUtil.hasOPForCommand(player))
                return true;
        }

        if(args.length < 1)
            return false;

        String playername;
        if(args.length > 1) {
            playername = checkPlayer(args[0]);

            if(playername == null) {
                sendMessage(sender, "Player does not exist on this server");
                return true;
            }
        } else {
            if(!(sender instanceof Player)) {
                sendMessage(sender, "Specify a player to use this command on the console");
                return true;
            }
            playername = ((Player)sender).getName();
        }

        try {
            SignShopPlayer dude = PlayerIdentifier.getByName(playername);
            int index = (args.length == 1 ? 0 : 1);
            int count = Integer.parseInt(args[index]);
            // Taking away guardians should be possible by passing negatives
            if(count < 0 && GuardianUtil.getPlayerGuardianCount(dude) < Math.abs(count)) {
                Map<String, String> temp = new LinkedHashMap<String, String>();
                temp.put("!guardians", GuardianUtil.getPlayerGuardianCount(dude).toString());
                sendMessage(sender, SignShopConfig.getError("other_player_has_insufficient_guardians", temp));
                return true;
            }

            GuardianUtil.incrementPlayerGuardianCounter(dude, count);

            Map<String, String> temp = new LinkedHashMap<String, String>();
            temp.put("!guardians", Integer.toString(count));

            if(args.length == 1)
                sendMessage(sender, SignShopConfig.getError("added_guardians_for_self", temp));
            else
                sendMessage(sender, SignShopConfig.getError("added_guardians_for_player", temp));
        } catch(NumberFormatException ex) {
            return false;
        }

        return true;
    }
}
