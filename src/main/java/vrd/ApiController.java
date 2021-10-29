package vrd;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vrd.base.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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



    @GetMapping("/index")
    public String index(Model model, Principal principal) {
        if (principal != null) {
            Optional<User> user = userRepository.findByNameuser(principal.getName());
        }
        model.addAttribute("testId", -1);
        return "home";
    }

    @GetMapping("/test/{id}")
    public String test(@PathVariable("id") Long id, Model model, Principal principal) {
        Optional<User> user = userRepository.findByNameuser(principal.getName());
        return "redirect:/test/{id}/run/-1";
    }


    @GetMapping("/test/{id}/edit")
    public String editor(@PathVariable("id") Long id, Model model, Principal principal) {
        return "";
    }

    @GetMapping("/test/{id}/run/{page}")
    public String question(@PathVariable("id") Long id, @PathVariable("page") Long pageId, Model model, Principal principal) {
        if (pageId == -1) {
            model.addAttribute("testId", id);
            return "home";
        }
        String text = "2+2=";
        boolean var = true;
        ArrayList<Answer> answers = new ArrayList();
        answers.add(new Answer(0, null, null, "1"));
        answers.add(new Answer(1, null, null, "2"));
        answers.add(new Answer(2, null, null, "3"));
        answers.add(new Answer(3, null, null, "4"));
        answers.add(new Answer(4, null, null, "5"));


        model.addAttribute("text", text);
        model.addAttribute("variant", true);
        model.addAttribute("answers", answers);
        model.addAttribute("nextQuestionId", pageId+1);
        model.addAttribute("testId", id);
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
    String loadtest(HttpServletRequest httpServletRequest, Model model, Principal principal) {
        //List<Test> tests = testRepository.findByUser(user);
        Iterable<Test> tests = testRepository.findAll();
        model.addAttribute("tests", tests);
//        List<Test> _tests = new ArrayList<Test>();
//        {
//            Test test = new Test();
//            test.setName("имя 1");
//            _tests.add(test);
//        }
//        {
//            Test test = new Test();
//            test.setName("имя 2");
//            _tests.add(test);
//        }
//
//        {
//            Test test = new Test();
//            test.setName("имя 3");
//            _tests.add(test);
//        }
//
//        {
//            Test test = new Test();
//            test.setName("имя 4");
//            _tests.add(test);
//        }
//
//        {
//            Test test = new Test();
//            test.setName("имя 5");
//            _tests.add(test);
//        }
//        model.addAttribute("tests", _tests);
        return "loadtest";
    }

}
