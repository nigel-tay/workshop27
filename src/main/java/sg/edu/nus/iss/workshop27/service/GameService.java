package sg.edu.nus.iss.workshop27.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import sg.edu.nus.iss.workshop27.repository.GameRepository;
import sg.edu.nus.iss.workshop27.repository.ReviewRepository;

@Service
public class GameService {
    
    @Autowired
    GameRepository gRepo;

    @Autowired
    ReviewRepository rRepo;

    // Generic
    public JsonObject buildJson(String user, String rating, String comment, String gid, String name, Boolean edited, Date timestamp) {
        JsonObject json = Json.createObjectBuilder()
                            .add("user", user)
                            .add("rating", rating)
                            .add("comment", comment)
                            .add("gid", gid)
                            .add("name", name)
                            .add("edited", edited)
                            .add("timestamp", timestamp.toString())
                            .build();

        return json;
    }

    // Game
    public Document getGameById(String gid) {
        return gRepo.getGameById(gid).get(0);
    }

    // Review
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

    public Boolean updateReview(HashMap<String, String> values, String objectId) {
        Document updatedReview = new Document()
                                    .append("comment", values.get("comment"))
                                    .append("rating", values.get("rating"))
                                    .append("posted", values.get("posted"));
        return rRepo.updateReview(updatedReview, objectId);
    }

    public String getLatestReviewById(String reviewId) {
        List<Document> reviewResult = rRepo.getReviewById(reviewId);

        if (reviewResult.get(0) == null) {
            return null;
        }

        Document review = reviewResult.get(0);

        AggregationResults<Document> latestReviewResult = rRepo.getLatestReviewById(reviewId);

        String user = review.getString("user");
        String rating = "";
        String comment = "";
        String gid = review.getString("gid");
        String name = review.getString("name");
        Boolean edited = false;
        Date timestamp = null;
        
        if (latestReviewResult.getMappedResults().get(0) != null) {
            Document latestReview = (Document)latestReviewResult.getMappedResults().get(0).get("_id");
            // edited false
            rating = latestReview.getString("rating");
            comment = latestReview.getString("comment");
            edited = true;
            timestamp = new Date();

            return buildJson(user, rating, comment, gid, name, edited, timestamp).toString();
        }
        // edited true
        rating = review.getString("rating");
        comment = review.getString("comment");
        timestamp = new Date();

        return buildJson(user, rating, comment, gid, name, edited, timestamp).toString();
    }

    /**
     * 
        {
            "user" : String (reviewResult),
            "rating" : String (latestReviewResult),
            "comment" : String (latestReviewResult),
            "gid" : String (reviewResult),
            "name" : String (reviewResult),
            "edited" : Boolean true/false,
            "posted" : String new Date
        }
    }
     */
}
