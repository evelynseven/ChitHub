package service;

import common.Message;
import common.MessageType;
import utils.Utility;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Iterator;

/*
 * @author evelynsun
 */
public class SystemNews extends Thread{

    @Override
    public void run() {
        while (true) {
            System.out.println("请输入需要推送的消息(输入exit退出系统消息服务):");
            String news = Utility.readString(200);

            if ("exit".equals(news)) {
                break;
            }

            Message message = new Message();
            message.setMsgType(MessageType.MESSAGE_TO_ALL_MES);
            message.setSender("System");
            message.setContent(news);
            message.setSendTime(new Date().toString());
            System.out.println("==========系统消息==========\n" + news);

            ObjectOutputStream oos;
            Iterator<String> iterator = ManageClientThreads.getHm().keySet().iterator();
            while (iterator.hasNext()) {
                String onlineUserID =  iterator.next();
                try {
                    oos = new ObjectOutputStream(ManageClientThreads.getHm().get(onlineUserID).socket.getOutputStream());
                    oos.writeObject(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
