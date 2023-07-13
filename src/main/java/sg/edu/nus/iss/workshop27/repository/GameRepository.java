package sg.edu.nus.iss.workshop27.repository;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class GameRepository {

    private final String C_GAMES = "games";
    private final String F_GID = "gid";
    
    @Autowired
    MongoTemplate mTemplate;

    public List<Document> getGameById(String gid) {
        int intGid = Integer.parseInt(gid);
        Criteria criteria = Criteria.where(F_GID).is(intGid);
        Query query = Query.query(criteria);
        return mTemplate.find(query, Document.class, C_GAMES);
    }
}
