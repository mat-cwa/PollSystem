package github.com.matcwa.service;

import github.com.matcwa.api.dto.UserDto;
import github.com.matcwa.api.dto.UserLoginDto;
import github.com.matcwa.api.dto.UserRegistrationDto;
import github.com.matcwa.api.error.ErrorHandling;
import github.com.matcwa.api.error.TokenError;
import github.com.matcwa.api.error.UserError;
import github.com.matcwa.api.jwt.TokenService;
import github.com.matcwa.api.mapper.UserMapper;
import github.com.matcwa.model.enums.TokenType;
import github.com.matcwa.model.entity.Token;
import github.com.matcwa.model.entity.User;
import github.com.matcwa.repository.TokenRepository;
import github.com.matcwa.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCrypt;

import static org.mockito.BDDMockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenService tokenService;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private EmailService emailService;
    @InjectMocks
    UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void shouldReturnUsernameAndEmailIsEmptyError(){
        //given
        UserRegistrationDto emptyUsername=new UserRegistrationDto("","password","taken@email.com");
        UserRegistrationDto emptyPassword=new UserRegistrationDto("anyuser",null,"any@email.com");
        //when
        ErrorHandling<UserDto,UserError> emptyUsernameResponse=userService.registerUser(emptyUsername);
        ErrorHandling<UserDto,UserError> emptyPasswordResponse=userService.registerUser(emptyPassword);
        //then
        assertNull(emptyUsernameResponse.getDto());
        assertEquals(emptyUsernameResponse.getError(),UserError.EMPTY_USERNAME_ERROR);
        assertNull(emptyPasswordResponse.getDto());
        assertEquals(emptyPasswordResponse.getError(),UserError.EMPTY_PASSWORD_ERROR);

    }
    @Test
    void shouldReturnUserAlreadyExistsError(){
        //given
        UserRegistrationDto takenUsername=new UserRegistrationDto("user","password","taken@email.com");
        UserRegistrationDto takenEmail=new UserRegistrationDto("anyuser","password","any@email.com");
        UserRegistrationDto takenEmailAndUsername=new UserRegistrationDto("takenuser","password","taken2@email.com");
        User user=new User("user","password");
        given(userRepository.findByUsername("user")).willReturn(Optional.of(user));
        given(userRepository.findByUsername("anyuser")).willReturn(Optional.empty());
        given(userRepository.findByEmail("any@email.com")).willReturn(Optional.of(user));
        given(userRepository.findByEmail("taken@email.com")).willReturn(Optional.empty());
        given(userRepository.findByUsername("takenuser")).willReturn(Optional.of(user));
        given(userRepository.findByEmail("taken2@email.com")).willReturn(Optional.of(user));
        //when
        ErrorHandling<UserDto,UserError> usernameAlreadyExistsResponse=userService.registerUser(takenUsername);
        ErrorHandling<UserDto,UserError> emailAlreadyExistsResponse=userService.registerUser(takenEmail);
        ErrorHandling<UserDto,UserError> emailAndUsernameAlreadyExistsResponse=userService.registerUser(takenEmailAndUsername);
        //then
        assertNull(usernameAlreadyExistsResponse.getDto());
        assertEquals(usernameAlreadyExistsResponse.getError(),UserError.USERNAME_ALREADY_EXISTS);
        assertNull(emailAlreadyExistsResponse.getDto());
        assertEquals(emailAlreadyExistsResponse.getError(),UserError.EMAIL_ALREADY_EXISTS);
        assertNull(emailAndUsernameAlreadyExistsResponse.getDto());
        assertEquals(emailAndUsernameAlreadyExistsResponse.getError(),UserError.USERNAME_AND_EMAIL_ALREADY_EXISTS);
    }
    @Test
    void shouldCreateNewUser(){
        //give
        UserRegistrationDto newUser=new UserRegistrationDto("anylogin","password","anyemail@email.com");
        User user=new User(newUser.getUsername(),BCrypt.hashpw(newUser.getPassword(),BCrypt.gensalt()),newUser.getEmail());
        ArgumentCaptor<User> userCaptor=ArgumentCaptor.forClass(User.class);
        given(userRepository.findByUsername("anylogin")).willReturn(Optional.empty());
        given(userRepository.findByEmail("anyemail@email.com")).willReturn(Optional.empty());
        willDoNothing().given(emailService).sendRegistrationEmailTo(anyString(),anyString());
        //when
        ErrorHandling<UserDto, UserError> response = userService.registerUser(newUser);
        //then
        verify(userRepository,times(1)).save(userCaptor.capture());
        assertNull(response.getError());
        assertEquals(user,userCaptor.getValue());
        assertEquals(response.getDto(), UserMapper.toDto(user));

    }
    @Test
    void shouldCreateNewToken(){
        //give
        UserRegistrationDto newUser=new UserRegistrationDto("anylogin","password","anyemail@email.com");
        User user=new User(newUser.getUsername(),BCrypt.hashpw(newUser.getPassword(),BCrypt.gensalt()),newUser.getEmail());
        Token token=new Token("anyvalue",user, TokenType.REGISTRATION,true);
        ArgumentCaptor<Token> tokenCaptor=ArgumentCaptor.forClass(Token.class);
        //when
        ErrorHandling<UserDto, UserError> response = userService.registerUser(newUser);
        //then
        verify(tokenRepository,times(1)).save(tokenCaptor.capture());
        assertNull(response.getError());
        assertEquals(tokenCaptor.getValue(),token);
        assertEquals(response.getDto(), UserMapper.toDto(user));
    }
    @Test
    void shouldReturnTokenNotFoundError(){
        //given
        when(tokenRepository.findByValue("token")).thenReturn(Optional.empty());
        //when
        ErrorHandling<UserDto, TokenError> response = userService.activateAccount("token");
        //then
        assertNull(response.getDto());
        assertEquals(response.getError(),TokenError.TOKEN_NOT_FOUND_ERROR);
    }

    @Test
    void shouldReturnTokenIsInactiveError(){
        //given
        Token token=new Token("token",null,TokenType.REGISTRATION,false);
        when(tokenRepository.findByValue("token")).thenReturn(Optional.of(token));
        //when
        ErrorHandling<UserDto, TokenError> response = userService.activateAccount("token");
        //then
        assertNull(response.getDto());
        assertEquals(response.getError(),TokenError.TOKEN_IS_INACTIVE);
    }

    @Test
    void shouldActivateAccount(){
        //given
        User user=new User();
        user.setActive(true);
        Token token=new Token("anyValue",user,TokenType.REGISTRATION,true);
        when(tokenRepository.findByValue("anyValue")).thenReturn(Optional.of(token));
        //when
        ErrorHandling<UserDto, TokenError> response = userService.activateAccount("anyValue");
        //then
        assertNull(response.getError());
        assertEquals(UserMapper.toDto(user),response.getDto());
    }

    @Test
    void shouldReturnUserDtoAndNullError() {
        //given
        UserLoginDto userLoginDto = new UserLoginDto("login", "anyPassword");
        User user = new User("login",BCrypt.hashpw("anyPassword",BCrypt.gensalt()));
        user.setActive(true);
        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));
        given(tokenService.generateTokenFor(userLoginDto)).willReturn("token");
        //when
        ErrorHandling<UserDto, UserError> login = userService.login(userLoginDto);
        //then
        assertEquals(login.getDto(), UserMapper.toDto(user));
        assertNull(login.getError());
    }
    @Test
    void shouldReturnNullUserDtoAndUserInactiveError() {
        //given
        UserLoginDto userLoginDto = new UserLoginDto("login", "anyPassword");
        User user = new User("login",BCrypt.hashpw("anyPassword",BCrypt.gensalt()));
        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));
        //when
        ErrorHandling<UserDto, UserError> login = userService.login(userLoginDto);
        //then
        assertEquals(login.getError(), UserError.USER_INACTIVE_ERROR);
        assertNull(login.getDto());
    }

    @Test
    void shouldReturnNullUserDtoAndWrongPasswordError() {
        //given
        UserLoginDto userLoginDto = new UserLoginDto("login","anyPassword");
        User user = new User();
        user.setUsername("login");
        user.setPassword(BCrypt.hashpw("wrongPassword",BCrypt.gensalt()));
        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));
        //when
        ErrorHandling<UserDto, UserError> login = userService.login(userLoginDto);
        //then
        assertEquals(login.getError(), UserError.WRONG_PASSWORD_ERROR);
        assertNull(login.getDto());
    }

    @Test
    void shouldReturnNullUserDtoAndUserNotFoundError() {
        //given
        UserLoginDto userLoginDto = new UserLoginDto("login", "anyPassword");
        given(userRepository.findByUsername(any())).willReturn(Optional.empty());
        //when
        ErrorHandling<UserDto, UserError> login = userService.login(userLoginDto);
        //then
        assertNull(login.getDto());
        assertEquals(login.getError(), UserError.USER_NOT_FOUND_ERROR);
    }

    @Test
    void shouldReturnNullUserDtoAndEmptyUsernameError() {
        //given
        UserLoginDto userLoginDto = new UserLoginDto("", "anyPassword");
        UserLoginDto userLoginDto2 = new UserLoginDto(null, "anyPassword");
        //when
        ErrorHandling<UserDto, UserError> login = userService.login(userLoginDto);
        ErrorHandling<UserDto, UserError> login2 = userService.login(userLoginDto2);
        //then
        assertNull(login.getDto());
        assertNull(login2.getDto());
        assertEquals(login.getError(), UserError.EMPTY_USERNAME_ERROR);
        assertEquals(login2.getError(), UserError.EMPTY_USERNAME_ERROR);
    }

    @Test
    void shouldReturnNullUserDtoAndEmptyPasswordError() {
        //given
        UserLoginDto userLoginDto = new UserLoginDto("login", "");
        UserLoginDto userLoginDto2 = new UserLoginDto("login", null);
        //when
        ErrorHandling<UserDto, UserError> login = userService.login(userLoginDto);
        ErrorHandling<UserDto, UserError> login2 = userService.login(userLoginDto2);
        //then
        assertNull(login.getDto());
        assertNull(login2.getDto());
        assertEquals(login.getError(), UserError.EMPTY_PASSWORD_ERROR);
        assertEquals(login2.getError(), UserError.EMPTY_PASSWORD_ERROR);
    }

    @Test
    void shouldReturnNullUserDtoAndEmptyUsernameAndPasswordError() {
        //given
        UserLoginDto userLoginDto = new UserLoginDto("", "");
        UserLoginDto userLoginDto2 = new UserLoginDto(null, null);
        UserLoginDto userLoginDto3 = new UserLoginDto("", null);
        UserLoginDto userLoginDto4 = new UserLoginDto(null, "");
        //when
        List<ErrorHandling<UserDto, UserError>> response = Arrays.asList(userService.login(userLoginDto), userService.login(userLoginDto2), userService.login(userLoginDto3), userService.login(userLoginDto4));
        //then
        response.forEach(error -> assertEquals(error.getError(), UserError.EMPTY_USERNAME_AND_PASSWORD_ERROR));
        response.forEach(error -> assertNull(error.getDto()));
    }
}
