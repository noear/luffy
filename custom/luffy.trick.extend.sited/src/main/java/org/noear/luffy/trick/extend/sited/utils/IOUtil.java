package org.noear.luffy.trick.extend.sited.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtil {
    public static final InputStream byte2Stream(byte[] buf) {
        return new ByteArrayInputStream(buf);
    }

    public static final byte[] stream2Byte(InputStream stream) throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = stream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }


    public static final String stream2String(InputStream stream) throws IOException {
        stream.reset();
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = stream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        return swapStream.toString();
    }
}
