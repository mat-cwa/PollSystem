package github.com.matcwa.controller;

import github.com.matcwa.api.dto.UserLoginDto;
import github.com.matcwa.infrastructure.ResponseResolver;
import github.com.matcwa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody UserLoginDto userLoginDto){
        return ResponseResolver.resolve(userService.login(userLoginDto));
    }
}
