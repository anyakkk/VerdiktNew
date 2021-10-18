package vrd;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vrd.base.Test;
import vrd.base.TestRepository;
import vrd.base.User;
import vrd.base.UserRepository;

import java.security.Principal;
import java.util.Optional;

@Controller
public class ApiController {

    @Autowired
    public UserRepository userRepository;
    @Autowired
    public TestRepository testRepository;

    @GetMapping("/index")
    public String index(Model model, Principal principal) {
        if (principal != null) {
            Optional<User> user = userRepository.findByNameuser(principal.getName());
        }
        return "home";
    }

    @GetMapping("/test/{id}")
    public String test(@PathVariable("id") Long id, Model model, Principal principal) {
        Optional<User> user = userRepository.findByNameuser(principal.getName());
        if (!user.isPresent())
            return "tests";
        else
            return "redirect:/login";
    }

    @GetMapping("/test/edit/{id}")
    public String editor(@PathVariable("id") Long id, Model model, Principal principal) {
        return "";
    }

    @GetMapping("/test/run/{id}/{page}")
    public String question(@PathVariable("id") Long id, @PathVariable("page") Long pageId, Model model, Principal principal) {
        return "";
    }

    @GetMapping("/request/create_test")
    public String creator() {
        return "";
    }
}
