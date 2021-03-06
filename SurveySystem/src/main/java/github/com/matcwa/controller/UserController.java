package github.com.matcwa.controller;

import github.com.matcwa.api.dto.UserLoginDto;
import github.com.matcwa.api.dto.UserRegistrationDto;
import github.com.matcwa.infrastructure.error.ResponseResolver;
import github.com.matcwa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pollsystem")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody UserRegistrationDto userRegistrationDto){
        return ResponseResolver.resolve(userService.registerUser(userRegistrationDto));
    }
    @GetMapping("/user/activate/{token}")
    public ResponseEntity activeAccount(@PathVariable String token){
        return ResponseResolver.resolve(userService.activateAccount(token));
    }
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody UserLoginDto userLoginDto){
        return ResponseResolver.resolve(userService.login(userLoginDto));
    }
    @PostMapping("/user/{id}/promoteToAdmin")
    public ResponseEntity promoteToAdmin(@PathVariable Long id,@RequestHeader("Authorization") String token){
        return ResponseResolver.resolve(userService.promoteUserToAdmin(token,id));
    }
    @GetMapping("/user/{username}/promoteToAdmin/{token}")
    public ResponseEntity confirmPromoteToAdmin(@PathVariable String token,@RequestHeader("Authorization") String jwtoken){
        return ResponseResolver.resolve(userService.confirmPromoteUserToAdmin(token,jwtoken));
    }
}
