package org.ai.carp.controller.util;

import org.ai.carp.controller.util.SolutionChecker.Checker;
import org.ai.carp.model.judge.CARPCase;

public class CARPUtils {

    public static void checkResult(CARPCase carpCase) {
        if (!CaseUtils.checkResult(carpCase)) {
            return;
        }
        Checker checker = new Checker(carpCase);
        checker.checkSolution();
    }

}
