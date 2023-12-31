package org.dzmdre.ProductsService.command;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.dzmdre.ProductsService.core.data.ProductLookupEntity;
import org.dzmdre.ProductsService.core.data.ProductLookupRepository;
import org.dzmdre.ProductsService.core.events.ProductCreatedEvent;
import org.springframework.stereotype.Component;

import static org.dzmdre.ProductsService.ProductsServiceApplication.PRODUCT_GROUP;


@Component
@ProcessingGroup(PRODUCT_GROUP)
public class ProductLookupEventsHandler {
	private final ProductLookupRepository productLookupRepository;
	
	public ProductLookupEventsHandler(ProductLookupRepository productLookupRepository) {
		this.productLookupRepository = productLookupRepository;
	}

	@EventHandler
	public void on(ProductCreatedEvent event) {
		ProductLookupEntity productLookupEntity = new ProductLookupEntity(event.getProductId(),
				event.getTitle());
		productLookupRepository.save(productLookupEntity);
	}
	
	@ResetHandler
	public void reset() {
		productLookupRepository.deleteAll();
	}
	
}
