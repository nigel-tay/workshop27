package sg.edu.nus.iss.workshop27.restcontroller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @GetMapping("/update")
    public String getUpdateReview(@RequestParam String review_id, Model m) {
        m.addAttribute("review_id", "review_id");
        return "updateReview";
    }

    @PostMapping(path="/review", consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> postReview(@RequestBody MultiValueMap<String, String> values) {
        if (gService.postReview(values)) {
            return new ResponseEntity<String>(HttpStatusCode.valueOf(200));
        }
        else {
            return new ResponseEntity<String>(HttpStatusCode.valueOf(400));
        }
    }

    @PutMapping(path="/review/{review_id}", consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateReview(@PathVariable String review_id, @RequestBody HashMap<String, String> values) {
        
        if (gService.updateReview(values, review_id)) {
            return ResponseEntity.status(200).body("Review with ID: " + review_id + " has been updated");
        }
        else {
            return ResponseEntity.status(400).body("Review ID provided does not exist");
        }
    }

    @GetMapping(path="/review/{review_id}", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getLatestReviewById(@PathVariable String review_id) {
        String result = gService.getLatestReviewById(review_id);

        if (result == null) {
            return ResponseEntity.status(400).body("Review ID provided does not exist");
        }

        return ResponseEntity.status(200).body(result);
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
    
    //  DONE
    // POST /review
    // consumes = x-www-form-urlencoded

    // PUT /review/{review_id}
    // consumes = application/json

    // TO DO
    // GET /review/{review_id}
    // produces = application/json

    // GET /review/{review_id}/history
    // produces = aplciation/json
}
