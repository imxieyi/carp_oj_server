package org.ai.carp.controller.util;

import org.ai.carp.model.dataset.Dataset;
import org.ai.carp.model.judge.CARPCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class CARPUtilsTests {

    CARPCase carpCase, carpCase2;

    private String readResource(String name) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = classLoader.getResourceAsStream(name);
        byte[] bytes = new byte[is.available()];
        is.read(bytes);
        is.close();
        String data = new String(bytes);
        return data;
    }

    @Before
    public void setUp() throws IOException {
        Dataset dataset = new Dataset("gdb10", 10, 256, 1, readResource("gdb10.dat"));
        carpCase = new CARPCase(null, dataset, null);
        carpCase.setStatus(CARPCase.FINISHED);
        carpCase.setExitcode(0);
        String data = readResource("gdb10_out.txt");
        carpCase.setStdout(data);
        carpCase2 = new CARPCase(null, dataset, null);
        carpCase2.setStatus(CARPCase.FINISHED);
        carpCase2.setExitcode(0);
        data = readResource("gdb10_out2.txt");
        carpCase2.setStdout(data);
    }

    @Test
    public void testCheckResult() {
        CARPUtils.checkResult(carpCase);
        Assert.assertTrue(carpCase.isValid());
        Assert.assertEquals(298, carpCase.getCost());
        CARPUtils.checkResult(carpCase2);
        Assert.assertTrue(carpCase2.isValid());
        Assert.assertEquals(301, carpCase.getCost());
    }

}
