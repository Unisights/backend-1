package com.unisights.backend.auth;

import com.unisights.backend.config.UserInfoUserDetails;
import com.unisights.backend.security.JwtService;
import com.unisights.backend.user.User;
import com.unisights.backend.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

record SignupReq(String email,String password,String role){}
record LoginReq(String email,String password){}
record LoginRes(String token, List<String> role, Long userId){}
@RestController @RequestMapping("/api/auth")
public class AuthController {



    private final UserRepository repo;

    private final PasswordEncoder enc;


    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController( UserRepository r, PasswordEncoder e, AuthenticationManager authenticationManager, JwtService jwtService){

        this.repo=r; this.enc=e;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

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

//        @PostMapping("/login") public LoginRes login(@RequestBody LoginReq r){
//        var u=repo.findByEmail(r.email()).orElse(null);
//
//        if(u!=null && enc.matches(r.password(),u.getPassword_hash())){
//            var token=jwt.generateToken(u.getRole().name(), u.getId());
//            return new LoginRes(token,u.getRole().name(),u.getId());
//        }
//        throw new RuntimeException("invalid credentials");
//    }

    @PostMapping("/login")
    public LoginRes authenticateAndGetToken(@RequestBody LoginReq authRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.email(), authRequest.password()));
        if (authentication.isAuthenticated()) {

            UserInfoUserDetails principal = (UserInfoUserDetails) authentication.getPrincipal();
            Long userId = principal.getId();
            String email = principal.getUsername();
            List<String> roles = principal.getAuthorities().stream()

                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            // email used as subject
            return new LoginRes(jwtService.generateToken(email, userId,roles), roles, userId);
        } else {
            throw new UsernameNotFoundException("invalid user request !");
        }


    }
}


