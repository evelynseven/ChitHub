package service;

import java.util.HashMap;
import java.util.Iterator;

/*
 * @author evelynsun
 */
public class ManageClientThreads {
    public static HashMap<String, ServerConnectClientThread> getHm() {
        return hm;
    }

    private static HashMap<String, ServerConnectClientThread> hm = new HashMap<>();

    public static void addClientThread(String userID, ServerConnectClientThread serverConnectClientThread){
        hm.put(userID, serverConnectClientThread);
    }

    public static void rmClientThread(String userID) {
        hm.remove(userID);
    }

    public static ServerConnectClientThread getClientThread (String userID) {
        return hm.get(userID);
    }

    //返回在线用户列表
    public static String getOnlineUsers () {
        String list = "";
        Iterator<String> iterator = hm.keySet().iterator();
        while (iterator.hasNext()) {
            String next =  iterator.next();
            list += next;
            list += " ";
        }
        return list;
    }


}
