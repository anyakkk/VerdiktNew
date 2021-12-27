package vrd;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vrd.DTO.ResultDTO;
import vrd.DTO.SessionDTO;
import vrd.base.*;
import vrd.compiler.Analizator;
import vrd.compiler.Reader;
import vrd.security.UserService;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

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

    @GetMapping("/test/{id}")
    public String test(@PathVariable("id") Long id, @RequestParam(value = "session", required = true) Long sessionId, Model model, Principal principal) {
        Optional<Test> test = testRepository.findById(id);
        Optional<User> user = userRepository.findByUsername(principal.getName());
        Optional<Session> session = sessionRepository.findById(sessionId);

        if (!session.isPresent() || (!test.isPresent()))
            return "redirect:/error";

        Optional<UserApp> userApp = userAppRepository.findBySessionAndUser(session.get(), user.get());

        if (!userApp.isPresent() || userApp.get().getStatus() != StatusApp.ACCEPTED)
            return "redirect:/error";

        Optional<CurrentTest> current = currentTestRepository.findBySessionAndUser(session.get(), user.get());
        if (current.isPresent()) {
            if (current.get().checkOpen()) {
                return "redirect:/test/" + current.get().getId() + "/run/-1";
            }
            model.addAttribute("notFound", true);
            return "studentHome";
        }

        if (!session.get().checkOpen()) {
            model.addAttribute("notFound", true);
            return "studentHome";
        }

        Long testId = testGenerator.generateCurrentTest(user.get(), test.get(), session.get());
        return "redirect:/test/" + testId + "/run/-1";
    }

    @GetMapping("/test/{id}/send/{page}")
    ResponseEntity<String> sending(@PathVariable("id") Long id,
                                   @PathVariable("page") Integer pageId,
                                   @RequestParam(name = "data") String data,
                                   Principal principal) {
        Optional<User> user = userRepository.findByUsername(principal.getName());
        Optional<CurrentTest> currentTest = currentTestRepository.findById(id);

        if (!currentTest.isPresent())
            return ResponseEntity.notFound().build();

        Optional<CurrentQuestion> currentQuestion = currentQuestionRepository.findByTestAndInTestIndex(currentTest.get(), pageId);

        if (!currentQuestion.isPresent())
            return ResponseEntity.notFound().build();

        if (!currentTest.get().getUser().getUsername().equals(user.get().getUsername()))
            return ResponseEntity.badRequest().build();

        if (currentQuestion.get().getQuest().getVariant()) {
            currentQuestion.get().setAnswer(Integer.parseInt(data));
        } else {
            for (Answer answer: currentQuestion.get().getQuest().getAnswers()) {
                if (answer.getAnswerCont().equals(data)) {
                    currentQuestion.get().setAnswer(answer.getOrder());
                    break;
                }
            }
        }
        currentQuestionRepository.save(currentQuestion.get());

        return ResponseEntity.ok("ok");
    }

    @GetMapping("/test/{id}/run/{page}")
    public String question(@PathVariable("id") Long id,
                           @PathVariable("page") Integer pageId,
                           Model model,
                           Principal principal) {
        if (pageId == -1) {
            model.addAttribute("testId", id);
            return "home";
        }
        Optional<CurrentTest> currentTest = currentTestRepository.findById(id);
        if (!currentTest.isPresent()) {
            model.addAttribute("currentTestError", "Такого теста не существует");
        } else {
            Optional<CurrentQuestion> currentQuestion = currentQuestionRepository.findByTestAndInTestIndex(currentTest.get(), pageId);
            if (currentQuestion.isPresent()) {
                Quest quest = currentQuestion.get().getQuest();
                model.addAttribute("text", quest.getText().split("\n"));

                model.addAttribute("variant", quest.getVariant());

                ArrayList<Answer> answers = new ArrayList(quest.getAnswers());
                Collections.sort(answers, Comparator.comparing(Answer::getOrder));

                model.addAttribute("answers", answers);
                model.addAttribute("nextQuestionId", pageId + 1);
                model.addAttribute("testId", id);
            } else {
                return "redirect:/test/" + id + "/end";
            }
        }
        return "tests";
    }

    @GetMapping("/test/{id}/end")
    public String res(@PathVariable("id") Long id, Principal principal) {
        Optional<CurrentTest> currentTest = currentTestRepository.findById(id);
        Optional<User> user = userRepository.findByUsername(principal.getName());
        if (!currentTest.isPresent() || !currentTest.get().getUser().getUsername().equals(user.get().getUsername())) {
            return "error";
        }
        currentTest.get().setClosed(true);
        currentTestRepository.save(currentTest.get());
        return "redirect:/test/" + id + "/results/-1";
    }

    @GetMapping("/test/{id}/results/{page}")
    public String res(@PathVariable("id") Long id,
                      @PathVariable("page") Integer pageId,
                      Model model, Principal principal) {
        Optional<User> user = userRepository.findByUsername(principal.getName());
        Optional<CurrentTest> currentTest = currentTestRepository.findById(id);
        if (currentTest.isPresent()) {
            model.addAttribute("isOwner", currentTest.get().getUser().getUsername().equals(user.get().getUsername()));
            model.addAttribute("username", currentTest.get().getUser().getUsername());
            model.addAttribute("sessionId", currentTest.get().getSession().getId());
            if (pageId == -1) {
                model.addAttribute("testId", id);
                int correct = 0;
                for (CurrentQuestion question: currentTest.get().getQuestions()) {
                    if (question.getAnswer() != null) {
                        Optional<Answer> answer = answerRepository.findByQuestAndOrder(question.getQuest(), question.getAnswer());
                        if (answer.isPresent() && answer.get().isCorrect())
                            ++correct;
                    }
                }

                model.addAttribute("result", correct + "/" + currentTest.get().getQuestions().size());
                return "end";
            }

            Optional<CurrentQuestion> currentQuestion = currentQuestionRepository.findByTestAndInTestIndex(currentTest.get(), pageId);
            if (currentQuestion.isPresent()) {
                Quest quest = currentQuestion.get().getQuest();
                model.addAttribute("text", quest.getText().split("\n"));

                model.addAttribute("variant", quest.getVariant());

                ArrayList<Answer> answers = new ArrayList(quest.getAnswers());
                Collections.sort(answers, Comparator.comparing(Answer::getOrder));

                model.addAttribute("currentAnswer", currentQuestion.get().getAnswer());
                model.addAttribute("answers", answers);
                model.addAttribute("nextQuestionId", pageId + 1);
                model.addAttribute("testId", id);

            }
                else return "redirect:/test/" + id + "/results/-1";


            return "results";
        }
            else return "/home";
    }

    @GetMapping("/editor")
    public String creator(@RequestParam(name = "id", required = false) Long id) {
        return "editortest";
    }

    @PostMapping("/editor")
    public String creat(String code, String name, @RequestParam(name = "id", required = false) Long id, HttpServletRequest httpServletRequest, Principal principal, Model model) {
        Optional<User> user = userRepository.findByUsername(principal.getName());
        Test test = null;
        if (id != null) {
            Optional<Test> found = testRepository.findById(id);
            if (found.isPresent()) {
                test = found.get();
            }
            if (currentTestRepository.existsByTest(test)) {
                test = null;
                id = null;
            } else {
                testRepository.delete(test);
            }
        }
        if (test == null) {
            test = new Test();
            test.setUser(user.get());
        }
        test.setName(name);
        analizator.parse(new Reader(code), test);
        return "editortest";
    }

    @GetMapping("/loadtest")
    String loadtest(HttpServletRequest httpServletRequest, Model model, Principal principal) {
        //Optional<Test> test = testRepository.findById(id)
        Optional<User> user = userRepository.findByUsername(principal.getName());
        List<Test> tests = testRepository.findByUser(user.get());
        model.addAttribute("tests", tests);
        return "loadtest";
    }

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

