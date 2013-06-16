
package org.wargamer2010.signshopguardian.util;

import org.wargamer2010.signshop.player.SignShopPlayer;
import org.wargamer2010.signshopguardian.SignShopGuardian;

public class GuardianUtil {
    private GuardianUtil() {

    }

    public static Integer getPlayerGuardianCount(SignShopPlayer player) {
        Integer totalGuardians = 0;
        if(player.hasMeta(SignShopGuardian.getMetaName())) {
            try {
                totalGuardians += Integer.parseInt(player.getMeta(SignShopGuardian.getMetaName()));
            } catch(NumberFormatException ex) {
                player.removeMeta(SignShopGuardian.getMetaName());
            }
        }
        return totalGuardians;
    }

    public static Integer incrementPlayerGuardianCounter(SignShopPlayer player, int increment) {
        Integer newTotal = new Integer(getPlayerGuardianCount(player) + increment);
        player.setMeta(SignShopGuardian.getMetaName(), newTotal.toString());
        return newTotal;
    }
}
