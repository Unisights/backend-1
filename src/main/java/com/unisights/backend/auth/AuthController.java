package com.unisights.backend.auth;

import com.unisights.backend.security.JwtUtil;
import com.unisights.backend.user.User;
import com.unisights.backend.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

record SignupReq(String email,String password,String role){}
record LoginReq(String email,String password){}
record LoginRes(String token,String role,Long userId){}
@RestController @RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwt;

    private final UserRepository repo;

    private final PasswordEncoder enc;

    public AuthController(JwtUtil jwt, UserRepository r, PasswordEncoder e){
        this.jwt = jwt;
        this.repo=r; this.enc=e;}

    @PostMapping("/signup") public String signup(@RequestBody SignupReq r){
        if(repo.findByEmail(r.email()).isPresent()) return "User exists";
        var u=new User(); u.setEmail(r.email()); u.setPassword_hash(enc.encode(r.password())); u.setRole(User.Role.valueOf(r.role()));
        repo.save(u); return "OK";
    }
//    @PostMapping("/login") public String login(@RequestBody LoginReq r){
//        var u=repo.findByEmail(r.email()).orElse(null);
//
//        return (u!=null && enc.matches(r.password(),u.getPassword_hash()))? "OK":"Invalid";
//    }

//    @PostMapping("/login")
//    public LoginRes login(@RequestBody LoginReq r){
//        // for MVP: hardcode 2 users
//        if(r.email().equals("student@demo.com")){
//            return new LoginRes(jwt.generateToken("STUDENT",1L),"STUDENT",1L);
//        }
//        if(r.email().equals("consult@demo.com")){
//            return new LoginRes(jwt.generateToken("CONSULTANT",100L),"CONSULTANT",100L);
//        }
//        throw new RuntimeException("invalid credentials");
//    }

        @PostMapping("/login") public LoginRes login(@RequestBody LoginReq r){
        var u=repo.findByEmail(r.email()).orElse(null);

        if(u!=null && enc.matches(r.password(),u.getPassword_hash())){
            var token=jwt.generateToken(u.getRole().name(), u.getId());
            return new LoginRes(token,u.getRole().name(),u.getId());
        }
        throw new RuntimeException("invalid credentials");
    }
}


