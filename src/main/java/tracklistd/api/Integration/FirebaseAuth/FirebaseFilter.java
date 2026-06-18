package tracklistd.api.Integration.FirebaseAuth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import tracklistd.api.Dto.User.UserRegisterRequestDTO;
import tracklistd.api.Entity.User;
import tracklistd.api.Repository.UserRepository;
import tracklistd.api.Service.FirebaseService;
import tracklistd.api.Service.UserService;

import com.google.firebase.auth.FirebaseToken;

import java.io.IOException;
import java.util.Collections;

@Component
public class FirebaseFilter extends OncePerRequestFilter {

    private final FirebaseService firebaseService;
    private final UserService userService;

    public FirebaseFilter(FirebaseService firebaseService, UserService userService) {
        this.firebaseService = firebaseService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
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

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    Collections.emptyList());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token inválido ou expirado: " + e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }
}
