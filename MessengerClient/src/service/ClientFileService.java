package service;

import common.Message;
import common.MessageType;

import java.io.*;

/*
 * @author evelynsun
 */
public class ClientFileService {
    public void sendFile (String src, String dest, String senderID, String receiverID) throws IOException {
        Message message = new Message();
        message.setMsgType(MessageType.MESSAGE_SEND_FILE);
        message.setSender(senderID);
        message.setReceiver(receiverID);
        message.setSrc(src);
        message.setDest(dest);

        FileInputStream fis = null;
        byte[] fileBytes = new byte[(int)new File(src).length()];
        try {
            fis = new FileInputStream(src);
            fis.read(fileBytes);
            message.setFileBytes(fileBytes);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            if (fis != null) {
                fis.close();
            }
        }

        System.out.println(senderID + "给" + receiverID + "发送文件" + src + "到对方的电脑目录" + dest);

        ObjectOutputStream oos = new ObjectOutputStream(
                ManageClientConnectServerThread.getClientConnectServerThread(senderID)
                        .getSocket().getOutputStream());
        oos.writeObject(message);
    }
}
