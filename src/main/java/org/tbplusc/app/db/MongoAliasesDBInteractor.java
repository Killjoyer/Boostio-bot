package org.tbplusc.app.db;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

public class MongoAliasesDBInteractor implements IAliasesDBInteractor {
    private final MongoCollection<Document> collection;

    public MongoAliasesDBInteractor(MongoDatabase database) {
        collection = database.getCollection("aliases");
    }

    @Override public void addAlias(String serverId, String alias, String hero)
                    throws FailedWriteException {
        var query = and(eq("guildId", serverId), eq("alias", alias));
        var result = collection.updateOne(query, set("hero", hero),
                        new UpdateOptions().upsert(true));
        if (!result.wasAcknowledged())
            throw new FailedWriteException();
    }

    @Override public void removeAlias(String serverId, String alias) throws FailedWriteException {
        var result = collection.deleteOne(and(eq("guildId", serverId), eq("alias", alias)));
        if (!result.wasAcknowledged())
            throw new FailedWriteException();
    }

    @Override public Map<String, String> getAliases(String serverId) throws FailedReadException {
        var aliases = new HashMap<String, String>();
        try {
            collection.find(eq("guildId", serverId)).forEach(document -> aliases
                            .put(document.get("alias").toString(), document.get("hero").toString()));
        } catch (MongoException e) {
            throw new FailedReadException();
        }
        return aliases;
    }

    @Override public String getHeroByAlias(String serverId, String alias) throws FailedReadException {
        var document = collection.find(and(eq("guildId", serverId), eq("alias", alias))).first();
        if (document == null) throw new FailedReadException();
        return document.get("hero").toString();
    }
}
