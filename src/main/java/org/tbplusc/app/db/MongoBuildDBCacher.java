package org.tbplusc.app.db;

import java.util.ArrayList;
import java.util.List;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbplusc.app.talent.helper.HeroBuild;
import org.tbplusc.app.talent.helper.HeroBuilds;

public class MongoBuildDBCacher implements IBuildDBCacher {
    private static final Logger logger = LoggerFactory.getLogger(MongoBuildDBCacher.class);
    private final MongoCollection<Document> collection;

    public MongoBuildDBCacher(MongoDatabase database) {
        this.collection = database.getCollection("builds");
    }

    @Override
    public HeroBuilds getBuilds(String heroName) throws FailedReadException {
        var query = collection.find(Filters.eq("heroName", heroName)).limit(1).first();
        if (query == null) {
            throw new FailedReadException();
        }
        var builds = new ArrayList<HeroBuild>();
        for (var elem : (List<Document>) query.get("builds")) {
            builds.add(new HeroBuild((String) elem.get("name"), (String) elem.get("description"),
                            (List<String>) elem.get("talents")));
        }
        return new HeroBuilds(heroName, builds);
    }

    @Override
    public void cacheBuilds(String heroName, HeroBuilds builds) throws FailedWriteException {
        var result = collection.updateOne(Filters.eq("heroName", heroName),
                        Updates.set("builds", builds.getBuilds()),
                        new UpdateOptions().upsert(true));
        if (!result.wasAcknowledged())
            throw new FailedWriteException();
    }

    @Override
    public void clearCache() throws FailedWriteException {
        var result = collection.deleteMany(new Document());
        if (!result.wasAcknowledged())
            throw new FailedWriteException();
    }
}
