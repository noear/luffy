package org.noear.luffy.dso;

import org.noear.solon.Solon;;
import org.noear.luffy.executor.IJtConfigAdapter;
import org.noear.luffy.executor.IJtExecutorAdapter;
import org.noear.luffy.model.AFileModel;

import java.util.List;
import java.util.Map;

/**
 * 执行工厂适配器
 * */
public class JtAdapter implements IJtExecutorAdapter, IJtConfigAdapter {
    public static JtAdapter global = new JtAdapter();

    private String _defaultExecutor = "freemarker";

    public JtAdapter() {
    }

    @Override
    public void log(AFileModel file, Map<String, Object> data) {
        DbApi.log(data);
    }

    @Override
    public void logError(AFileModel file, String msg, Throwable err) {
        LogUtil.log("_file", file.tag, file.path, LogLevel.ERROR, "", msg);
    }

    @Override
    public AFileModel fileGet(String path) throws Exception {
        return AFileUtil.get(path);
    }

    @Override
    public List<AFileModel> fileFind(String tag, String label, boolean isCache) throws Exception {
        return DbApi.fileGetPaths(tag, label, isCache);
    }

    private static String _nodeId;

    @Override
    public String nodeId() {
        if (_nodeId == null) {
            _nodeId = Solon.cfg().get("luffy.node", "");
        }

        return _nodeId;
    }

    @Override
    public String defaultExecutor() {
        return _defaultExecutor;
    }

    public void defaultExecutorSet(String defaultExecutor) {
        _defaultExecutor = defaultExecutor;
    }


    @Override
    public String cfgGet(String name, String def) throws Exception {
        return DbApi.cfgGet(name, def);
    }

    @Override
    public Map cfgMap(String name) throws Exception {
        return DbApi.cfgMap(name);
    }

    @Override
    public boolean cfgSet(String name, String value) throws Exception {
        return DbApi.cfgSet(name, value, null);
    }
}
