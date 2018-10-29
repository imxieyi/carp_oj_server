package org.ai.carp.controller.util;

import org.ai.carp.model.Database;
import org.ai.carp.model.user.User;

import java.util.Date;

public class CaseUtils {

    public static int countPreviousDay(User user) {
        Date oneDayBefore = new Date(new Date().getTime() - 24 * 3600 * 1000);
        return Database.getInstance().getCarpCases()
                .countCARPCasesByUserAndSubmitTimeAfter(user, oneDayBefore);
    }

}
