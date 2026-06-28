package tracklistd.api.Integration.FirebaseAuth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import org.springframework.web.servlet.HandlerExceptionResolver;
import tracklistd.api.Entity.User;
import tracklistd.api.Service.FirebaseService;
import tracklistd.api.Service.UserService;

import com.google.firebase.auth.FirebaseToken;

import java.io.IOException;
import java.util.List;

@Component
public class FirebaseFilter extends OncePerRequestFilter {

    private final FirebaseService firebaseService;
    private final UserService userService;
    private final HandlerExceptionResolver exceptionResolver;

    public FirebaseFilter(FirebaseService firebaseService, UserService userService, @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {
        this.firebaseService = firebaseService;
        this.userService = userService;
        this.exceptionResolver = exceptionResolver;
    }

    @Override
    //Coloquei as annotation @NonNull, para remover warnings do intelliJ
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            FirebaseToken decodedToken = firebaseService.verify(token);

            User user = userService.findOrCreateUser(decodedToken);

            String roleName = "ROLE_" + user.getRole().name();
            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(roleName));

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    authorities);

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            // Em vez de escrever a resposta manualmente, delegamos para que o erro caia no método correto da GlobalExceptionHandler
            exceptionResolver.resolveException(request, response, null, new InsufficientAuthenticationException("Token inválido ou expirado", e));
            return;
        }

        filterChain.doFilter(request, response);
    }
}
