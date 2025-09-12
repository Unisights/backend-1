package com.unisights.backend.auth;

import com.unisights.backend.user.User;
import com.unisights.backend.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

record SignupReq(String email,String password,String role){}
record LoginReq(String email,String password){}
@RestController @RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository repo; private final PasswordEncoder enc;
    public AuthController(UserRepository r, PasswordEncoder e){this.repo=r; this.enc=e;}
    @PostMapping("/signup") public String signup(@RequestBody SignupReq r){
        if(repo.findByEmail(r.email()).isPresent()) return "User exists";
        var u=new User(); u.setEmail(r.email()); u.setPassword_hash(enc.encode(r.password())); u.setRole(User.Role.valueOf(r.role()));
        repo.save(u); return "OK";
    }
    @PostMapping("/login") public String login(@RequestBody LoginReq r){
        var u=repo.findByEmail(r.email()).orElse(null);
        return (u!=null && enc.matches(r.password(),u.getPassword_hash()))? "OK":"Invalid";
    }
}

