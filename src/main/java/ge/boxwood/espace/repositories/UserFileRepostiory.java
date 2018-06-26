package ge.boxwood.espace.repositories;

import ge.boxwood.espace.models.User;
import ge.boxwood.espace.models.UserFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserFileRepostiory extends JpaRepository<UserFile, Long> {
    List<UserFile> findAllByUser(User user);
}
