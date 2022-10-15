package org.noear.luffy.dso;

import org.noear.wood.cache.LocalCache;

public class DefaultJtLock implements IJtLock {
    private LocalCache _cache = new LocalCache("lock",3);
    private String _lock = "";
    @Override
    public boolean tryLock(String group, String key, int inSeconds, String inMaster) {
        String lkey = group + "_" + key;

        synchronized (_lock) {
            Object tmp = _cache.get(lkey);

            if (tmp == null) {
                //如果还没有，则锁成功
                if(inMaster == null) {
                    _cache.store(lkey, "_", inSeconds);
                }else{
                    _cache.store(lkey, inMaster, inSeconds);
                }
                return true;
            }

            /**
             * 如果 inMaster 是 null；则结果肯定为 false（被别人锁了）
             *
             * 如果 inMaster 不是 null；则看比对结果
             *
             * */
            return tmp.equals(inMaster);// inMaster 可能是 null
        }
    }

    @Override
    public boolean tryLock(String group, String key, int inSeconds) {
        return tryLock(group, key, inSeconds, null);
    }

    @Override
    public boolean tryLock(String group, String key) {
        return tryLock(group, key, 3, null);
    }

    @Override
    public boolean isLocked(String group, String key) {
        String lkey = group + "_" + key;

        synchronized (_lock) {
            Object tmp = _cache.get(lkey);
            return tmp != null;
        }
    }

    @Override
    public void unLock(String group, String key) {
        String lkey = group + "_" + key;
        synchronized (_lock) {
             _cache.remove(lkey);
        }
    }
}
