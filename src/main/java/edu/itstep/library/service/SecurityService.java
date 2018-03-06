package edu.itstep.library.service;

import edu.itstep.library.entity.User;

public interface SecurityService {
    User getCurrentUser();
    void autologin(String username, String password);
}
