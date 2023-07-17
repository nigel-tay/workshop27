package sg.edu.nus.iss.workshop27.repository;

import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.UpdateResult;

@Repository
public class ReviewRepository {

    private final String C_REVIEW = "reviews";
    private final String F_OBJECTID = "_id";
    private final String F_EDITED = "edited";
    
    @Autowired
    MongoTemplate mTemplate;

    public Boolean postReview(Document review) {
        Document insertedDocument = mTemplate.insert(review, C_REVIEW);
        return insertedDocument.getObjectId(F_OBJECTID) != null ? true : false;
    }

    /**
     * db.reviews.updateOne(
            {_id: ObjectId("64afbc4a58fa4269417edf04")},
            { $push: {
                edited: {comment: "text2", rating: "6", posted: "100"}
            }}
        )
     */

    public Boolean updateReview(Document updatedReview, String objectId) {
        ObjectId reviewId = new ObjectId(objectId);
        Criteria critera = Criteria.where(F_OBJECTID).is(reviewId);
        Query query = Query.query(critera);
        Update updateOperation = new Update().push("edited", updatedReview);
        UpdateResult uResult = mTemplate.updateMulti(query, updateOperation, Document.class, C_REVIEW);

        return uResult.getModifiedCount() > 0 ? true : false;
    }

    public List<Document> getReviewById(String reviewId) {
        ObjectId objectId = new ObjectId(reviewId);
        Criteria criteria = Criteria.where(F_OBJECTID).is(objectId);
        Query query = Query.query(criteria);

        return mTemplate.find(query, Document.class, C_REVIEW);
    }

    /**
     * db.reviews.aggregate([
            { $match: { _id: ObjectId("64afc44bd19a7c360f4deecb") } },
            { $unwind: '$edited'},
            { $group: { _id: '$edited'} },
            { $sort: { _id: -1 }},
            { $limit: 1 }
        ])
     */
    public AggregationResults<Document> getLatestReviewById(String reviewId) {
        ObjectId objectId = new ObjectId(reviewId);
        Criteria criteria = Criteria.where(F_OBJECTID).is(objectId);
        MatchOperation m = Aggregation.match(criteria);
        // UnwindOperation u = Aggregation.unwind(F_EDITED);
        GroupOperation g = Aggregation.group(F_EDITED);
        SortOperation s = Aggregation.sort(Sort.by(Direction.ASC, "edited.posted"));
        // LimitOperation l = Aggregation.limit(1);
        Aggregation pipeline = Aggregation.newAggregation(m, g, s);

        return mTemplate.aggregate(pipeline, C_REVIEW, Document.class);
    }
}
