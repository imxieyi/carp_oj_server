package org.ai.carp.controller.util;

import org.ai.carp.model.judge.ISECase;

public class ISEUtils {
    
    public static void checkResult(ISECase iseCase) {
        if (!CaseUtils.checkResult(iseCase)) {
            return;
        }
        // TODO: check result
        double stdInfluence = iseCase.getDataset().getInfluence();
        String stdout = iseCase.getStdout();
        iseCase.setInfluence(Double.valueOf(stdout));
        boolean result = checkIsLargeBias(stdInfluence, iseCase.getInfluence(), 0.001);
        iseCase.setValid(!result);
        if (result) {
            iseCase.setReason("Bias Too Large");
        } else {
            iseCase.setReason("Solution accepted");
        }

    }

    private static boolean checkIsLargeBias(double standard, double student, double toleranceRatio) {
        double max_bias = standard * toleranceRatio;
        double bias = Math.abs(standard - student);
        return bias > max_bias;
    }
    
}
