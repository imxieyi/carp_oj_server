package org.ai.carp.controller.util;

import org.ai.carp.model.dataset.ISEDataset;
import org.ai.carp.model.judge.ISECase;
import org.junit.Test;

import java.io.IOException;

public class ISEUtilsTests {

    @Test
    public void testCheckResult() throws IOException {
        ISEDataset dataset = new ISEDataset("test", 10, 256, 1, "IC",
                ResourceUtils.readResource("network.txt"),
                ResourceUtils.readResource("seeds.txt"), 5);
        ISECase iseCase = new ISECase(null, dataset, null);
        iseCase.setStatus(ISECase.FINISHED);
        iseCase.setExitcode(0);
        String data = ResourceUtils.readResource("network_ise_ic.txt");
        iseCase.setStdout(data);
        ISEUtils.checkResult(iseCase);
        // TODO: assertions
    }

}
