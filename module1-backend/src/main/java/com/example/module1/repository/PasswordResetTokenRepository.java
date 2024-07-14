package backend.authModule.repository;

import backend.authModule.entities.AppUser;
import backend.authModule.entities.PasswordResetToken;
import backend.authModule.entities.ProfessionnelSante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);

    PasswordResetToken findByUser(AppUser user);
}
