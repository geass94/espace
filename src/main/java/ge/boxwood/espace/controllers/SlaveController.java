package ge.boxwood.espace.controllers;


import ge.boxwood.espace.security.TokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/slave")
public class SlaveController {
    @Autowired
    private TokenHelper tokenHelper;

}
