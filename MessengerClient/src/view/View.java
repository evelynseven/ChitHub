package view;

import service.ClientFileService;
import service.ClientMessageService;
import service.ClientUserService;
import utils.Utility;

import java.io.IOException;

/*
 * @author evelynsun
 */
public class View {
    private String key = "";
    private boolean loop = true;
    private ClientUserService clientUserService = new ClientUserService();
    private ClientMessageService clientMessageService = new ClientMessageService();
    private ClientFileService clientFileService = new ClientFileService();


    public static void main(String[] args) throws IOException, ClassNotFoundException {
        new View().mainMenu();
        System.out.println("客户端退出系统。");
    }

    private void mainMenu () throws IOException, ClassNotFoundException {
        while (loop) {
            System.out.println("==========欢迎登录网络通讯系统==========");
            System.out.println("\t\t 1 登录系统");
            System.out.println("\t\t 9 退出系统");
            System.out.println("请输入你的选择：");
            key = Utility.readString(1);

            switch (key) {
                case "1":
                    System.out.println("请输入用户号：");
                    String userID = Utility.readString(50);
                    System.out.println("请输入密码：");
                    String password = Utility.readString(50);

                    //这里去服务端校验该用户是否合法
                    if (clientUserService.checkUser(userID, password)) {
                        System.out.println("===============欢迎【用户 " + userID + "】===============");
                        while (loop) {
                            System.out.println("==========网络通讯系统二级菜单【用户" + userID + "】==========");
                            System.out.println("\t\t 1 显示在线用户列表");
                            System.out.println("\t\t 2 群发消息");
                            System.out.println("\t\t 3 私聊消息");
                            System.out.println("\t\t 4 发送文件");
                            System.out.println("\t\t 9 退出系统");
                            System.out.println("请输入你的选择：");

                            key = Utility.readString(1);
                            switch (key) {
                                case "1":
                                    clientUserService.onlineFriendsList();
                                    break;
                                case "2":
                                    System.out.println("请输入群发消息内容：");
                                    String msgToAll = Utility.readString(200);
                                    clientMessageService.sendMsgToAll(userID, msgToAll);
                                    break;
                                case "3":
                                    System.out.println("请输入接收方ID：");
                                    String receiverID = Utility.readString(50);
                                    System.out.println("请输入消息内容：");
                                    String msg = Utility.readString(200);
                                    clientMessageService.privateMessage(userID, receiverID, msg);
                                    break;
                                case "4":
                                    System.out.println("请输入接收方ID：");
                                    String fileReceiver = Utility.readString(50);
                                    System.out.println("请输入文件发送路径：");
                                    String src = Utility.readString(200);
                                    System.out.println("请输入文件接收路径：");
                                    String dest = Utility.readString(200);
                                    //文件路径src: /Users/evelynsun/Downloads/seven.png
                                    //文件路径dest: /Users/evelynsun/Desktop/seven.png
                                    clientFileService.sendFile(src, dest, userID, fileReceiver);
                                    break;
                                case "9":
                                    clientUserService.logOut();
                                    System.out.println("退出系统");
                                    loop = false;
                                    break;
                            }
                        }
                    } else {
                        System.out.println("==========欢迎登录网络通讯系统==========");
                    }
                    break;
                case "9":
                    loop = false;
                    break;
            }
        }
    }
}
