package org.ai.carp.controller.util;

import org.ai.carp.model.Database;
import org.ai.carp.model.judge.BaseCase;
import org.ai.carp.model.user.User;
import org.springframework.util.StringUtils;

import java.util.Date;

public class CaseUtils {

    public static int countPreviousDay(User user) {
        Date oneDayBefore = new Date(new Date().getTime() - 24 * 3600 * 1000);
        return Database.getInstance().getCarpCases()
                .countCARPCasesByUserAndSubmitTimeAfter(user, oneDayBefore);
    }
    
    public static boolean checkResult(BaseCase baseCase) {
        if (baseCase.getStatus() != BaseCase.FINISHED) {
            return false;
        }
        if (baseCase.isTimedout()) {
            baseCase.setReason("Timed out");
            return false;
        }
        if (baseCase.getExitcode() != 0) {
            baseCase.setReason("Exit code is not zero");
            return false;
        }
        if (StringUtils.isEmpty(baseCase.getStdout())) {
            baseCase.setReason("No output");
            return false;
        }
        return true;
    }

}
