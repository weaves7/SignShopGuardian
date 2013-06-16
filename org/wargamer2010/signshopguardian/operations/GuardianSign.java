
package org.wargamer2010.signshopguardian.operations;

import org.wargamer2010.signshop.operations.SignShopArguments;
import org.wargamer2010.signshop.operations.SignShopOperation;
import org.wargamer2010.signshop.player.SignShopPlayer;
import org.wargamer2010.signshop.util.signshopUtil;
import org.wargamer2010.signshopguardian.SignShopGuardian;
import org.wargamer2010.signshopguardian.util.GuardianUtil;

public class GuardianSign implements SignShopOperation {
    private Integer GetAmountOfGuardians(SignShopArguments ssArgs) {
        Integer numberOfGuardians = signshopUtil.getNumberFromLine(ssArgs.getSign().get(), 1).intValue();
        if(numberOfGuardians == 0)
            numberOfGuardians = 1;
        return numberOfGuardians;
    }

    @Override
    public Boolean setupOperation(SignShopArguments ssArgs) {
        ssArgs.setMessagePart("!guardians", GetAmountOfGuardians(ssArgs).toString());
        return true;
    }

    @Override
    public Boolean checkRequirements(SignShopArguments ssArgs, Boolean activeCheck) {
        ssArgs.setMessagePart("!guardians", GetAmountOfGuardians(ssArgs).toString());
        ssArgs.setMessagePart("!currentguardians", GuardianUtil.getPlayerGuardianCount(ssArgs.getPlayer().get()).toString());
        return true;
    }

    @Override
    public Boolean runOperation(SignShopArguments ssArgs) {
        SignShopPlayer player = ssArgs.getPlayer().get();
        Integer totalGuardians = GuardianUtil.incrementPlayerGuardianCounter(player, GetAmountOfGuardians(ssArgs));
        ssArgs.setMessagePart("!guardians", GetAmountOfGuardians(ssArgs).toString());
        ssArgs.setMessagePart("!currentguardians", totalGuardians.toString());
        return true;
    }
}
