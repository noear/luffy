package org.noear.luffy.executor;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.luffy.dso.JtBridge;
import org.noear.luffy.model.AFileModel;
import org.noear.luffy.utils.ExceptionUtils;
import org.noear.luffy.utils.StringUtils;
import org.noear.luffy.utils.TextUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 执行器工厂
 * */
public class ExecutorFactory {
    private static final Map<String, ExecutorEntity> _map = new HashMap<>();
    private static IJtExecutor _def;

    /**
     * 记录日志
     */
    public static void errorLog(AFileModel file, String msg, Throwable err) {
        JtBridge.executorAdapter().logError(file, msg, err);
    }

    /**
     * 获取文件
     */
    public static AFileModel fileGet(String path) throws Exception {
        return JtBridge.executorAdapter().fileGet(path);
    }

    /**
     * 执行器清单
     */
    public static Set<String> list() {
        return _map.keySet();
    }


    ///////////////////


    /**
     * 注册执行引擎
     */
    public static void register(IJtExecutor engine) {
        register(engine.language(), engine, 0);
    }

    /**
     * 注册执行引擎
     */
    public static void register(String language, IJtExecutor engine) {
        register(language, engine, 0);
    }

    /**
     * 注册执行引擎
     */
    public static void register(String language, IJtExecutor engine, int priority) {
        ExecutorEntity ent = _map.get(language);
        if (ent != null && ent.priority > priority) {
            return;
        }

        if (language.equals(JtBridge.executorAdapter().defaultExecutor())) {
            _def = engine;
        }

        if (ent == null) {
            ent = new ExecutorEntity().set(language, engine, priority);
            _map.put(language, ent);
        } else {
            ent.set(language, engine, priority);
        }
    }

    /**
     * 删除代码缓存
     */
    public static void del(String name) {
        _map.forEach((k, v) -> {
            v.del(name);
        });
    }

    /**
     * 删除所有代码缓存
     */
    public static void delAll() {
        _map.forEach((k, v) -> {
            v.delAll();
        });
    }

    /**
     * 执行一段代码
     */
    public static Object exec(String language, String code, Map<String, Object> model) throws Exception {
        IJtExecutor tmp = _map.get(language);
        if (tmp != null) {
            return tmp.exec(code, model);
        } else {
            return null;
        }
    }


    /**
     * 执行一个文件并输出（jsx 或 ftl）
     */
    public static void exec(String name, AFileModel file, Context ctx) throws Exception {
        //最后是动态的
        String text = (String) call(name, file, ctx, null, true);
        String call = ctx.param("callback");

        if (ctx.status() == 404) {
            return;
        }

        AFileModel.currentSet(file);

        try {
            ctx.charset("utf-8");

            if (TextUtils.isEmpty(file.content_type) == false) {
                ctx.contentType(file.content_type);
            }

            if (text != null) {
                if (TextUtils.isEmpty(call) == false) {
                    /**
                     * jsonp 的请求支持
                     * */
                    StringBuilder sb = StringUtils.borrowBuilder();
                    sb.append(call).append("(").append(text).append(");");
                    text = StringUtils.releaseBuilder(sb);

                    ctx.contentType("application/x-javascript");
                } else {
                    String contentType = ctx.attr("Content-Type");

                    if (Utils.isNotEmpty(contentType)) {
                        ctx.contentType(contentType);
                    } else {
                        if (TextUtils.isEmpty(file.content_type)) {
                            /**
                             * 如果没有预设content type；则自动检测
                             * */


                            if (text.startsWith("<")) {
                                ctx.contentType("text/html");
                            }

                            if (text.startsWith("{")) {
                                ctx.contentType("application/json");
                            }

                        }
                    }
                }
            }

            if (text != null) {
                ctx.output(text);
            }
        } finally {
            AFileModel.currentRemove();
        }
    }

    /**
     * 纯执行一个js文件（一般用于执行拦截器 或任务）
     */
    public static Object execOnly(AFileModel file, Context ctx) throws Exception {
        IJtExecutor tmp = _map.get(file.edit_mode);

        //最后是动态的
        if (tmp != null) {
            AFileModel.currentSet(file);

            try {
                String path = file.path;
                String name = path.replace("/", "__");

                return tmp.exec(name, file, ctx, null, false);
            } finally {
                AFileModel.currentRemove();
            }
        }

        return null;
    }

    /**
     * 执行一个文件并返回
     */
    public static Object call(String name, AFileModel file, Context ctx, Map<String, Object> model, boolean outString) throws Exception {
        IJtExecutor tmp = _map.get(file.edit_mode);

        try {
            if (tmp != null) {
                return tmp.exec(name, file, ctx, model, outString);
            } else {
                return _def.exec(name, file, ctx, model, outString);
            }
        } catch (Throwable err) {
            //如果出错，输出异常
            if (ctx != null && ctx.status() < 400) {
                String msg = ExceptionUtils.getString(err);
                if (ctx != null) {
                    ctx.output(msg);
                }

                errorLog(file, msg, err);
            }

            return null;
        }
    }
}
