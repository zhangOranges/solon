package org.noear.solon.boot.jetty;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.XPlugin;

import java.io.Closeable;
import java.io.IOException;

public final class XPluginImp implements XPlugin , Closeable {
    private XPlugin _server = null;
    @Override
    public void start(XApp app) {
        long time_start = System.currentTimeMillis();
        System.out.println("oejs.Server:main: Jetty 9.4");

       Class<?> jspClz = XUtil.loadClass("org.eclipse.jetty.jsp.JettyJspServlet");

       XServerProp.init();

       if(jspClz == null){
           _server = new XPluginJetty();
       }else{
           _server = new XPluginJettyJsp();
       }

       _server.start(app);

        long time_end = System.currentTimeMillis();

        System.out.println("oejs.Server:main: Started @" + (time_end - time_start) + "ms");
    }

    @Override
    public void close() throws IOException {
        if (_server != null) {
            ((Closeable) _server).close();
        }
    }
}
