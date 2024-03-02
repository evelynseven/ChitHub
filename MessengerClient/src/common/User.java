package common;

import java.io.Serializable;

/*
 * @author evelynsun
 */
public class User implements Serializable {
    private String userID;
    private String password;
    private static final long serialVersionUID = 1L;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
