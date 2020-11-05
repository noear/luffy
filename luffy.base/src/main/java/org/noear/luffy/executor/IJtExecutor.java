package org.noear.luffy.executor;

import org.noear.solon.core.XContext;
import org.noear.luffy.model.AFileModel;

import java.util.Map;

public interface IJtExecutor {
    /** 支持语言 */
    String language();

    /** 编译 */
    //String compile(String name, AFileModel file);

    /** 执行文件 */
    Object exec(String name, AFileModel file, XContext ctx, Map<String, Object> model, boolean outString) throws Exception;

    /** 执行代码 */
    default Object exec(String code, Map<String, Object> model) throws Exception{
        return null;
    }


    /** 删除代码缓存 */
    void del(String name);

    /** 删除所有代码缓存 */
    void delAll();

    /** 是否已加载 */
    boolean isLoaded(String name);
    /** 预加载代码 */
    boolean preLoad(String name, AFileModel file) throws Exception;
}
