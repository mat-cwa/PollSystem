package github.com.matcwa.service;

import github.com.matcwa.api.dto.UserDto;
import github.com.matcwa.api.dto.UserLoginDto;
import github.com.matcwa.api.error.ErrorHandling;
import github.com.matcwa.api.error.UserError;
import github.com.matcwa.api.jwt.TokenService;
import github.com.matcwa.model.User;
import github.com.matcwa.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenService tokenService;
    @InjectMocks
    UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void shouldReturnUserDtoAndNullError() {
        //given
        UserLoginDto userLoginDto = new UserLoginDto("login", "anyPassword");
        User user = new User();
        user.setUsername("login");
        user.setPassword("anyPassword");
        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));
        //when
        ErrorHandling<UserDto, UserError> login = userService.login(userLoginDto);
        //then
        assertEquals(login.getDto().getUsername(), userLoginDto.getUsername());
        assertNull(login.getError());
    }

    @Test
    void shouldReturnNullUserDtoAndNullError() {
        //given
        UserLoginDto userLoginDto = new UserLoginDto("login", "anyPassword");
        User user = new User();
        user.setUsername("login");
        user.setPassword("wrongPassword"); //anyPassword
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
