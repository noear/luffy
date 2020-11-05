package org.noear.luffy.utils;


import org.noear.luffy.utils.io.ByteArrayInputStreamEx;

import java.io.*;

public class IOUtils {
    public static final boolean reset(InputStream stream) {
        try {
            stream.reset();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public static final InputStream copyOf(InputStream stream) throws Exception{
        return fromBytes(toBytes(stream));
    }

    public static final InputStream fromBytes(byte[] buf) {
        return new ByteArrayInputStreamEx(buf);
    }

    public static final byte[] toBytes(InputStream stream) throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = stream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }


    public static final String toString(InputStream stream,String charset) throws IOException {
        try {
            stream.reset();
        }catch (Exception ex){}

        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = stream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        return swapStream.toString(charset);
    }

    public static final InputStream outToIn(OutputStream out){
        ByteArrayOutputStream out2 = (ByteArrayOutputStream) out;
        final ByteArrayInputStreamEx in2 = new ByteArrayInputStreamEx(out2.toByteArray());
        return in2;
    }

    public static byte[] getFileBytes(File file) throws IOException {
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            System.out.println("file too big...");
            return null;
        }
        return toBytes( new FileInputStream(file));
    }
}
