package org.ai.carp.controller.util;

import org.ai.carp.model.judge.ISECase;

public class ISEUtils {
    
    public static void checkResult(ISECase iseCase) {
        if (!CaseUtils.checkResult(iseCase)) {
            return;
        }
        double stdInfluence = iseCase.getDataset().getInfluence();
        double bias = iseCase.getDataset().getBias();
        String stdout = iseCase.getStdout();
        String firstLine = stdout.replaceAll("\r", "").split("\n")[0];
        try {
            iseCase.setInfluence(Double.valueOf(firstLine));
            boolean result = checkIsLargeBias(stdInfluence, iseCase.getInfluence(), bias);
            iseCase.setValid(!result);
            if (result) {
                iseCase.setReason("Bias Too Large");
            } else {
                iseCase.setReason("Solution accepted");
            }
        } catch (NumberFormatException ignored) {
            iseCase.setValid(false);
            iseCase.setReason("Invalid number format");
        }
    }

    private static boolean checkIsLargeBias(double standard, double student, double toleranceRatio) {
        double max_bias = standard * toleranceRatio;
        double bias = Math.abs(standard - student);
        return bias > max_bias;
    }
    
}
