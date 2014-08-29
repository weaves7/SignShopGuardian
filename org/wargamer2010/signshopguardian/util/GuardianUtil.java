
package org.wargamer2010.signshopguardian.util;

import java.lang.reflect.Method;
import org.wargamer2010.signshop.operations.SignShopArguments;
import org.wargamer2010.signshop.player.SignShopPlayer;
import org.wargamer2010.signshop.util.signshopUtil;
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

    public static Integer getAmountOfGuardians(SignShopArguments ssArgs) {
        Integer numberOfGuardians = signshopUtil.getNumberFromLine(ssArgs.getSign().get(), 1).intValue();
        if(numberOfGuardians == 0)
            numberOfGuardians = 1;
        return numberOfGuardians;
    }

    /**
     * Returns true if the given class has a method by the name specified.
     * Name compare is case insensitive.
     *
     * @param aClass Class to check for the method
     * @param methodName Name of the method to find
     * @return True if the method was found by the given name
     */
    public static boolean hasMethod(Class<?> aClass, String methodName) {
        for(Method method : aClass.getMethods()) {
            if(method.getName().equalsIgnoreCase(methodName))
                return true;
        }

        return false;
    }
}
