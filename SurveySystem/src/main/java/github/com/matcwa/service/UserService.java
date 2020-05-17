package github.com.matcwa.service;

import github.com.matcwa.api.dto.SuccessResponseDto;
import github.com.matcwa.api.dto.UserDto;
import github.com.matcwa.api.dto.UserLoginDto;
import github.com.matcwa.api.dto.UserRegistrationDto;
import github.com.matcwa.api.error.ErrorHandling;
import github.com.matcwa.api.error.TokenError;
import github.com.matcwa.api.error.UserError;
import github.com.matcwa.api.jwt.TokenService;
import github.com.matcwa.api.mapper.UserMapper;
import github.com.matcwa.model.enums.Role;
import github.com.matcwa.model.enums.TokenType;
import github.com.matcwa.model.entity.Token;
import github.com.matcwa.model.entity.User;
import github.com.matcwa.repository.TokenRepository;
import github.com.matcwa.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private UserRepository userRepository;
    private TokenService tokenService;
    private TokenRepository tokenRepository;
    private EmailService emailService;
    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public UserService(UserRepository userRepository, TokenService tokenService, TokenRepository tokenRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }

    @Transactional
    public ErrorHandling<UserDto, UserError> registerUser(UserRegistrationDto newUser) {
        ErrorHandling<UserDto, UserError> response = new ErrorHandling<>();
        if (!isRegistersCredentialsEmpty(newUser, response)) {
            if (!isUsernameAndEmailAlreadyExists(newUser, response)) {
                User user = modelMapper.map(newUser, User.class);
                user.setPassword(BCrypt.hashpw(newUser.getPassword(), BCrypt.gensalt()));
                userRepository.save(user);
                Token token = new Token(UUID.randomUUID().toString(), user, TokenType.REGISTRATION, true);
                tokenRepository.save(token);
                emailService.sendRegistrationEmailTo(newUser.getEmail(), token.getValue());
                response.setDto(UserMapper.toDto(user));
            }
        }
        return response;
    }

    @Transactional
    public ErrorHandling<UserDto, TokenError> activateAccount(String token) {
        ErrorHandling<UserDto, TokenError> response = new ErrorHandling<>();
        tokenRepository.findByValue(token).ifPresentOrElse(tokenByValue -> {
            if (tokenByValue.isActive()) {
                tokenByValue.getOwner().setActive(true);
                userRepository.save(tokenByValue.getOwner());
                tokenByValue.setConfirmed(true);
                tokenByValue.setActive(false);
                tokenRepository.save(tokenByValue);
                response.setDto(UserMapper.toDto(tokenByValue.getOwner()));
            } else response.setError(TokenError.TOKEN_IS_INACTIVE);
        }, () -> response.setError(TokenError.TOKEN_NOT_FOUND_ERROR));
        return response;
    }

    @Transactional
    public ErrorHandling<UserDto, UserError> login(UserLoginDto userLoginDto) {
        ErrorHandling<UserDto, UserError> response = new ErrorHandling<>();
        if (!isUsernameOrPasswordEmpty(userLoginDto, response)) {
            userRepository.findByUsername(userLoginDto.getUsername()).ifPresentOrElse(u -> {
                if (checkPassword(userLoginDto.getPassword(), u)) {
                    if (u.isActive()) {
                        String token = tokenService.generateTokenFor(userLoginDto);
                        UserDto userDto = modelMapper.map(u, UserDto.class);
                        userDto.setToken(token);
                        response.setDto(userDto);
                    } else response.setError(UserError.USER_INACTIVE_ERROR);
                } else response.setError(UserError.WRONG_PASSWORD_ERROR);
            }, () -> response.setError(UserError.USER_NOT_FOUND_ERROR));
        }
        return response;
    }

    public ErrorHandling<UserDto, UserError> promoteUserToAdmin(String token, Long userId) {
        ErrorHandling<UserDto, UserError> response = new ErrorHandling<>();
        if (tokenService.getRoleFromToken(token).equals(Role.ADMIN.name())) {
            userRepository.findById(userId).ifPresentOrElse(user -> {
                if (!user.getRole().equals(Role.ADMIN)) {
                    userRepository.findByUsername(tokenService.getUsernameFromToken(token)).ifPresentOrElse(promoter -> {
                        Token promoteToken = new Token(UUID.randomUUID().toString(), promoter, TokenType.PERMISSION, true);
                        tokenRepository.save(promoteToken);
                        emailService.sendPromoteToAdminEmailTo(promoter.getEmail(),promoteToken.getValue(),user);
                        response.setDto(UserMapper.toDto(user));
                    }, () -> response.setError(UserError.PROMOTER_NOT_FOUND));
                } else {
                    response.setError(UserError.ADMIN_ROLE_ALREADY_EXIST);
                }
            }, () -> response.setError(UserError.USER_NOT_FOUND_ERROR));
        } else {
            response.setError(UserError.AUTHORIZATION_ERROR);
        }
        return response;
    }


    private boolean checkPassword(String passwordToCheck, User user) {
        if (null == passwordToCheck || !user.getPassword().startsWith("$2a$"))
            throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");
        return BCrypt.checkpw(passwordToCheck, user.getPassword());
    }

    private boolean isRegistersCredentialsEmpty(UserRegistrationDto newUser, ErrorHandling<UserDto, UserError> errorHandling) {

        if (newUser.getUsername() == null || newUser.getUsername().isEmpty()) {
            errorHandling.setError(UserError.EMPTY_USERNAME_ERROR);
            return true;
        }
        if (newUser.getPassword() == null || newUser.getPassword().isEmpty()) {
            errorHandling.setError(UserError.EMPTY_PASSWORD_ERROR);
            return true;
        }
        if (newUser.getEmail() == null || newUser.getEmail().isEmpty()) {
            errorHandling.setError(UserError.EMPTY_EMAIL_ERROR);
            return true;
        }
        return false;
    }

    private boolean isUsernameOrPasswordEmpty(UserLoginDto userLoginDto, ErrorHandling<UserDto, UserError> errorHandling) {
        if ((userLoginDto.getUsername() == null || userLoginDto.getUsername().isEmpty()) && (userLoginDto.getPassword() == null || userLoginDto.getPassword().isEmpty())) {
            errorHandling.setError(UserError.EMPTY_USERNAME_AND_PASSWORD_ERROR);
            return true;
        }
        if (userLoginDto.getUsername() == null || userLoginDto.getUsername().isEmpty()) {
            errorHandling.setError(UserError.EMPTY_USERNAME_ERROR);
            return true;
        }
        if (userLoginDto.getPassword() == null || userLoginDto.getPassword().isEmpty()) {
            errorHandling.setError(UserError.EMPTY_PASSWORD_ERROR);
            return true;
        }
        return false;
    }

    private boolean isUsernameAndEmailAlreadyExists(UserRegistrationDto newUser, ErrorHandling<?, UserError> response) {
        Optional<User> userByUsername = userRepository.findByUsername(newUser.getUsername());
        Optional<User> userByEmail = userRepository.findByEmail(newUser.getEmail());

        if (!userByUsername.isPresent() && userByEmail.isPresent()) {
            response.setError(UserError.EMAIL_ALREADY_EXISTS);
            return true;
        }
        if (userByUsername.isPresent() && !userByEmail.isPresent()) {
            response.setError(UserError.USERNAME_ALREADY_EXISTS);
            return true;
        }
        if (userByUsername.isPresent() && userByEmail.isPresent()) {
            response.setError(UserError.USERNAME_AND_EMAIL_ALREADY_EXISTS);
            return true;
        }
        return false;
    }


}