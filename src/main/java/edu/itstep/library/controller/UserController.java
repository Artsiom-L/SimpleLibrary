package edu.itstep.library.controller;

import edu.itstep.library.dto.RegistrationDto;
import edu.itstep.library.service.SecurityService;
import edu.itstep.library.service.UserService;
import edu.itstep.library.validators.RegistrationValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class UserController {
    private UserService userService;
    private RegistrationValidator registrationValidator;
    private SecurityService securityService;

    public UserController(UserService userService, RegistrationValidator registrationValidator,
                          SecurityService securityService) {
        this.userService = userService;
        this.registrationValidator = registrationValidator;
        this.securityService = securityService;
    }

    @InitBinder
    public void addRegistrationValidator(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(registrationValidator);
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registration")
    public String registrationForm(Model model) {
        model.addAttribute("user", new RegistrationDto());
        return "registration";
    }

    @PostMapping("/registration")
    public String register(@ModelAttribute("user") @Valid RegistrationDto registrationDto,
                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "registration";
        }
        userService.register(registrationDto);
        securityService.autologin(registrationDto.getLogin(), registrationDto.getPassword());
        return "redirect:/";
    }


}
