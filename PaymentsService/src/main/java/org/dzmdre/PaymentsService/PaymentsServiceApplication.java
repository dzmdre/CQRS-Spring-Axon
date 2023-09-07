package org.dzmdre.PaymentsService;

import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.SnapshotTriggerDefinition;
import org.axonframework.eventsourcing.Snapshotter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PaymentsServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(PaymentsServiceApplication.class, args);
	}

	@Bean(name="paymentSnapshotTriggerDefinition")
	public SnapshotTriggerDefinition paymentSnapshotTriggerDefinition(Snapshotter snapshotter) {
		return new EventCountSnapshotTriggerDefinition(snapshotter, 3);
	}
}
