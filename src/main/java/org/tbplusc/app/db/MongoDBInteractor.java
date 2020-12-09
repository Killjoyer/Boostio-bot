package org.tbplusc.app.db;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbplusc.app.util.EnvWrapper;

public class MongoDBInteractor {
    private static final Logger logger = LoggerFactory.getLogger(MongoDBInteractor.class);

    public static MongoDatabase getMongoDatabase() {
        var pojoCodecRegistry = CodecRegistries.fromRegistries(
                        MongoClientSettings.getDefaultCodecRegistry(),
                        CodecRegistries.fromProviders(
                                        PojoCodecProvider.builder().automatic(true).build()));
        var settings = MongoClientSettings.builder().codecRegistry(pojoCodecRegistry)
                        .applyConnectionString(new ConnectionString(
                                        EnvWrapper.getValue("MONGO_CONN_STRING")))
                        .build();
        var mongoClient = MongoClients.create(settings);
        return mongoClient.getDatabase("telegramBot");
    }
}
