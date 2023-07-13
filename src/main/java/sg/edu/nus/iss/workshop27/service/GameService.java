package sg.edu.nus.iss.workshop27.service;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.edu.nus.iss.workshop27.repository.GameRepository;

@Service
public class GameService {
    
    @Autowired
    GameRepository gRepo;

    public Document getGameById(String gid) {
        return gRepo.getGameById(gid).get(0);
    }
}
