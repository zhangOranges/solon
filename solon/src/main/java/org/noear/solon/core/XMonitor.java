package org.noear.solon.core;

import org.noear.solon.ext.Act2;
import org.noear.solon.ext.Fun1;

/**
 * 监听器（内部类，外部不要使用）
 * */
public class XMonitor {
    private static Act2<XContext,Throwable> _onErrorEvent;
    private static Fun1<XContext,XContext> _onContextEvent;

    public static void sendError(XContext ctx, Throwable err) {
        if (_onErrorEvent != null) {
            try {
                _onErrorEvent.run(ctx, err);
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }

    public static XContext sendContext(XContext ctx){
        if(_onContextEvent == null){
            return ctx;
        }else{
            return _onContextEvent.run(ctx);
        }
    }

    public static void onError(Act2<XContext,Throwable> event) {
        _onErrorEvent = event;
    }

    public static void onContext(Fun1<XContext,XContext> event) {
        _onContextEvent = event;
    }
}
