package github.com.matcwa.api.jwt;

import github.com.matcwa.api.dto.UserLoginDto;
import github.com.matcwa.model.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;

@Service
public class TokenService {

    public String generateTokenFor(UserLoginDto userLoginDto) {
        Date date=new Date();
        String secret = "T9>~_f1=msb8ues%&z)EfcBv=}@A(`";
        return Jwts.builder()
                .setSubject(userLoginDto.getUsername())
                .claim("role", Role.USER.name())
                .setIssuedAt(date)
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(1)))
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();
    }

}
