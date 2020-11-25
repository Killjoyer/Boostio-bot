package org.tbplusc.app.db;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

public class MongoPrefixesDBInteractor implements IPrefixDBInteractor {
    private static final Logger logger = LoggerFactory.getLogger(MongoPrefixesDBInteractor.class);
    private final MongoCollection<Document> collection;

    public MongoPrefixesDBInteractor(MongoDatabase database) {
        this.collection = database.getCollection("prefixes");
    }

    @Override public void setPrefix(String guildId, String prefix) throws FailedWriteException {
        var result = collection.updateOne(eq("guildId", guildId), set("prefix", prefix),
                        new UpdateOptions().upsert(true));
        if (!result.wasAcknowledged())
            throw new FailedWriteException();
    }

    @Override public String getPrefix(String guildId) throws FailedReadException {
        var result = collection.find(eq("guildId", guildId)).limit(1).first();
        if (result == null) throw new FailedReadException();
        return result.get("prefix").toString();
    }
}
