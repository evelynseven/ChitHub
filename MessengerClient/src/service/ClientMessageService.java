package service;

import common.Message;
import common.MessageType;
import common.User;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/*
 * @author evelynsun
 */
public class ClientMessageService {
    //发送私聊消息
    public void privateMessage (String senderID, String receiverID, String msg) throws IOException {
        Message message = new Message();
        message.setMsgType(MessageType.MESSAGE_COMM_MES);
        message.setSender(senderID);
        message.setReceiver(receiverID);
        message.setContent(msg);
        message.setSendTime(new java.util.Date().toString());

        System.out.println("=====" + message.getSender() + "对" + message.getReceiver() + "说：=====");
        System.out.println(message.getContent());
        System.out.println(message.getSendTime());
        System.out.println("================================");

        ObjectOutputStream oos = new ObjectOutputStream(
                ManageClientConnectServerThread.getClientConnectServerThread(senderID)
                        .getSocket().getOutputStream());
        oos.writeObject(message);
    }

    public void sendMsgToAll (String senderID, String msgToAll) throws IOException {
        Message message = new Message();
        message.setMsgType(MessageType.MESSAGE_TO_ALL_MES);
        message.setSender(senderID);
        message.setContent(msgToAll);
        message.setSendTime(new java.util.Date().toString());

        System.out.println("=====" + message.getSender() + "对大家说：=====");
        System.out.println(message.getContent());
        System.out.println(message.getSendTime());
        System.out.println("================================");

        ObjectOutputStream oos = new ObjectOutputStream(
                ManageClientConnectServerThread.getClientConnectServerThread(senderID)
                        .getSocket().getOutputStream());
        oos.writeObject(message);
    }

}
