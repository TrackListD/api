package tracklistd.api.Controller;

import static org.mockito.Mockito.when;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tracklistd.api.Dto.Feed.PublicationFeedDTO;
import tracklistd.api.Dto.User.UserMinResponseDTO;
import tracklistd.api.Entity.User;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Entity.Enums.Role;
import tracklistd.api.Integration.FirebaseAuth.FirebaseFilter;
import tracklistd.api.Integration.FirebaseAuth.Config.SecurityConfig;
import tracklistd.api.Service.FeedService;
import tracklistd.api.Service.FirebaseService;
import tracklistd.api.Service.UserService;

@WebMvcTest(controllers = FeedController.class, includeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                                SecurityConfig.class,
                                FirebaseFilter.class
                })
})
class FeedControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private FeedService feedService;

        @MockitoBean
        private FirebaseService firebaseService;

        @MockitoBean
        private UserService userService;

        private User mockUser;
        private PublicationFeedDTO samplePublication;
        private UsernamePasswordAuthenticationToken mockAuth;

        @BeforeEach
        void setUp() {
                mockUser = new User("João Pedro", "auth0|123456", Role.MEMBER, Privacy.PUBLIC);
                mockAuth = new UsernamePasswordAuthenticationToken(mockUser, null, Collections.emptyList());

                UserMinResponseDTO authorMin = new UserMinResponseDTO(2L, "Autor Teste", "imagem-teste");

                samplePublication = new PublicationFeedDTO(
                                10L,
                                "Excelente Álbum!",
                                "RATING",
                                4.5f,
                                LocalDateTime.now(),
                                authorMin,
                                5L,
                                2,
                                true,
                                null,
                                false,
                                null,
                                null);
        }

        @Test
        void shouldReturnSocialFeedForAuthenticatedUser() throws Exception {
                List<PublicationFeedDTO> mockFeed = List.of(samplePublication);

                // CORREÇÃO: O service espera receber o ID (Long) do usuário, e não o mockAuth
                when(feedService.getSocialFeed(mockUser.getId())).thenReturn(mockFeed);

                mockMvc.perform(get("/api/feed/me")
                                .with(authentication(mockAuth))
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(10L))
                                .andExpect(jsonPath("$[0].content").value("Excelente Álbum!"))
                                .andExpect(jsonPath("$[0].author.name").value("Autor Teste"))
                                .andExpect(jsonPath("$[0].likedByMe").value(true));
        }

        @Test
        void shouldReturnGlobalFeedForAuthenticatedUser() throws Exception {
                List<PublicationFeedDTO> mockFeed = List.of(samplePublication);
                when(feedService.getGlobalFeed(mockUser.getId())).thenReturn(mockFeed);

                mockMvc.perform(get("/api/feed/global")
                                .with(authentication(mockAuth))
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(10L))
                                .andExpect(jsonPath("$[0].likedByMe").value(true));
        }

        @Test
        void shouldReturnGlobalFeedForAnonymousUser() throws Exception {
                UserMinResponseDTO authorMin = new UserMinResponseDTO(2L, "Autor Teste", "imagem-teste");
                PublicationFeedDTO anonymousPublication = new PublicationFeedDTO(
                                10L, "Excelente Álbum!", "RATING", 4.5f, LocalDateTime.now(),
                                authorMin, 5L, 2, false, null, false, null, null);

                List<PublicationFeedDTO> mockFeed = List.of(anonymousPublication);
                when(feedService.getGlobalFeed(null)).thenReturn(mockFeed);

                mockMvc.perform(get("/api/feed/global")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(10L))
                                .andExpect(jsonPath("$[0].likedByMe").value(false));
        }
}