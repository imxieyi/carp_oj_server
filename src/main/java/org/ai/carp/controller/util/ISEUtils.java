package org.ai.carp.controller.util;

import org.ai.carp.model.judge.ISECase;

public class ISEUtils {
    
    public static double getInfluence(String network, String seeds, String model) {
        // TODO: generate influence
        return 0;
    }
    
    public static void checkResult(ISECase iseCase) {
        if (!CaseUtils.checkResult(iseCase)) {
            return;
        }
        // TODO: check result
        double stdInfluence = iseCase.getDataset().getInfluence();
        String stdout = iseCase.getStdout();
        iseCase.setInfluence(0d);
    }
    
}
