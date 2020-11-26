package org.tbplusc.app.db;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbplusc.app.util.EnvWrapper;

public class MongoDBInteractor {
    private static final Logger logger = LoggerFactory.getLogger(MongoDBInteractor.class);

    public static MongoDatabase getMongoDatabase() {
        var mongoClient = MongoClients.create(EnvWrapper.getValue("MONGO_CONN_STRING"));
        return mongoClient.getDatabase("telegramBot");
    }
}
