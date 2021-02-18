package org.noear.solon.extend.sessionstate.jwt;

import org.noear.solon.Solon;

class XServerProp {
    public static int request_maxRequestSize = 1024 * 1024 * 2;//默认2mb
    public static int session_timeout = 0;
    public static String session_state_domain;
    public static boolean session_state_domain_auto;

    public static String session_state_signKey;
    public static boolean session_state_requestUseHeader;
    public static boolean session_state_responseUseHeader;

    public static void init() {
        String tmp = Solon.cfg().get("server.request.maxRequestSize", "").trim().toLowerCase();//k数
        if (tmp.endsWith("mb")) {
            int val = Integer.parseInt(tmp.substring(0, tmp.length() - 2));
            request_maxRequestSize = val * 1204 * 1204;
        }

        if (tmp.endsWith("kb")) {
            int val = Integer.parseInt(tmp.substring(0, tmp.length() - 2));
            request_maxRequestSize = val * 1204;
        }

        session_timeout = Solon.cfg().getInt("server.session.timeout", 0);
        session_state_domain = Solon.cfg().get("server.session.state.domain");
        session_state_domain_auto = Solon.cfg().getBool("server.session.state.domain.auto", true);

        session_state_signKey = Solon.cfg().get("server.session.state.signKey");
        session_state_requestUseHeader = Solon.cfg().getBool("server.session.state.requestUseHeader", false);
        session_state_responseUseHeader = Solon.cfg().getBool("server.session.state.responseUseHeader", false);
    }
}
