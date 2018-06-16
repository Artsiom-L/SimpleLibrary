package edu.itstep.library.service.impl;

import edu.itstep.library.dto.RegistrationDto;
import edu.itstep.library.entity.User;
import edu.itstep.library.repository.UserRepository;
import edu.itstep.library.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public void register(RegistrationDto registrationDto) {
        User user = new User();
        user.setUsername(registrationDto.getLogin());
        user.setPassword(bCryptPasswordEncoder.encode(registrationDto.getPassword()));
        userRepository.save(user);
    }
}
