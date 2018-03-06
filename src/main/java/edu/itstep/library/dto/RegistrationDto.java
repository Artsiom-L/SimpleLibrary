package edu.itstep.library.dto;

import javax.validation.constraints.Size;

public class RegistrationDto {
    @Size(min = 2, max = 32)
    private String login;

    @Size(min = 8)
    private String password;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
