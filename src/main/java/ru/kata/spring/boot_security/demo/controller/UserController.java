package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;


@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String getUserPage(Model model, Authentication authentication, @RequestParam("id") Integer id) {
        User currentUser = (User) authentication.getPrincipal();
        model.addAttribute("user", currentUser);

        if (id != null && !id.equals(currentUser.getId())) {
            return "redirect:/user?id=" + ((User) authentication.getPrincipal()).getId();
        }

        if (id != null) {
            model.addAttribute("currentUser", userService.getUserById(id));
        } else {
            model.addAttribute("currentUser", new User());
        }

        return "/user/user"; // Возвращаем шаблон
    }
}