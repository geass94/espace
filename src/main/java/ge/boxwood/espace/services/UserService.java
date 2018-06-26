package ge.boxwood.espace.services;

import ge.boxwood.espace.models.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    User create(User user);
    void delete(Long id);
    User getOne(Long id);
    List<User> getAll();
    User getByUsername(String username);
    User getByEmail(String email);
    User getByPhone(String phone);
    User update(User user,Long id);
    Page<User> findNotDeleted(Page page);
    Boolean checkUsername(User user);
    Boolean checkEmail(User user);
    Boolean checkPhone(User user);
}

