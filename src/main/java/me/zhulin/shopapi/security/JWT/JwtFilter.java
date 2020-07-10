package me.zhulin.shopapi.security.JWT;

import me.zhulin.shopapi.entity.User;
import me.zhulin.shopapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created By Zhu Lin on 1/1/2019.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
    	 System.out.println("doFilterInternal");
    	String jwt = getToken(httpServletRequest);
       
        if (jwt != null && jwtProvider.validate(jwt)) {
            try {
                String userAccount = jwtProvider.getUserAccount(jwt);
                System.out.println("DoFilter_try");
                User user = userService.findOne(userAccount);
                // pwd not necessary
                // if jwt ok, then authenticate
                SimpleGrantedAuthority sga = new SimpleGrantedAuthority(user.getRole());
                ArrayList<SimpleGrantedAuthority> list = new ArrayList<>();
                list.add(sga);
                UsernamePasswordAuthenticationToken auth
                        = new UsernamePasswordAuthenticationToken(user.getEmail(), null, list);
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {
                logger.error("Set Authentication from JWT failed");
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
        System.out.println("DoFilter_filterChain.doFilter");
    }

    private String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        System.out.println("getToken");
        System.out.println(authHeader);
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.replace("Bearer ", "");
        }

        return null;
    }
    public void suman()
    {
    	System.out.println("execute");
    	
    }
}
