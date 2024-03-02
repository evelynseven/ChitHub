package service;

import java.util.HashMap;

/*
 * @author evelynsun
 */
public class ManageClientConnectServerThread {
    //管理客户端连接到服务器端的线程
    //把多个线程放到一个HashMap集合中，key就是用户ID，value就是一个线程
    private static HashMap<String, ClientConnectServerThread> hm = new HashMap<>();

    //将某个线程加入到集合中
    public static void addClientConnectServerThread (String userID, ClientConnectServerThread clientConnectServerThread) {
        hm.put(userID, clientConnectServerThread);
    }

    //通过UserID将集合中的线程取出来
    public static ClientConnectServerThread getClientConnectServerThread (String userID) {
        return hm.get(userID);
    }
}
