package org.ai.carp.controller.util;

import org.ai.carp.model.dataset.Dataset;
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
        Dataset dataset = carpCase.getDataset();
        String inputData = dataset.getData();
        String outputData = carpCase.getStdout();
        // TODO: Check result
        carpCase.setValid(true);
        carpCase.setCost(0);
    }

}
