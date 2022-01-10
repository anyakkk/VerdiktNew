package vrd.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import vrd.TestGenerator;
import vrd.UserForm;
import vrd.base.*;
import vrd.compiler.Analizator;
import vrd.repository.*;
import vrd.security.UserService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Controller
public class UserController {

    @Autowired
    public UserRepository userRepository;
    @Autowired
    public TestRepository testRepository;
    @Autowired
    public QuestRepository questRepository;
    @Autowired
    public AnswerRepository answerRepository;
    @Autowired
    public CurrentTestRepository currentTestRepository;
    @Autowired
    public CurrentQuestionRepository currentQuestionRepository;
    @Autowired
    public Analizator analizator;
    @Autowired
    TestGenerator testGenerator;
    @Autowired
    SessionRepository sessionRepository;
    @Autowired
    UserAppRepository userAppRepository;

    @Autowired
    private UserService userService;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/registration")
    public String FormReg(Model model, Boolean isTeacher) {
        model.addAttribute("form", new UserForm());
        return "registration";
    }

    @PostMapping("/registration")
    public String regi(@ModelAttribute("form") UserForm form, Model model) {
        Optional<User> user = userRepository.findByUsername(form.getUsername());
        if (!form.getPassword().equals(form.getPasswordConfirm())) {
            model.addAttribute("passwordError", true);
            return "registration";
        }
        if (!user.isPresent()) {
            userRepository.save(new User(form.getUsername(), bCryptPasswordEncoder.encode(form.getPassword()), form.getIsTeacher() ? "TEACHER" : "STUDENT"));
        } else {
            model.addAttribute("usernameError", "Пользователь с таким именем уже существует");
            return "registration";
        }
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest httpServletRequest) throws ServletException {
        httpServletRequest.logout();
        return "redirect:/login";
    }

}
