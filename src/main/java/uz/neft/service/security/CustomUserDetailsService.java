package uz.neft.service.security;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uz.neft.entity.User;
import uz.neft.payload.ApiResponse;
import uz.neft.payload.ApiResponseObject;
import uz.neft.repository.UserRepository;
import uz.neft.secret.JwtTokenProvider;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    UserRepository userRepository;
    JwtTokenProvider jwtTokenProvider;
//    @Autowired
//    DaoAuthenticationProvider authenticationManager;
    @Autowired
    Logger logger;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
//        this.authenticationManager = authenticationManager;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username + " not found"));
    }

    public User loadByUserId(Integer userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("user not found"));

    }

//    public ResToken signIn(SignIn signIn) {
//        try {
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(signIn.getUsername(), signIn.getPassword())
//            );
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            User principal = (User) authentication.getPrincipal();
//            String jwt = jwtTokenProvider.generateToken(principal);
//            return new ResToken(jwt);
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error(e.toString());
//            return null;
//        }
//    }
//
//    public ResToken signInAdminPanel(SignIn signIn) {
//        try {
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(signIn.getUsername(), signIn.getPassword())
//            );
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            User principal = (User) authentication.getPrincipal();
//            for (GrantedAuthority authority : principal.getAuthorities()) {
//                if (Objects.equals(authority.getAuthority(), RoleName.SUPER_ADMIN.name())){
//                    String jwt = jwtTokenProvider.generateToken(principal);
//                    return new ResToken(jwt);
//                }
//            }
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error(e.toString());
//            return null;
//        }
//    }

    public ApiResponse searchUser(String search) {
        return new ApiResponseObject("Ok", true, userRepository.byUsername(search));
    }

//    public ApiResponse all() {
//        return new ApiResponse("Ok", true, userRepository.findAll().stream().map(item -> dtoService.userDto(item)).collect(Collectors.toList()));
//    }
}
