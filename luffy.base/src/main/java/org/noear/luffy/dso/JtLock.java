package org.noear.luffy.dso;

import org.noear.solon.annotation.XNote;

/** 引擎锁管理器 */
public final class JtLock implements IJtLock {
    public final static JtLock g = new JtLock();

    @XNote("尝试获取锁")
    @Override
    public boolean tryLock(String group, String key, int inSeconds, String inMaster) {
        return JtBridge.lock().tryLock(group, key, inSeconds, inMaster);
    }

    @XNote("尝试获取锁")
    @Override
    public boolean tryLock(String group, String key, int inSeconds) {
        return JtBridge.lock().tryLock(group, key, inSeconds);
    }

    @XNote("尝试获取锁，锁3秒")
    @Override
    public boolean tryLock(String group, String key) {
        return JtBridge.lock().tryLock(group, key);
    }

    @XNote("是否已锁")
    @Override
    public boolean isLocked(String group, String key) {
        return JtBridge.lock().isLocked(group, key);
    }

    @XNote("取消锁")
    @Override
    public void unLock(String group, String key) {
        JtBridge.lock().unLock(group, key);
    }
}
