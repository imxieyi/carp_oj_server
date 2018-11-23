package org.ai.carp.controller.util;

import java.io.IOException;
import java.io.InputStream;

public class ResourceUtils {

    static String readResource(String name) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = classLoader.getResourceAsStream(name);
        byte[] bytes = new byte[is.available()];
        is.read(bytes);
        is.close();
        String data = new String(bytes);
        return data;
    }

}
