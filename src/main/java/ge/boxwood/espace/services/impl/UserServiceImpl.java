package ge.boxwood.espace.services.impl;

import ge.boxwood.espace.models.*;
import ge.boxwood.espace.models.enums.Status;
import ge.boxwood.espace.repositories.AuthorityRepository;
import ge.boxwood.espace.repositories.UserRepository;
import ge.boxwood.espace.services.NotificationService;
import ge.boxwood.espace.services.UserFileService;
import ge.boxwood.espace.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private UserFileService userFileService;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    public User create(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        List<Authority> authorities = authorityRepository.findAllByIdIn(user.getAuthIds());
        user.setAuthorities(authorities);
        user.setStatus(Status.LOCKED);
        user.setEnabled(true);
        user.setCreatedBy(0L);
        user.setSmsActive(false);
        user.setEmailActive(false);
        user = userRepository.save(user);
        Notification notification = new Notification();
        notification.setUser(user);
        notificationService.create(notification);
        if(user.getImage() != null && !user.getImage().isEmpty()){
            userFileService.create(user.getImage(), user);
        }
        return user;
    }

    @Override
    public void delete(Long id) {
        User user = userRepository.findOne(id);
        user.setStatus(Status.DELETED);
        user.setUpdatedBy(1L);
        userRepository.save(user);
    }

    @Override
    public void destroy(Long id) {
        userRepository.delete(id);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User getByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if(user == null){
            throw new RuntimeException("User not found!");
        }
        else{
            return user;
        }
    }

    @Override
    public User getByPhone(String phone) {
        User user = userRepository.findByPhoneNumber(phone);
        if(user == null){
            throw new RuntimeException("User not found!");
        }
        else{
            return user;
        }
    }

    @Override
    public User getOne(Long id) {
        return userRepository.findOne(id);
    }

    @Override
    public User update(User user, Long id) {
        User raw = userRepository.findOne(id);
        if (user.getOldPassword() != null && !user.getOldPassword().isEmpty() && user.getNewPassword() != null && !user.getNewPassword().isEmpty()){
            customUserDetailsService.changePassword(user.getOldPassword(), user.getNewPassword());
        }
        if(user.getFirstName() != null && raw.getFirstName() != user.getFirstName()){
            raw.setFirstName(user.getFirstName());
        }
        if(user.getLastName() != null && raw.getLastName() != user.getLastName()){
            raw.setLastName(user.getLastName());
        }
        if(user.getPhoneNumber() != null && raw.getPhoneNumber() != user.getPhoneNumber()){
            raw.setPhoneNumber(user.getPhoneNumber());
        }
        if(user.getEmail() != null && raw.getEmail() != user.getEmail()){
            raw.setEmail(user.getEmail());
        }
        if(user.getImage() != null && !user.getImage().isEmpty()){
            UserFile userFile = userFileService.create(user.getImage(), raw);
            raw.setProfilePicture(userFile.getId());
        }

        if(user.getCars() != null && user.getCars().size() > 0){
            List<Car> cars = new ArrayList<>(raw.getCars());
            for (Car car:user.getCars()) {
                if (!raw.getCars().contains(car)){
                    car.setStatus(Status.ACTIVE);
                    cars.add(car);
                }
            }
            raw.setCars(cars);
        }


        return userRepository.save(raw);
    }

    @Override
    public Page<User> findNotDeleted(Page page) {
        return null;
    }

    @Override
    public Boolean checkUsername(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null){
            return false;
        }
        else{
            return true;
        }
    }

    @Override
    public Boolean checkEmail(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null){
            return false;
        }
        else{
            return true;
        }
    }

    @Override
    public Boolean checkPhone(User user) {
        if (userRepository.findByPhoneNumber(user.getPhoneNumber()) != null){
            return false;
        }
        else{
            return true;
        }
    }
}