package org.dzmdre.ProductsService.query;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.dzmdre.ProductsService.core.data.ProductEntity;
import org.dzmdre.ProductsService.core.data.ProductsRepository;
import org.dzmdre.ProductsService.core.events.ProductCreatedEvent;
import org.dzmdre.core.events.ProductReservationCancelledEvent;
import org.dzmdre.core.events.ProductReservedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@ProcessingGroup("product-group")
public class ProductEventsHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductEventsHandler.class);
	private final ProductsRepository productsRepository;
	public ProductEventsHandler(ProductsRepository productsRepository) {
		this.productsRepository = productsRepository;
	}

	@ExceptionHandler(resultType=Exception.class)
	public void handle(Exception exception) throws Exception {
		throw exception;
	}
	
	@ExceptionHandler(resultType=IllegalArgumentException.class)
	public void handle(IllegalArgumentException exception) {
		LOGGER.error(exception.getMessage(), exception);
	}

	@EventHandler
	public void on(ProductCreatedEvent event) {
		ProductEntity productEntity = new ProductEntity();
		BeanUtils.copyProperties(event, productEntity);
		try {
			productsRepository.save(productEntity);
		} catch (IllegalArgumentException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
	}
	
	@EventHandler
	public void on(ProductReservedEvent productReservedEvent) {
		ProductEntity productEntity = productsRepository.findByProductId(productReservedEvent.getProductId());
		LOGGER.debug("ProductReservedEvent: Current product quantity " + productEntity.getQuantity());
		productEntity.setQuantity(productEntity.getQuantity() - productReservedEvent.getQuantity());
		productsRepository.save(productEntity);
		LOGGER.debug("ProductReservedEvent: New product quantity " + productEntity.getQuantity());
 		LOGGER.info("ProductReservedEvent is called for productId:" + productReservedEvent.getProductId() +
				" and orderId: " + productReservedEvent.getOrderId());
	}
	
	@EventHandler
	public void on(ProductReservationCancelledEvent productReservationCancelledEvent) {
		final ProductEntity currentlyStoredProduct =  productsRepository.findByProductId(productReservationCancelledEvent.getProductId());
		LOGGER.debug("ProductReservationCancelledEvent: Current product quantity "
		+ currentlyStoredProduct.getQuantity() );
		final int newQuantity = currentlyStoredProduct.getQuantity() + productReservationCancelledEvent.getQuantity();
		currentlyStoredProduct.setQuantity(newQuantity);
		productsRepository.save(currentlyStoredProduct);
		LOGGER.debug("ProductReservationCancelledEvent: New product quantity "
		+ currentlyStoredProduct.getQuantity() );
	}
	
	@ResetHandler
	public void reset() {
		productsRepository.deleteAll();
	}

}
