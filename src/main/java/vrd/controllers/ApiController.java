package vrd.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vrd.DTO.SessionDTO;
import vrd.TestGenerator;
import vrd.UserForm;
import vrd.base.*;
import vrd.compiler.Analizator;
import vrd.repository.*;
import vrd.security.UserService;

import java.security.Principal;
import java.text.SimpleDateFormat;
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

    @GetMapping("/")
    public String index() {
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String index(Model model, UserForm form, Principal principal) {
        Optional<User> user = userRepository.findByUsername(principal.getName());
        if (user.get().getRole().equals("TEACHER")) {
            return "redirect:/loadtest";
        }
        if (user.get().getRole().equals("STUDENT")) {
            return "studentHome";
        }
        return "error";
    }

    @GetMapping("/status")
    ResponseEntity<Boolean> currentStatus(@RequestParam (name = "session") Long id, @RequestParam (name = "count", required=false) Integer count, Principal principal) {
        Optional<User> user = userRepository.findByUsername(principal.getName());
        Optional<Session> session = sessionRepository.findById(id);
        if (!session.isPresent())
             return ResponseEntity.ok(true);
        if (user.get().getRole().equals("TEACHER") && (count != null))
            return ResponseEntity.ok(session.get().getUserApps().size() != count);
        Optional<UserApp> userApp = userAppRepository.findBySessionAndUser(session.get(), user.get());
        boolean exists = userApp.isPresent();
        boolean notWaiting = userApp.get().getStatus() != StatusApp.WAITING ;
        boolean readyToEnterSession = (session.get().getStarted() || (userApp.get().getStatus() != StatusApp.ACCEPTED)) ;

        return ResponseEntity.ok(!exists || (notWaiting && readyToEnterSession));
    }

    @GetMapping("/stat")
    public String stat(Principal principal, Model model) {
        Optional<User> user = userRepository.findByUsername(principal.getName());
        ArrayList<Session> sessions = new ArrayList<Session>(user.get().getSessions());
        sessions.sort(Comparator.comparing(Session::getDateStart));

        ArrayList<SessionDTO> sessionDTOS = new ArrayList<>();
        sessions.forEach(session -> sessionDTOS.add(new SessionDTO(session.getNameSession(),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(session.getDateStart()),
                "/currentSession?session=" + session.getId()
        )));
        model.addAttribute("results", sessionDTOS);

        return "listtests";
    }
}

