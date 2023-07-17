package sg.edu.nus.iss.workshop27.service;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.tomcat.util.json.JSONParser;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
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

    public String getLatestReviewById(String reviewId) throws ParseException {
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
        Date posted = null;
        
        if (!latestReviewResult.getMappedResults().isEmpty()) {
            Document latestReview = latestReviewResult.getMappedResults().get(0);
            for (Document oreview: latestReview.getList("_id", Document.class)) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");  
                Date postedDate = formatter.parse(oreview.getString("posted"));

                if (posted == null) {
                    posted = postedDate;
                    rating = oreview.getString("rating");
                    comment = oreview.getString("comment");
                }
                else if (posted.getTime() < postedDate.getTime()) {
                    posted = postedDate;
                    rating = oreview.getString("rating");
                    comment = oreview.getString("comment");
                }
            }
            edited = true;
            timestamp = new Date();

            return buildJson(user, rating, comment, gid, name, edited, timestamp).toString();
        }

        rating = review.getString("rating");
        comment = review.getString("comment");
        timestamp = new Date();

        return buildJson(user, rating, comment, gid, name, edited, timestamp).toString();
    }

    public String getHistory(String reviewId) throws ParseException {
        List<Document> reviewResult = rRepo.getReviewById(reviewId);
        AggregationResults<Document> latestReviewResult = rRepo.getLatestReviewById(reviewId);
        
        if (reviewResult.get(0) == null) {
            return null;
        }

        Document review = reviewResult.get(0);

        String user = review.getString("user");
        String gid = review.getString("gid");
        String name = review.getString("name");
        List<JsonObject> edited = new ArrayList<>();
        Date timestamp = new Date();
        String rating = "";
        String comment = "";
        Date posted = null;

        if (!latestReviewResult.getMappedResults().isEmpty()) {
            
            Document latestReview = latestReviewResult.getMappedResults().get(0);
            for (Document oreview: latestReview.getList("_id", Document.class)) {

                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");  
                Date postedDate = formatter.parse(oreview.getString("posted"));

                if (posted == null) {
                    posted = postedDate;
                    rating = oreview.getString("rating");
                    comment = oreview.getString("comment");
                }
                else if (posted.getTime() < postedDate.getTime()) {
                    posted = postedDate;
                    rating = oreview.getString("rating");
                    comment = oreview.getString("comment");
                }
            }
        }
        else {
            rating = review.getString("rating");
            comment = review.getString("comment");
        }
        

        for (Document dEdited: review.getList("edited", Document.class)) {
            JsonObject editedJson = Json.createObjectBuilder()
                                            .add("comment", dEdited.getString("comment"))
                                            .add("rating", dEdited.getString("rating"))
                                            .add("posted", dEdited.getString("posted"))
                                            .build();
            edited.add(editedJson);
        }

        JsonObject json = Json.createObjectBuilder()
                            .add("user", user)
                            .add("rating", rating)
                            .add("comment", comment)
                            .add("gid", gid)
                            .add("name", name)
                            .add("edited", Json.createArrayBuilder(edited))
                            .add("timestamp", timestamp.toString())
                            .build();
        
        return json.toString();
    }
}
