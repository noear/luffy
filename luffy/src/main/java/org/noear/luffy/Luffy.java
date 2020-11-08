package org.noear.luffy;

import org.noear.luffy.dso.*;
import org.noear.solon.XApp;
import org.noear.solon.core.*;
import org.noear.luffy.utils.ExceptionUtils;
import org.noear.luffy.utils.TextUtils;
import org.noear.weed.ext.Act0;
import org.noear.weed.xml.XmlSqlLoader;

public class Luffy {
    private static Act0 _onLoadEvent;

    public static void onLoad(){
        if(_onLoadEvent != null){
            try {
                _onLoadEvent.run();
            }catch (Throwable ex){
                ex.printStackTrace();
            }
        }
    }

    public static XApp start(Class<?> source, String[] args){
        return start(source,args,null);
    }

    public static XApp start(Class<?> source, String[] args, Act0 onLoadEvent) {
        Luffy._onLoadEvent = onLoadEvent;

        XmlSqlLoader.tryLoad();

        //0.构建参数
        XMap xarg = XMap.from(args);

        //1.获取扩展目录
        String extend = InitUtil.tryInitExtend(xarg);

        if (extend == null) {
            throw new RuntimeException("Please enter an 'extend' parameter!");
        }

        //2.初始化扩展目录（包括：配置、jar）
        ExtendUtil.init(extend);
        xarg.put("extend", extend);

        InitXfunUtil.init();

        //3.初始化执行器工厂
        JtBridge.executorAdapterSet(JtAdapter.global);
        JtBridge.configAdapterSet(JtAdapter.global);

        //4.启动服务
        XApp app = XApp.start(source, xarg, (x) -> {

            String def_exec = x.prop().get("luffy.executor.default");
            if(TextUtils.isEmpty(def_exec) == false){
                JtAdapter.global.defaultExecutorSet(def_exec);
            }

            x.sharedAdd("XFun", JtFun.g);
            x.sharedAdd("XMsg", JtMsg.g);
            x.sharedAdd("XUtil", JtUtilEx.g2);
            x.sharedAdd("XLock", JtLock.g);

            //不再支持
            x.sharedAdd("XBus", JtMsg.g);//为兼容旧版本
        }).onError((err)->{
            String txt = ExceptionUtils.getString(err);
            LogUtil.log("XAPP",LogLevel.ERROR, txt);
        });

        //4.1.加载自己的bean
        app.beanScan(Luffy.class);

        //5.初始化功能
        if (app.prop().size() < 4) {
            //5.1.如果没有DB配置；则启动配置服务
            AppUtil.runAsInit(app, extend);
        } else {
            //5.2.如果有DB配置了；则启动工作服务
            AppUtil.init(app,true);

            AppUtil.runAsWork(app);
        }

        return app;
    }
}
