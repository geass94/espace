package ge.boxwood.espace.controllers.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class AdminController {
    @RequestMapping(path = "/admin", method = GET)
    public String index(){
        return "admin/index.html";
    }
}
