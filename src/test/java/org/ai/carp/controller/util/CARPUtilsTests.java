package org.ai.carp.controller.util;

import org.ai.carp.model.dataset.CARPDataset;
import org.ai.carp.model.judge.CARPCase;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

public class CARPUtilsTests {

    static CARPCase carpCase, carpCase2;

    @BeforeClass
    public static void setUp() throws IOException {
        CARPDataset dataset = new CARPDataset("gdb10", 10, 256, 1, ResourceUtils.readResource("gdb10.dat"));
        carpCase = new CARPCase(null, dataset, null);
        carpCase.setStatus(CARPCase.FINISHED);
        carpCase.setExitcode(0);
        String data = ResourceUtils.readResource("gdb10_out.txt");
        carpCase.setStdout(data);
        carpCase2 = new CARPCase(null, dataset, null);
        carpCase2.setStatus(CARPCase.FINISHED);
        carpCase2.setExitcode(0);
        data = ResourceUtils.readResource("gdb10_out2.txt");
        carpCase2.setStdout(data);
    }

    @Test
    public void testCheckResult() {
        CARPUtils.checkResult(carpCase);
        Assert.assertTrue(carpCase.isValid());
        Assert.assertEquals(298, carpCase.getCost());
    }

    @Test
    public void testCheckResult2() {
        CARPUtils.checkResult(carpCase2);
        Assert.assertTrue(carpCase2.isValid());
        Assert.assertEquals(301, carpCase2.getCost());
    }

}
