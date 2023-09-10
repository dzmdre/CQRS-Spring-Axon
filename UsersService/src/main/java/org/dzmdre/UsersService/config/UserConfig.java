package org.dzmdre.UsersService.config;


import com.mongodb.MongoClient;
import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventsourcing.*;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.kafka.eventhandling.DefaultKafkaMessageConverter;
import org.axonframework.kafka.eventhandling.KafkaMessageConverter;
import org.axonframework.mongo.DefaultMongoTemplate;
import org.axonframework.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.axonframework.serialization.Serializer;
import org.axonframework.springboot.autoconfig.AxonAutoConfiguration;
import org.dzmdre.UsersService.command.UserAggregate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(AxonAutoConfiguration.class)
public class UserConfig {

    @Autowired
    private EventStore myEventStore;

    @Bean
    public EventStorageEngine eventStore(MongoClient client) {
        return new MongoEventStorageEngine(new DefaultMongoTemplate(client));
    }
    @Bean
    public AggregateFactory<UserAggregate> aggregateFactory(){
        return new GenericAggregateFactory<>(UserAggregate.class);
    }

    @Bean
    public Snapshotter snapShotter(AggregateFactory<UserAggregate> aggregateFactory){
        return new AggregateSnapshotter(myEventStore, aggregateFactory);
    }

    @Bean
    public SnapshotTriggerDefinition snapshotTriggerDefinition(Snapshotter snapshotter) {
        return new EventCountSnapshotTriggerDefinition(snapshotter, 5);
    }
    @Bean
    public Repository<UserAggregate> accountAggregateRepository(SnapshotTriggerDefinition snapshotTriggerDefinition, AggregateFactory<UserAggregate> aggregateFactory){
        return new EventSourcingRepository<>(aggregateFactory, myEventStore, snapshotTriggerDefinition);
    }

    @ConditionalOnMissingBean
    @Bean
    public KafkaMessageConverter<String, byte[]> kafkaMessageConverter(
            @Qualifier("eventSerializer") Serializer eventSerializer) {
        return new DefaultKafkaMessageConverter(eventSerializer);
    }
}
