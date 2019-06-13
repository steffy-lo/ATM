package users;

import java.io.Serializable;

public abstract class User implements Serializable {
    private String name;
    private String password;

    public User(String name, String pw){
        this.name = name;
        this.password = pw;
    }

    public User(String name){
        this.name = name;
        this.password = generatePassword();
    }

    private String generatePassword(){
        String s = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()_-+=";
        int count = 7;
        StringBuilder password = new StringBuilder();
        while (count-- != 0) {
            int i = (int) (Math.random() * s.length());
            password.append(s.charAt(i));
        }
        return password.toString();
    }

    public boolean verifyPassword(String pw){
        return this.password.equals(pw);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
