package ru.kata.spring.boot_security.demo.initializer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import ru.kata.spring.boot_security.demo.service.RegistrationService;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@Component
public class Init {
    private final RegistrationService registrationService;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Autowired
    public Init(RegistrationService registrationService, RoleRepository roleRepository, UserRepository userRepository) {
        this.registrationService = registrationService;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @PostConstruct
    @Transactional
    public void init() {
        Role adminRole = roleRepository.findRoleByName("ROLE_ADMIN").orElseGet(() -> {
            Role role = new Role("ROLE_ADMIN");
            role.setId(2L);
            return roleRepository.save(role);
        });

        Role userRole = roleRepository.findRoleByName("ROLE_USER").orElseGet(() -> {
            Role role = new Role("ROLE_USER");
            role.setId(1L);
            return roleRepository.save(role);
        });

        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(adminRole);
        adminRoles.add(userRole);

        Set<Role> userRoles = new HashSet<>();
        userRoles.add(userRole);

        if (userRepository.findByUsername("Admin").isEmpty()) {
            registrationService.register(new User("Admin", "adminEmail@gmail.com", "123", adminRoles));
        }

        if (userRepository.findByUsername("User ").isEmpty()) {
            registrationService.register(new User("User", "userEmail@gmail.com", "123", userRoles));
        }
    }
}



