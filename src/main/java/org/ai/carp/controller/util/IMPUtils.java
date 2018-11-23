package org.ai.carp.controller.util;

import org.ai.carp.model.judge.IMPCase;

public class IMPUtils {

    public static void checkResult(IMPCase impCase) {
        if (!CaseUtils.checkResult(impCase)) {
            return;
        }
        // TODO: check result
        String stdout = impCase.getStdout();
        String model = impCase.getDataset().getModel();
        impCase.setInfluence(0d);
    }

}
