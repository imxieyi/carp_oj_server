package org.ai.carp.controller.util;

import org.ai.carp.model.dataset.Dataset;
import org.ai.carp.model.judge.CARPCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class CARPUtilsTests {

    CARPCase carpCase;

    @Before
    public void setUp() throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = classLoader.getResourceAsStream("gdb10.dat");
        byte[] bytes = new byte[is.available()];
        is.read(bytes);
        is.close();
        String data = new String(bytes);
        Dataset dataset = new Dataset("gdb10", 10, 256, 1, data);
        carpCase = new CARPCase(null, dataset, null);
        carpCase.setStatus(CARPCase.FINISHED);
        carpCase.setExitcode(0);
        is = classLoader.getResourceAsStream("gdb10_out.txt");
        bytes = new byte[is.available()];
        is.read(bytes);
        is.close();
        data = new String(bytes);
        carpCase.setStdout(data);
    }

    @Test
    public void testCheckResult() {
        CARPUtils.checkResult(carpCase);
        Assert.assertTrue(carpCase.isValid());
        Assert.assertEquals(298, carpCase.getCost());
    }

}
