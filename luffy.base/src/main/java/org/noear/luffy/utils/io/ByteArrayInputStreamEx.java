package org.noear.luffy.utils.io;

import java.io.ByteArrayInputStream;

public class ByteArrayInputStreamEx extends ByteArrayInputStream {
    protected int _offset = 0;

    public ByteArrayInputStreamEx(byte[] buf) {
        super(buf);
    }

    public ByteArrayInputStreamEx(byte[] buf, int offset, int length) {
        super(buf, offset, length);
        this._offset = offset;
    }

    @Override
    public void mark(int readAheadLimit) {
        if(readAheadLimit < _offset){
            readAheadLimit = _offset;
        }

        mark = readAheadLimit;
        pos = readAheadLimit;
    }

    @Override
    public synchronized void reset() {
        mark = _offset;
        pos = _offset;
    }
}
