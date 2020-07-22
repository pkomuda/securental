package pl.lodz.p.it.securental.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.repositories.accounts.AccountRepository;
import pl.lodz.p.it.securental.utils.JwtUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Slf4j
@Component
@AllArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final AccountRepository accountRepository;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional(rollbackFor = ApplicationBaseException.class, propagation = Propagation.REQUIRES_NEW)
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authHeader != null) {
            jwt = authHeader;
            username = jwtUtils.extractUsername(jwt);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (accountRepository.findByUsername(username).isPresent()) {
                User user = new User(username, "", new ArrayList<>());
                boolean valid = jwtUtils.validateToken(jwt, user);
                log.info("VALID: " + valid);
                if (valid) {
                    SecurityContextHolder.getContext().setAuthentication(new CustomAuthenticationToken(username, user.getAuthorities()));
                    chain.doFilter(request, response);
//                    CustomAuthenticationToken authToken = new CustomAuthenticationToken(
//                            username, user.getAuthorities()
//                    );
//                    authToken.setAuthenticated(true);
//                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }


    }
}
