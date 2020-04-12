package github.com.matcwa.service;

import github.com.matcwa.api.dto.UserDto;
import github.com.matcwa.api.dto.UserLoginDto;
import github.com.matcwa.api.dto.UserRegistrationDto;
import github.com.matcwa.api.error.ErrorHandling;
import github.com.matcwa.api.error.UserError;
import github.com.matcwa.api.jwt.TokenService;
import github.com.matcwa.model.User;
import github.com.matcwa.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private UserRepository userRepository;
    private TokenService tokenService;
    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public UserService(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    @Transactional
    public ErrorHandling<UserDto, UserError> login(UserLoginDto userLoginDto) {
        ErrorHandling<UserDto, UserError> errorHandling = new ErrorHandling<>();
        if (isUsernameAndPasswordNotEmpty(userLoginDto, errorHandling)) {
            userRepository.findByUsername(userLoginDto.getUsername()).ifPresentOrElse(u -> {
                if (checkPassword(userLoginDto.getPassword(), u)) {
                    String token = tokenService.generateTokenFor(userLoginDto);
                    UserDto userDto = modelMapper.map(userLoginDto, UserDto.class);
                    userDto.setToken(token);
                    errorHandling.setDto(userDto);
                } else errorHandling.setError(UserError.WRONG_PASSWORD_ERROR);
            }, () -> errorHandling.setError(UserError.USER_NOT_FOUND_ERROR));
        }
        return errorHandling;
    }

    @Transactional
    public void registration(UserRegistrationDto userRegistrationDto){

    }

    private boolean checkPassword(String passwordToCheck, User user) {
//        if (null == passwordToCheck || !user.getPassword().startsWith("$2a$"))
//            throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");
//        return BCrypt.checkpw(passwordToCheck, user.getPassword()); //todo BCrypt
        return passwordToCheck.equals(user.getPassword());
    }

    private boolean isUsernameAndPasswordNotEmpty(UserLoginDto userLoginDto, ErrorHandling<UserDto, UserError> errorHandling) {
        if ((userLoginDto.getUsername() == null || userLoginDto.getUsername().isEmpty()) && (userLoginDto.getPassword() == null || userLoginDto.getPassword().isEmpty())) {
            errorHandling.setError(UserError.EMPTY_USERNAME_AND_PASSWORD_ERROR);
            return false;
        }
        if (userLoginDto.getUsername() == null || userLoginDto.getUsername().isEmpty()) {
            errorHandling.setError(UserError.EMPTY_USERNAME_ERROR);
            return false;
        }
        if (userLoginDto.getPassword() == null || userLoginDto.getPassword().isEmpty()) {
            errorHandling.setError(UserError.EMPTY_PASSWORD_ERROR);
            return false;
        }
        return true;
    }
}