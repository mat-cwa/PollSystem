package github.com.matcwa.infrastructure.config;


import github.com.matcwa.api.jwt.JwtFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests().anyRequest().permitAll();
//                .authorizeRequests().antMatchers("api/poll/**").hasAnyRole("USER","ADMIN")
//                .and()
//                .addFilter(new JwtFilter(authenticationManager()));

    }
//    @Override
//    public void configure(WebSecurity webSecurity) throws Exception {
//        webSecurity
//                .ignoring()
//                .anyRequest();
//    }
}
