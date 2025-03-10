package ru.kata.spring.boot_security.demo.controller;


import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RegistrationService;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.*;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;
    private final RegistrationService registrationService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService, RegistrationService registrationService) {
        this.userService = userService;
        this.roleService = roleService;
        this.registrationService = registrationService;

    }

    @GetMapping()
    public String listAllUsers(Model model, @RequestParam(value = "id", required = false) Integer id, Authentication auth) {
        model.addAttribute("users", userService.listAllUsers());
        model.addAttribute("user", new User());
        if (id != null && !id.equals(((User) auth.getPrincipal()).getId())) {
            return "redirect:/admin?id=" + ((User) auth.getPrincipal()).getId();
        }
        if (id != null) {
            model.addAttribute("currentUser", userService.getUserById(id));
        } else {
            model.addAttribute("currentUser", new User());
        }
        model.addAttribute("authId", ((User) auth.getPrincipal()).getId());
        return "admin/index";
    }

    @GetMapping("/getUser")
    @ResponseBody
    public User getUser(@RequestParam int id) {
        return userService.getUserById(id);
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute("user") @Valid User user,
                             @RequestParam(value = "selectedRoleIds") List<Long> selectedRoleIds,
                             Authentication authentication) {
        user.setRoles(roleService.getRolesFromIds(selectedRoleIds));
        userService.updateUser(user);
        return "redirect:/admin?id=" + ((User) authentication.getPrincipal()).getId();
    }


    @PostMapping("/saveUser")
    public String addUser(
            @ModelAttribute("user") @Valid User user,
            @RequestParam(value = "selectedRoleIds") List<Long> selectedRoleIds,
            Authentication authentication) {

        user.setRoles(roleService.getRolesFromIds(selectedRoleIds));
        registrationService.register(user);
        return "redirect:/admin?id=" + ((User) authentication.getPrincipal()).getId();
    }


    @PostMapping("/delete")
    public String deleteUserById(@RequestParam("id") int id, Authentication authentication) {
        userService.deleteUserById(id);
        return "redirect:/admin?id=" + ((User) authentication.getPrincipal()).getId();
    }

    @GetMapping("/user")
    public String getUserPage(Model model, Authentication authentication, @RequestParam("id") Integer id) {
        User user = (User) authentication.getPrincipal();
        model.addAttribute("user", user);
        if (id != null) {
            model.addAttribute("currentUser", userService.getUserById(id));
        } else {
            model.addAttribute("currentUser", new User());
        }
        return "admin/user";
    }
}