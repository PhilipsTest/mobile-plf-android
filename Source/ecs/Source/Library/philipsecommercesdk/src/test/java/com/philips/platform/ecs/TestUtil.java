package com.philips.platform.ecs;

import java.io.IOException;
import java.io.InputStream;

public class TestUtil {

    public static String loadJSONFromFile(InputStream is) {
        String json = null;
        try {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
