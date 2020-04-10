package github.com.matcwa.api.jwt;

import github.com.matcwa.api.dto.UserLoginDto;
import github.com.matcwa.model.Role;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {
    private TokenService tokenService = new TokenService();
    private final UserLoginDto USER_LOGIN_DTO = new UserLoginDto("username", "password");
    private final String TOKEN = tokenService.generateTokenFor(USER_LOGIN_DTO);

    @BeforeEach
    void setUp() {
    }

    @Test
    void shouldReturnRoleEqualsUSER() {
        //when
        String roleFromToken = tokenService.getRoleFromToken(TOKEN);
        //then
        assertEquals(Role.USER.name(), roleFromToken);
    }

    @Test
    void shouldReturnUsernameFromToken() {
        //when
        String usernameFromToken = tokenService.getUsernameFromToken(TOKEN);
        //then
        assertEquals(USER_LOGIN_DTO.getUsername(), usernameFromToken);
    }

    @Test
    void shouldFalseWhenTokenIsNoExpiredAndErrorWhenTokenIsExpired() {
        //given
        String expiredToken = Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().minusDays(1)))
                .compact();
        //then
        assertFalse(tokenService.isTokenExpired(TOKEN));
        assertThrows(ExpiredJwtException.class,()->tokenService.isTokenExpired(expiredToken));
    }

}