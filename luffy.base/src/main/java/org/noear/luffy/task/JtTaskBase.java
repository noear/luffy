package org.noear.luffy.task;

import org.noear.snack.ONode;
import org.noear.solon.XUtil;
import org.noear.luffy.JtConfig;
import org.noear.luffy.dso.*;
import org.noear.luffy.utils.TextUtils;

public abstract class JtTaskBase implements IJtTask {
    protected JtTaskBase(String name, int interval) {
        _name = name;
        _interval = interval;
        _interval_bak = interval;
    }

    protected String _name;
    protected int _interval;
    protected int _interval_bak;//各份



    private String _node_id;
    protected String node_id(){
        if(_node_id == null){
            _node_id = JtBridge.nodeId();
        }

        return _node_id;
    }



    public void poolExecute(Runnable runnable){
        JtConfig.pools.execute(runnable);
        //new Thread(runnable).start();
    }


    protected ONode node_cfg() {
        //每次要实时产生
        //
        ONode _node_cfg = null;

        if (TextUtils.isEmpty(node_id()) == false) {
            try {
                String cfg_str = CfgUtil.cfgGetValue(node_id());

                if (XUtil.isEmpty(cfg_str) == false) {
                    cfg_str = cfg_str.trim();

                    if (cfg_str.startsWith("{")) {
                        _node_cfg = ONode.load(cfg_str);
                    }
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }

        return _node_cfg;
    }

    protected boolean node_current_can_run() {
        ONode cfg = node_cfg();

        if (cfg == null) {
            _interval = 1000 * 60;
            LogUtil.log(getName(), LogLevel.TRACE, JtUtil.g.localAddr() + "::is not enabled");
            return false;
        }

        if (cfg.contains("task") && cfg.get("task").getString().indexOf(getName()) < 0) {
            _interval = 1000 * 60;
            LogUtil.log(getName(), LogLevel.TRACE, JtUtil.g.localAddr() + "::is not enabled");
            return false;
        }

        if (cfg.contains("event") && cfg.get("event").getString().indexOf(getName()) < 0) {
            _interval = 1000 * 60;
            LogUtil.log(getName(), LogLevel.TRACE, JtUtil.g.localAddr() + "::is not enabled");
            return false;
        }

        return true;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public int getInterval() {
        return _interval;
    }
}
