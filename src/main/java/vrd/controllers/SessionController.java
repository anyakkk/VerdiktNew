package vrd.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import vrd.DTO.ResultDTO;
import vrd.TestGenerator;
import vrd.base.*;
import vrd.compiler.Analizator;
import vrd.repository.*;
import vrd.security.UserService;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Optional;

@Controller
public class SessionController {

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

    @GetMapping("/session/{id}")
    public String session(@PathVariable(value = "id", required = false) Long id, Model model, Principal principal) {
        if (id == null)
            return "redirect:/index";

        model.addAttribute("sessionId", id.toString());

        Optional<User> user = userRepository.findByUsername(principal.getName());
        if (user.get().getRole().equals("TEACHER")) {
            Optional<Test> test = testRepository.findById(id);
            model.addAttribute("testName", test.get().getName());
            model.addAttribute("name", test.get().getName() + " : " + new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date()));
            model.addAttribute("sessionTime", 90);
            model.addAttribute("mins", 45);
            model.addAttribute("readonly", false);
            return "sessionEditor";
        }
        if (user.get().getRole().equals("STUDENT")) {
            Optional<Session> session = sessionRepository.findById(id);
            if (session.isPresent()) {
                Optional<UserApp> userApp = userAppRepository.findBySessionAndUser(session.get(), user.get());
                if (userApp.isPresent()) {
                    switch (userApp.get().getStatus()) {
                        case WAITING:
                            model.addAttribute("isAwait", true);
                            model.addAttribute("request", userApp.get().getId());
                            return "studentHome";

                        case CANCELED:
                            model.addAttribute("canceled", true);
                            return "studentHome";

                        case ACCEPTED:
                            if (!session.get().getStarted()) {
                                model.addAttribute("isAwait", true);
                                return "studentHome";
                            }
                            return "redirect:/test/" + session.get().getTest().getId() + "?session=" + id;
                    }

                } else if (session.get().checkOpen()) {
                    UserApp newUserApp = new UserApp();
                    newUserApp.setUser(user.get());
                    newUserApp.setSession(session.get());
                    newUserApp.setStatus(StatusApp.WAITING);
                    userAppRepository.save(newUserApp);
                    model.addAttribute("isAwait", true);
                    model.addAttribute("request", newUserApp.getId());
                    return "studentHome";
                } else  {
                    model.addAttribute("notFound", true);
                    return "studentHome";
                }

            } else {
                model.addAttribute("notFound", true);
                return "studentHome";
            }

        }

        return "error";
    }

    @GetMapping("/currentSession")
    public String currentSession(@RequestParam("session") Long id,
                                 @RequestParam(name = "testId", required=false) Long testId,
                                 @RequestParam(name = "name", required=false) String name,
                                 @RequestParam(name = "minutes", required=false) Integer minutes,
                                 @RequestParam(name = "accept", required=false) Long accept,
                                 @RequestParam(name = "cancel", required=false) Long cancel,
                                 @RequestParam(name = "startSession", required=false) Boolean start,
                                 @RequestParam(name = "endSession", required=false) Boolean end,
                                 @RequestParam(name = "sessionTime", required = false) Integer sessionTime,
                                 Model model, Principal principal) {

        Optional<User> user = userRepository.findByUsername(principal.getName());
        if (accept != null) {
            Optional<UserApp> userApp = userAppRepository.findById(accept);
            if (userApp.isPresent()) {
                userApp.get().setStatus(StatusApp.ACCEPTED);
                userAppRepository.save(userApp.get());
            }
        }


        if (cancel != null) {
            Optional<UserApp> userApp = userAppRepository.findById(cancel);
            if (userApp.isPresent()) {
                userApp.get().setStatus(StatusApp.CANCELED);
                userAppRepository.save(userApp.get());
            }
        }
//        Optional<Session> session = sessionRepository.findById(id);
//        if (!session.isPresent()) {
//            return "/error";
//        }
        Session session;
        if (id == -1) {
            if (testId == null)
                return "error";
            Optional<Test> test = testRepository.findById(testId);
            if (!test.isPresent())
                return "error";

            session = new Session();
            session.setTest(test.get());
            session.setNameSession(name);
            session.setTime(minutes);
            session.setStarted(false);
            session.setDateStart(new Date());
            session.setSessionTime(sessionTime);
            session.setUser(user.get());

            session = sessionRepository.save(session);
            return "redirect:/currentSession?session="+session.getId();
        } else {
            Optional<Session> found = sessionRepository.findById(id);
            if (found.isPresent()) {
                session = found.get();
            } else
                return "error";
        }
        if ((start != null) && start) {
            session.setStarted(true);
            session = sessionRepository.save(session);
            return "redirect:/currentSession?session="+session.getId();
        }
        if ((end != null) && end) {
            session.closeSession();
            session = sessionRepository.save(session);
            return "redirect:/currentSession?session="+session.getId();
        }

        ArrayList<UserApp> apps = new ArrayList<>(session.getUserApps());
        apps.sort(Comparator.comparing(UserApp::getStatus));
        model.addAttribute("sessionId", id);
        model.addAttribute("mins", session.getTime());
        model.addAttribute("test", session.getTest());
        model.addAttribute("userApps", apps);
        model.addAttribute("name", session.getNameSession());
        model.addAttribute("sessionTime", session.getSessionTime());
        model.addAttribute("readonly", true);
        model.addAttribute("started", session.getStarted());
        model.addAttribute("ended", !session.checkOpen());
        ArrayList<ResultDTO> resultDTOS = new ArrayList<>();

        for (UserApp userApp: apps) {
            switch (userApp.getStatus()) {
                case ACCEPTED:
                {
                    Optional<CurrentTest> test = currentTestRepository.findBySessionAndUser(session, userApp.getUser());
                    if (test.isPresent()) {
                        if (test.get().checkOpen()) {
                            resultDTOS.add(new ResultDTO(null, "Решается", userApp.getUser().getUsername()));
                            break;
                        }
                        int correct = 0;
                        for (CurrentQuestion question: test.get().getQuestions()) {
                            if (question.getAnswer() != null) {
                                Optional<Answer> answer = answerRepository.findByQuestAndOrder(question.getQuest(), question.getAnswer());
                                if (answer.isPresent() && answer.get().isCorrect())
                                    ++correct;
                            }
                        }
                        resultDTOS.add(new ResultDTO("/test/" + test.get().getId() + "/results/-1?session="+session.getId(),
                                correct + " / " + test.get().getQuestions().size(),
                                userApp.getUser().getUsername()));
                        break;
                    }
                }
                case CANCELED:
                case WAITING:
                    resultDTOS.add(new ResultDTO(null, "Отклонено", userApp.getUser().getUsername()));
                    break;
            }
        }
        model.addAttribute("userResults", resultDTOS);

        return "sessionEditor";
    }
}
