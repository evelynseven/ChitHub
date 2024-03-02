package service;

import common.Message;
import common.MessageType;
import common.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/*
 * @author evelynsun
 */
public class ClientUserService {
    //该类完成用户登录验证注册等功能
    private User user = new User();

    private Socket socket;
    private Message message = new Message();

    public boolean checkUser (String userID, String password) throws IOException, ClassNotFoundException {
        boolean result = false;

        user.setUserID(userID);
        user.setPassword(password);


        //向服务器发送进行校验
        socket = new Socket(InetAddress.getLocalHost(), 9999);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(user);

        //读取服务端回送的信息
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        Message message2 = (Message) ois.readObject();

        //对回送消息的处理
        if (message2.getMsgType().equals(MessageType.MESSAGE_LOGIN_SUCCEED)) {
            //创建一个和服务器端保持通讯的线程，并启动
            ClientConnectServerThread clientConnectServerThread =
                    new ClientConnectServerThread(socket);
            clientConnectServerThread.start();

            //把线程放在一个集合里面去管理
            ManageClientConnectServerThread.addClientConnectServerThread(userID, clientConnectServerThread);

            result = true;
        } else {
            //登录失败，就不能启动和服务器通信的线程，此时需要关闭socket
            socket.close();
        }
        return result;
    }

    //向服务端请求在线用户列表
    public void onlineFriendsList () throws IOException {
        message.setMsgType(MessageType.MESSAGE_GET_ONLINE_FRIEND);
        message.setSender(user.getUserID());
        ObjectOutputStream oos = new ObjectOutputStream(
                ManageClientConnectServerThread.getClientConnectServerThread(user.getUserID())
                        .getSocket().getOutputStream());
        oos.writeObject(message);
    }

    //向服务端发送退出系统的消息
    public void logOut () throws IOException {
        message.setMsgType(MessageType.MESSAGE_CLIENT_EXIT);
        message.setSender(user.getUserID());
        ObjectOutputStream oos = new ObjectOutputStream(
                ManageClientConnectServerThread.getClientConnectServerThread(user.getUserID())
                        .getSocket().getOutputStream());
        oos.writeObject(message);
        System.out.println(user.getUserID() + " 退出系统");
        System.exit(0);
    }

}
