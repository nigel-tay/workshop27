package sg.edu.nus.iss.workshop27.repository;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ReviewRepository {

    private final String C_REVIEW = "reviews";
    private final String F_OBJECTID = "_id";
    
    @Autowired
    MongoTemplate mTemplate;

    public Boolean postReview(Document review) {
        Document insertedDocument = mTemplate.insert(review, C_REVIEW);
        return insertedDocument.getObjectId(F_OBJECTID) != null ? true : false;
    }
}
