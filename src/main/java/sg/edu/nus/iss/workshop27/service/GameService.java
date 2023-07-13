package sg.edu.nus.iss.workshop27.service;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import sg.edu.nus.iss.workshop27.repository.GameRepository;
import sg.edu.nus.iss.workshop27.repository.ReviewRepository;

@Service
public class GameService {
    
    @Autowired
    GameRepository gRepo;

    @Autowired
    ReviewRepository rRepo;

    public Document getGameById(String gid) {
        return gRepo.getGameById(gid).get(0);
    }

    public Boolean postReview(MultiValueMap<String, String> values) {
        
        if (gRepo.getGameById(values.getFirst("gid")).isEmpty()) {
            return false;
        }

        Document review = new Document()
                                    .append("gid", values.getFirst("gid"))
                                    .append("name", values.getFirst("name"))
                                    .append("user", values.getFirst("user"))
                                    .append("rating", values.getFirst("rating"))
                                    .append("comment", values.getFirst("comment"))
                                    .append("posted", values.getFirst("posted"));
        return rRepo.postReview(review);
    }
}
