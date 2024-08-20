package com.rs.cancel.config;

import com.nimbusds.jose.JOSEException;
import com.rs.cancel.model.User;
import com.rs.cancel.reponsitory.UserReponsitory;
import com.rs.cancel.service.CustomUserDetailsService;
import com.rs.cancel.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.CachingUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.ParseException;
import java.util.UUID;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private JwtTokenProvider jwtTokenProvider;

    private UserDetailsService userDetailsService;

    private UserReponsitory userReponsitory;



//    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService userDetailsService) {
//        this.jwtTokenProvider = jwtTokenProvider;
//        this.userDetailsService = userDetailsService;
//    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Get JWT token from HTTP request
        String token = getTokenFromRequest(request);

        // Validate Token
        try {
            if(StringUtils.hasText(token)&& jwtTokenProvider.checkToken(token) ){
                // get username from token
                String username ;
                try {
                    username = jwtTokenProvider.extractUsername(token);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                } catch (JOSEException e) {
                    throw new RuntimeException(e);
                }
               UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

                if (requestAttributes != null) {
                    requestAttributes.setAttribute("requestId", UUID.randomUUID().toString(), RequestAttributes.SCOPE_REQUEST);
                    requestAttributes.setAttribute("userId", userReponsitory.findByUsernameOrEmail(username,username).orElse(null).getId(), RequestAttributes.SCOPE_REQUEST);
                    requestAttributes.setAttribute("username", username, RequestAttributes.SCOPE_REQUEST);
                    requestAttributes.setAttribute("timestamp", System.currentTimeMillis(), RequestAttributes.SCOPE_REQUEST);
                }
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }


        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");

        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7, bearerToken.length());
        }

        return null;

    }
}
