package service;

import common.Message;
import common.MessageType;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/*
 * @author evelynsun
 * 该类的一个对象和某个客户端保持通讯
 */
public class ServerConnectClientThread extends Thread{
    Socket socket;
    String userID;
    //建一个离线消息集合
    ArrayList<Message> offLineMsgs = new ArrayList<>();//这里可能位置不太对
    public ServerConnectClientThread(Socket socket, String userID) {
        this.socket = socket;
        this.userID = userID;
    }

    @Override
    public void run() {
        //线程发送和接收消息
        while (true) {
            try {
                System.out.println("服务端和客户端保持通讯，读取数据...");

                //检查当前用户是否有离线消息
                System.out.println("=====系统检测是否有离线消息=====");
                if (Server.checkOfflineMsg(userID)) {
                    System.out.println(userID + "有离线消息");
                    //发送离线消息
                    Iterator<Message> offlineMsg = Server.getOfflineMsg(userID).iterator();
                    while (offlineMsg.hasNext()) {
                        Message offlineMessage =  offlineMsg.next();
                        ObjectOutputStream oos = new ObjectOutputStream(ManageClientThreads.getClientThread(userID)
                                .socket.getOutputStream());
                        oos.writeObject(offlineMessage);
                    }
                    Server.rmOfflineMsg(userID);
                } else {
                    System.out.println(userID + "没有离线消息");
                }
                System.out.println("=========系统检测完毕=========");

                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();
                Message message2 = new Message();
                ObjectOutputStream oos;

                if (message.getMsgType().equals(MessageType.MESSAGE_GET_ONLINE_FRIEND)) {
                    //获取用户列表
                    String onlineUsers = ManageClientThreads.getOnlineUsers();
                    message2.setMsgType(MessageType.MESSAGE_RET_ONLINE_FRIEND);
                    message2.setContent(onlineUsers);
                    message2.setReceiver(message.getSender());
                    System.out.println("在线用户列表接收者：" + message2.getReceiver());

                    //返回给客户端
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message2);

                } else if (message.getMsgType().equals(MessageType.MESSAGE_CLIENT_EXIT)) {
                    //关闭线程
                    System.out.println(message.getSender() + " 退出系统");
                    ManageClientThreads.rmClientThread(message.getSender());
                    socket.close();//关闭连接
                    break;

                } else if (message.getMsgType().equals(MessageType.MESSAGE_COMM_MES)) {

                    //在线用户直接发送消息
                    if (Server.checkOnline(message.getReceiver())) {
                        //切换到接收方的socket
                        oos = new ObjectOutputStream(ManageClientThreads.getClientThread(message.getReceiver()).socket.getOutputStream());
                        //给接收方发送私聊消息
                        oos.writeObject(message);
                    } else {//不在线的话，将userID和消息放入HashMap
                        offLineMsgs.add(message);
                        Server.setOfflineMsg(message.getReceiver(), offLineMsgs);
                    }

                } else if (message.getMsgType().equals(MessageType.MESSAGE_TO_ALL_MES)) {
                    Iterator<String> iterator = ManageClientThreads.getHm().keySet().iterator();
                    while (iterator.hasNext()) {
                        String onlineUserID =  iterator.next();
                        if (!message.getSender().equals(onlineUserID)) {
                            socket = ManageClientThreads.getHm().get(onlineUserID).socket;
                            oos = new ObjectOutputStream(socket.getOutputStream());
                            oos.writeObject(message);
                        }
                    }
                } else if (message.getMsgType().equals(MessageType.MESSAGE_SEND_FILE)) {
                    oos = new ObjectOutputStream(ManageClientThreads.getClientThread(message.getReceiver()).socket.getOutputStream());
                    oos.writeObject(message);
                }

            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
