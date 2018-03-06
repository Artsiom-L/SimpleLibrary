package edu.itstep.library.validators;

import edu.itstep.library.dto.RegistrationDto;
import edu.itstep.library.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

//валидатор для формы регстрации
@Component
public class RegistrationValidator implements Validator {
    private UserRepository userRepository;

    public RegistrationValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //поддерживает для валидации только класс RegistrationDto
    @Override
    public boolean supports(Class<?> aClass) {
        return RegistrationDto.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        RegistrationDto registrationDto = RegistrationDto.class.cast(o);
        //если пользователь с таким именем есть, то добавляем ошибку в BindingResult
        if (userRepository.findByUsername(registrationDto.getLogin()) != null) {
            errors.rejectValue("login", "user.exists");
        }
    }
}
