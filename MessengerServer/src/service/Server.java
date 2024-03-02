package service;

import common.Message;
import common.MessageType;
import common.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/*
 * @author evelynsun
 * 监听9999端口，等待客户端的连接，并保持通讯
 */
public class Server {
    private ServerSocket serverSocket;
    private static ConcurrentHashMap<String, User> validUsers = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, ArrayList<Message>> offlineMsg = new ConcurrentHashMap<>();

    static {
         validUsers.put("admin", new User("admin", "12345"));
         validUsers.put("evelyn", new User("evelyn", "77777"));
         validUsers.put("mark", new User("mark", "99999"));
    }

    //验证用户是否有效的方法
    public boolean checkUser (String userID, String password) {
        User user = validUsers.get(userID);
        if (user == null) {
            System.out.println("该用户不存在");
            return false;
        } else if (!(user.getPassword().equals(password))) {
            System.out.println("密码不正确");
            return false;
        } else {
            return true;
        }
    }

    //验证用户是否在线
    public static boolean checkOnline (String userID) {
        Boolean isOnline = false;
        String[] onlineUsers = ManageClientThreads.getOnlineUsers().split(" ");
        for (int i = 0; i < onlineUsers.length; i++) {
            if (userID.equals(onlineUsers[i])) {
                isOnline = true;
            }
        }
        return isOnline;
    }

    //将不在线用户和收到的消息放到offlineMsg
    public static void setOfflineMsg (String receiverID, ArrayList<Message> msgs) {
        offlineMsg.put(receiverID, msgs);
    }

    //获取离线消息集合
    public static ArrayList<Message> getOfflineMsg (String receiverID) {
        return offlineMsg.get(receiverID);
    }

    //将发送完的消息移除
    public static void rmOfflineMsg (String receiverID) {
        offlineMsg.remove(receiverID);
    }

    //检查是否有离线消息
    public static boolean checkOfflineMsg (String receiverID) {
        Boolean hasOffMsg = false;
        Set<String> userIDs = offlineMsg.keySet();
        Iterator iterator = userIDs.iterator();
        while (iterator.hasNext()) {
            String next = (String) iterator.next();
            if (receiverID.equals(next)) {
                hasOffMsg = true;
            }
        }
        return hasOffMsg;
    }

    public Server() {
        //端口其实可以写在一个配置文件中
        try {
            System.out.println("服务端在9999端口监听");
            new Thread(new SystemNews()).start();
            serverSocket = new ServerSocket(9999);

            while (true) {
                Socket socket = serverSocket.accept();
                ObjectInputStream ois =
                        new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

                User user = (User) ois.readObject();

                Message message = new Message();

                if (checkUser(user.getUserID(),user.getPassword())) {
                    //说明用户合法，登录成功
                    message.setMsgType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    oos.writeObject(message);

                    //创建一个线程，和客户端保持通讯，该线程需要持有socket对象
                    ServerConnectClientThread serverConnectClientThread = new ServerConnectClientThread(socket, user.getUserID());
                    serverConnectClientThread.start();

                    //把该线程对象放到一个集合进行管理
                    ManageClientThreads.addClientThread(user.getUserID(), serverConnectClientThread);

                } else {
                    //用户合法登陆失败
                    System.out.println("用户登录失败");
                    message.setMsgType(MessageType.MESSAGE_LOGIN_FAIL);
                    oos.writeObject(message);
                    socket.close();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
