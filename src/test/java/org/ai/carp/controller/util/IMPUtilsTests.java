package org.ai.carp.controller.util;

import org.ai.carp.model.dataset.IMPDataset;
import org.ai.carp.model.judge.IMPCase;
import org.junit.Test;

import java.io.IOException;

public class IMPUtilsTests {

    @Test
    public void testCheckResult() throws IOException {
        IMPDataset dataset = new IMPDataset("test", 10, 256, 1, 5, "IC",
                ResourceUtils.readResource("network.txt"));
        IMPCase impCase = new IMPCase(null, dataset, null);
        impCase.setStatus(IMPCase.FINISHED);
        impCase.setExitcode(0);
        String data = ResourceUtils.readResource("network_imp_ic.txt");
        impCase.setStdout(data);
        IMPUtils.checkResult(impCase);
        // TODO: assertions
    }

}
