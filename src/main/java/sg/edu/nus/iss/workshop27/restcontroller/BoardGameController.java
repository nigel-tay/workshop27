package sg.edu.nus.iss.workshop27.restcontroller;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping("/review")
    public ResponseEntity<String> postReview(@RequestBody MultiValueMap<String, String> values) {
        
        if (gService.postReview(values)) {
            return new ResponseEntity<String>(HttpStatusCode.valueOf(200));
        }
        else {
            return new ResponseEntity<String>(HttpStatusCode.valueOf(400));
        }
    }

    /**
     * "_id" : ObjectId("64af9bd16a179e639c899356"),
        "gid" : NumberInt(12),
        "name" : "Ra",
        "year" : NumberInt(1999),
        "ranking" : NumberInt(144),
        "users_rated" : NumberInt(17433),
        "url" : "https://www.boardgamegeek.com/boardgame/12/ra",
        "image" : "https://cf.geekdo-images.com/micro/img/dLCB1zVufOOwUH41u39hwZSunmE=/fit-in/64x64/pic3013552.jpg"
     */
    
    // POST /review
    // consumes = x-www-form-urlencoded

    // PUT /review/{review_id}
    // consumes = application/json

    // GET /review/{review_id}
    // produces = application/json

    // GET /review/{review_id}/history
    // produces = aplciation/json
}
