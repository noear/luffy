package org.noear.luffy.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class LocalUtil {
    private static String _localIp;
    public static String getLocalIp() {
        if (_localIp == null) {
            try {
                Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                Enumeration ee = null;

                while (en.hasMoreElements()) {
                    ee = ((NetworkInterface) en.nextElement()).getInetAddresses();

                    while (ee.hasMoreElements()) {
                        _localIp = ((InetAddress) ee.nextElement()).getHostAddress();
                        if (!TextUtils.isEmpty(_localIp) && (_localIp.startsWith("192.") || _localIp.startsWith("172.") || _localIp.startsWith("10."))) {
                            return _localIp;
                        }
                    }
                }
            } catch (Exception var3) {
                var3.printStackTrace();
            }
        }

        return _localIp;
    }

    public static String getLocalAddress(int port) {
        String host = null;

        try {
            host = LocalUtil.getLocalIp();
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        return port > 0 ? host + ":" + port : host;
    }
}
