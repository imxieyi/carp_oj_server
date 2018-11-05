package org.ai.carp.controller.util;

import org.ai.carp.controller.util.SolutionChecker.Checker;
import org.ai.carp.model.judge.CARPCase;
import org.springframework.util.StringUtils;

public class CARPUtils {

    public static void checkResult(CARPCase carpCase) {
        if (carpCase.getStatus() != CARPCase.FINISHED) {
            return;
        }
        if (carpCase.getExitcode() != 0) {
            carpCase.setReason("Exit code is not zero");
            return;
        }
        if (StringUtils.isEmpty(carpCase.getStdout())) {
            carpCase.setReason("No output");
            return;
        }
        Checker checker = new Checker(carpCase);
        checker.checkSolution();
    }

}
