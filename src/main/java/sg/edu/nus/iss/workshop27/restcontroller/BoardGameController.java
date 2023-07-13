package sg.edu.nus.iss.workshop27.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import sg.edu.nus.iss.workshop27.service.GameService;

@Controller
@RequestMapping
public class BoardGameController {

    @Autowired
    GameService gService;

    @GetMapping("/game")
    public String getGameForm(@RequestParam String gid, Model m) {
        m.addAttribute("game", gService.getGameById(gid));
        return "form";
    }
    
    // POST /review
    // consumes = x-www-form-urlencoded

    // PUT /review/{review_id}
    // consumes = application/json

    // GET /review/{review_id}
    // produces = application/json

    // GET /review/{review_id}/history
    // produces = aplciation/json
}
