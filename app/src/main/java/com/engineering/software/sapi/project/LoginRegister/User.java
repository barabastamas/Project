package com.engineering.software.sapi.project.LoginRegister;

/**
 * Created by Atti Zold on 2015. 11. 14..
 */
public class User {
    String email,password;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
