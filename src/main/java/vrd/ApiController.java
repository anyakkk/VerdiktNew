package vrd;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vrd.base.*;
import vrd.security.UserService;

import javax.naming.AuthenticationException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.*;

@Controller
public class ApiController {

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
    private UserService userService;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/index")
    public String index(Model model, Principal principal) {
        if (principal != null) {
            Optional<User> user = userRepository.findByUsername(principal.getName());
        }
        model.addAttribute("testId", -1);
        return "home";
    }

    @GetMapping("/test/{id}")
    public String test(@PathVariable("id") Long id, Model model, Principal principal) {
        Optional<User> user = userRepository.findByUsername(principal.getName());
        return "redirect:/test/{id}/run/-1";
    }

    @GetMapping("/test/{id}/edit")
    public String editor(@PathVariable("id") Long id, Model model, Principal principal) {
        return "";
    }

    @GetMapping("/test/{id}/run/{page}")
    public String question(@PathVariable("id") Long id, @PathVariable("page") Integer pageId, Model model, Principal principal) {
        if (pageId == -1) {
            model.addAttribute("testId", id);
            return "home";
        }
        Optional<CurrentTest> currentTest = currentTestRepository.findById(id);
        if (!currentTest.isPresent()) {
            model.addAttribute("currentTestError", "Такого теста не существует");
        } else {
            Optional<CurrentQuestion> currentQuestion = currentQuestionRepository.findByTestAndInTestIndex(currentTest.get(), pageId);
            model.addAttribute("text", currentQuestion.get().getQuest().getText());
            model.addAttribute("variant", true);
            ArrayList<Answer> answers = new ArrayList(currentQuestion.get().getQuest().getAnswers());
            Collections.sort(answers, Comparator.comparing(Answer::getOrder));
            model.addAttribute("answers", answers);
            model.addAttribute("nextQuestionId", pageId+1);
            model.addAttribute("testId", id);
        }
        return "tests";
    }

    @GetMapping("/editor")
    public String creator() {
        return "editortest";
    }

    @PostMapping("/editor")
    public String creat(String code, String name, HttpServletRequest httpServletRequest, Principal principal, Model model) {

        return "editortest";
    }

    @GetMapping("/loadtest")
    String loadtest(HttpServletRequest httpServletRequest, Long id, Model model, Principal principal) {
        Optional<Test> test = testRepository.findById(id);
        Optional<User> user = userRepository.findByUsername(principal.getName());
        return "loadtest";
    }

    @GetMapping("/registration")
    public String FormReg(Model model) {
        model.addAttribute("form", new UserForm());
        return "registration";
    }

    @PostMapping("/registration")
    public String regi(@ModelAttribute("form") UserForm form, Model model) {
        Optional<User> user = userRepository.findByUsername(form.getUsername());
        if (!user.isPresent()) {
            userRepository.save(new User(form.getUsername(), bCryptPasswordEncoder.encode(form.getPassword())));
            Test test = new Test("hi" );
            testRepository.save(test);
            Quest quest1 = new Quest("how are you", test, true);
            questRepository.save(quest1);
            CurrentTest currentTest = new CurrentTest(test);
            currentTestRepository.save(currentTest);
            Answer answer = new Answer((byte) 1, quest1, "good" );
            answerRepository.save(answer);
            CurrentQuestion cquestion1 = new CurrentQuestion(currentTest, 0, quest1);
            currentQuestionRepository.save(cquestion1);
        } else {
            model.addAttribute("usernameError", "Пользователь с таким именем уже существует");
            return "registration";
        }
        if (!form.getPassword().equals(form.getPasswordConfirm())) {
            model.addAttribute("passwordError", true);
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
