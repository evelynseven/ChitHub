package common;

/*
 * @author evelynsun
 */
public interface MessageType {
    String MESSAGE_LOGIN_SUCCEED = "1";
    String MESSAGE_LOGIN_FAIL = "2";
    String MESSAGE_COMM_MES = "3";//普通信息包
    String MESSAGE_GET_ONLINE_FRIEND = "4";//要求返回在线用户列表
    String MESSAGE_RET_ONLINE_FRIEND = "5";//返回在线用户列表
    String MESSAGE_CLIENT_EXIT = "6";//客户端请求退出
    String MESSAGE_TO_ALL_MES = "7";//群发消息
    String MESSAGE_SEND_FILE = "8";
}
