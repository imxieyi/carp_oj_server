package org.ai.carp.controller.util;

import org.ai.carp.controller.exceptions.InvalidRequestException;
import org.bson.types.Binary;
import org.junit.Assert;
import org.junit.Test;

public class ArchiveUtilsTests {

    @Test(expected = InvalidRequestException.class)
    public void testInvalidArchiveConvert() {
        ArchiveUtils.convertSubmission("123", "CARP_solver.py");
    }

    @Test
    public void testValidArchiveConvert() {
        Binary binary = ArchiveUtils.convertSubmission(
                "UEsDBAoAAAAAAKKcXE0AAAAAAAAAAAAAAAAOAAAAQ0FSUF9zb2x2ZXIucHlQSwECPwAKAAAAAACi" +
                        "nFxNAAAAAAAAAAAAAAAADgAkAAAAAAAAACAAAAAAAAAAQ0FSUF9zb2x2ZXIucHkKACAAAAAAAAEA" +
                        "GAD3IyeMsm7UAfoGneKwbtQB+gad4rBu1AFQSwUGAAAAAAEAAQBgAAAALAAAAAAA", "CARP_solver.py");
        Assert.assertNotNull(binary);
    }

}
