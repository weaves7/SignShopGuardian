
package org.wargamer2010.signshopguardian.operations;

import org.wargamer2010.signshop.operations.SignShopArguments;
import org.wargamer2010.signshop.operations.SignShopOperation;
import org.wargamer2010.signshop.player.SignShopPlayer;
import org.wargamer2010.signshopguardian.util.GuardianUtil;

public class givePlayerGuardians implements SignShopOperation {
    @Override
    public Boolean setupOperation(SignShopArguments ssArgs) {
        ssArgs.setMessagePart("!guardians", GuardianUtil.getAmountOfGuardians(ssArgs).toString());
        return true;
    }

    @Override
    public Boolean checkRequirements(SignShopArguments ssArgs, Boolean activeCheck) {
        ssArgs.setMessagePart("!guardians", GuardianUtil.getAmountOfGuardians(ssArgs).toString());
        ssArgs.setMessagePart("!currentguardians", GuardianUtil.getPlayerGuardianCount(ssArgs.getPlayer().get()).toString());
        return true;
    }

    @Override
    public Boolean runOperation(SignShopArguments ssArgs) {
        SignShopPlayer player = ssArgs.getPlayer().get();
        Integer totalGuardians = GuardianUtil.incrementPlayerGuardianCounter(player, GuardianUtil.getAmountOfGuardians(ssArgs));
        ssArgs.setMessagePart("!guardians", GuardianUtil.getAmountOfGuardians(ssArgs).toString());
        ssArgs.setMessagePart("!currentguardians", totalGuardians.toString());
        return true;
    }
}
