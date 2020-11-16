package org.noear.luffy.dso;

import org.noear.solon.annotation.Note;

/** 引擎锁管理器 */
public final class JtLock implements IJtLock {
    public final static JtLock g = new JtLock();

    @Note("尝试获取锁")
    @Override
    public boolean tryLock(String group, String key, int inSeconds, String inMaster) {
        return JtBridge.lock().tryLock(group, key, inSeconds, inMaster);
    }

    @Note("尝试获取锁")
    @Override
    public boolean tryLock(String group, String key, int inSeconds) {
        return JtBridge.lock().tryLock(group, key, inSeconds);
    }

    @Note("尝试获取锁，锁3秒")
    @Override
    public boolean tryLock(String group, String key) {
        return JtBridge.lock().tryLock(group, key);
    }

    @Note("是否已锁")
    @Override
    public boolean isLocked(String group, String key) {
        return JtBridge.lock().isLocked(group, key);
    }

    @Note("取消锁")
    @Override
    public void unLock(String group, String key) {
        JtBridge.lock().unLock(group, key);
    }
}
