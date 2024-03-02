package service;

import common.Message;
import common.MessageType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

/*
 * @author evelynsun
 */
public class ClientConnectServerThread extends Thread{
    private Socket socket;
    public ClientConnectServerThread (Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        //因为线程需要在后台和服务器通讯，因此做成while循环
        while (true) {
            //读取从后台得到的消息
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message2 = (Message) ois.readObject();

                if (message2.getMsgType().equals(MessageType.MESSAGE_RET_ONLINE_FRIEND)) {
                    String[] onlineFriends = message2.getContent().split(" ");
                    System.out.println("==========在线用户列表==========");
                    for (int i = 0; i < onlineFriends.length; i++) {
                        System.out.println("用户：" + onlineFriends[i]);
                    }
                    System.out.println("==============================");
                } else if (message2.getMsgType().equals(MessageType.MESSAGE_COMM_MES)) {
                    //打印收到的私聊消息
                    System.out.println("=====" + message2.getSender() + "对" + message2.getReceiver() + "说：=====");
                    System.out.println(message2.getContent());
                    System.out.println(message2.getSendTime());
                    System.out.println("================================");
                } else if (message2.getMsgType().equals(MessageType.MESSAGE_TO_ALL_MES)) {
                    System.out.println("=====" + message2.getSender() + "对大家说：=====");
                    System.out.println(message2.getContent());
                    System.out.println(message2.getSendTime());
                    System.out.println("================================");
                } else if (message2.getMsgType().equals(MessageType.MESSAGE_SEND_FILE)) {
                    System.out.println(message2.getSender() + "给" + message2.getReceiver() + "发送文件" + message2.getSrc() + "到对方的电脑目录" + message2.getDest());
                    FileOutputStream fos = new FileOutputStream(message2.getDest());
                    fos.write(message2.getFileBytes());
                    fos.close();
                    System.out.println("保存文件成功。");
                } else {
                    System.out.println("收到了其他类型的message");
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
