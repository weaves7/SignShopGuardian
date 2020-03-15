
package org.wargamer2010.signshopguardian.operations;

import org.wargamer2010.signshop.configuration.SignShopConfig;
import org.wargamer2010.signshop.operations.SignShopArguments;
import org.wargamer2010.signshop.operations.SignShopOperation;
import org.wargamer2010.signshop.player.SignShopPlayer;
import org.wargamer2010.signshopguardian.SignShopGuardian;
import org.wargamer2010.signshopguardian.util.GuardianUtil;

public class givePlayerGuardians implements SignShopOperation {
    @Override
    public Boolean setupOperation(SignShopArguments ssArgs) {
        if (SignShopGuardian.isNotEnabledForWorld(ssArgs.getPlayer().get().getWorld())) {
            ssArgs.getPlayer().get().sendMessage(SignShopConfig.getError("guardian_not_allowed_in_world", ssArgs.getMessageParts()));
            return false;
        }

        ssArgs.setMessagePart("!guardians", GuardianUtil.getAmountOfGuardians(ssArgs).toString());
        return true;
    }

    @Override
    public Boolean checkRequirements(SignShopArguments ssArgs, Boolean activeCheck) {
        if (SignShopGuardian.isNotEnabledForWorld(ssArgs.getPlayer().get().getWorld())) {
            ssArgs.getPlayer().get().sendMessage(SignShopConfig.getError("guardian_not_allowed_in_world", ssArgs.getMessageParts()));
            return false;
        }

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
