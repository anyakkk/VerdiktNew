package vrd.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vrd.TestGenerator;
import vrd.base.*;
import vrd.compiler.Analizator;
import vrd.compiler.Reader;
import vrd.repository.*;
import vrd.security.UserService;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.*;

@Controller
public class TestController {
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
}
