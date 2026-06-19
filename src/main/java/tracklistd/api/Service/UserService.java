package tracklistd.api.Service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseToken;

import tracklistd.api.Dto.User.UserRegisterRequestDTO;
import tracklistd.api.Dto.User.UserUpdatePerfilRequestDTO;
import tracklistd.api.Entity.User;
import tracklistd.api.Exceptions.ResourceNotFoundException;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Entity.Enums.Role;
import tracklistd.api.Repository.UserRepository;
import tracklistd.api.Entity.Report;
import tracklistd.api.Entity.Enums.Punishment;
import tracklistd.api.Entity.Enums.ReportStatus;
import tracklistd.api.Entity.Enums.ModerationStatus;
import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ReportService reportService;

    public UserService(UserRepository userRepository, ReportService reportService) {
        this.userRepository = userRepository;
        this.reportService = reportService;
    }

    @Transactional
    public User register(UserRegisterRequestDTO dto) {
        if (userRepository.existsByIdLoginApi((dto.idLoginApi())))
            throw new RuntimeException("Usuário já está cadastrado no sistema!");

        User newUser = new User();
        newUser.setName(dto.name());
        newUser.setIdLoginApi(dto.idLoginApi());
        newUser.setRole(dto.role());
        newUser.setWhoCanComment(dto.whoCanComment());
        newUser.setBio(dto.bio());

        return userRepository.save(newUser);
    }

    @Transactional
    public User perfilUpdate(Long id, UserUpdatePerfilRequestDTO dto) {
        User perfil = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + id));

        if (dto.name() != null)
            perfil.setName(dto.name());
        if (dto.bio() != null)
            perfil.setBio(dto.bio());
        if (dto.whoCanComment() != null)
            perfil.setWhoCanComment(dto.whoCanComment());

        return userRepository.save(perfil);
    }

    public void followUser(Long myId, Long friendId) {
        if (myId.equals(friendId))
            throw new RuntimeException("Você não pode seguir você mesmo!");

        User me = userRepository.findById(myId)
                .orElseThrow(() -> new RuntimeException("Seu usuário não foi encontrado."));

        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Usuário que você deseja seguir não foi encontrado."));

        if (!me.getFollowing().contains(friend)) {
            me.getFollowing().add(friend);

            userRepository.save(me);
        }
    }

    public void unfollowUser(Long myId, Long friendId) {
        if (myId.equals(friendId))
            throw new RuntimeException("Você não pode deixar de seguir você mesmo!");

        User me = userRepository.findById(myId)
                .orElseThrow(() -> new RuntimeException("Seu usuário não foi encontrado."));

        User friend = userRepository.findById(friendId)
                .orElseThrow(
                        () -> new RuntimeException("Usuário que você deseja deixar de seguir não foi encontrado."));

        if (me.getFollowing().contains(friend)) {
            me.getFollowing().remove(friend);

            userRepository.save(me);
        }
    }

    public User findUserById(Long id)
    {
        return this.userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Esse Usuario não foi encontrado")
        );
    }

  
    @Transactional
    public User findOrCreateUser(FirebaseToken decodedToken) {
        return userRepository.findByIdLoginApi(decodedToken.getUid())
                .orElseGet(() -> {
                    UserRegisterRequestDTO dto = new UserRegisterRequestDTO(decodedToken.getName(),
                            decodedToken.getUid(), Role.MEMBER, Privacy.PUBLIC, "");
                    return register(dto);
                });
    }

    public Report moderateReport(Long adminID, Long reportID, ReportStatus status, Punishment punishment){
        User adimin = userRepository.findById(adminID).orElseThrow(
                () -> new RuntimeException("Administrador não encontrado.")
        );
        if(adimin.getRole() != Role.ADMIN)
            throw new RuntimeException("Acesso negado: apenas administradores podem moderar denúncias.");

        Report report = reportService.resolveReport(reportID, status, punishment);

        if(status == ReportStatus.RESOLVED){
            User userTarget = null;

            if(report.getCommentTarget() != null){
                report.getCommentTarget().setModerationStatus(ModerationStatus.OCULT);
                userTarget = report.getCommentTarget().getAuthorPublication();
            }
            else if(report.getRatingTarget() != null){
                report.getRatingTarget().setStatus(ModerationStatus.OCULT);
                userTarget = report.getRatingTarget().getAuthorPublication();
            }
            else if(report.getUserTarget() != null){
                userTarget = report.getUserTarget();
            }

            if(userTarget != null){
                switch (punishment){
                    case WARNING:
                        break;
                    case TEMPORARY_SUSPENSION:
                        userTarget.setModerationStatus(ModerationStatus.SUSPENDED);
                        userTarget.setSuspensionEndDate(LocalDateTime.now().plusDays(7));//padrão 7 dias
                        //Deve ser feito uma maneira de receber o tempo de suspensão?
                        break;
                    case ACCOUNT_DELETION:
                        userTarget.setModerationStatus(ModerationStatus.BANNED);
                        //devemos deletar a conta?
                        break;
                }
            }
        }

        return report;
    }
}
