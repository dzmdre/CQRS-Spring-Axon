package org.dzmdre.OrdersService.config;

import org.axonframework.kafka.eventhandling.DefaultKafkaMessageConverter;
import org.axonframework.kafka.eventhandling.KafkaMessageConverter;
import org.axonframework.serialization.Serializer;
import org.axonframework.springboot.autoconfig.AxonAutoConfiguration;
import org.axonframework.eventsourcing.AggregateFactory;
import org.axonframework.eventsourcing.AggregateSnapshotter;
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventsourcing.GenericAggregateFactory;
import org.axonframework.eventsourcing.SnapshotTriggerDefinition;
import org.axonframework.eventsourcing.Snapshotter;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.mongo.DefaultMongoTemplate;
import org.axonframework.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.mongodb.MongoClient;
import org.dzmdre.OrdersService.command.*;

@Configuration
@AutoConfigureAfter(AxonAutoConfiguration.class)
public class OrderConfig {

    @Autowired
    private EventStore myEventStore;

    @Bean
    public EventStorageEngine eventStore(MongoClient client) {
        return new MongoEventStorageEngine(new DefaultMongoTemplate(client));
    }
    @Bean
    public AggregateFactory<OrderAggregate> aggregateFactory(){
        return new GenericAggregateFactory<>(OrderAggregate.class);
    }

    @Bean
    public Snapshotter snapShotter(AggregateFactory<OrderAggregate> aggregateFactory){
        return new AggregateSnapshotter(myEventStore, aggregateFactory);
    }

    @Bean
    public SnapshotTriggerDefinition snapshotTriggerDefinition(Snapshotter snapshotter) {
        return new EventCountSnapshotTriggerDefinition(snapshotter, 5);
    }
    @Bean
    public Repository<OrderAggregate> accountAggregateRepository(SnapshotTriggerDefinition snapshotTriggerDefinition,AggregateFactory<OrderAggregate> aggregateFactory){
        return new EventSourcingRepository<>(aggregateFactory, myEventStore, snapshotTriggerDefinition);
    }

    @ConditionalOnMissingBean
    @Bean
    public KafkaMessageConverter<String, byte[]> kafkaMessageConverter(
            @Qualifier("eventSerializer") Serializer eventSerializer) {
        return new DefaultKafkaMessageConverter(eventSerializer);
    }
}
