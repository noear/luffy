package org.noear.luffy.utils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * @author noear
 */
public class PingUtils {
    public static void ping(String address) throws Exception {
        ping(address, 2000);
    }

    /**
     * Ping 一个地址
     *
     * @param address （例：192.168.1.1 或 192.168.1.1:8080）
     */
    public static void ping(String address, int millis) throws Exception {
        if (address.contains(":")) {
            String host = address.split(":")[0];
            int port = Integer.parseInt(address.split(":")[1]);

            try (Socket socket = new Socket()) {
                SocketAddress addr = new InetSocketAddress(host, port);
                socket.connect(addr, millis);
            }
        } else {
            InetAddress.getByName(address).isReachable(millis);
        }
    }
}
