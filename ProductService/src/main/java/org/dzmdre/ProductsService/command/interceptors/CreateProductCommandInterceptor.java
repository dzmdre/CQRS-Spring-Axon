package org.dzmdre.ProductsService.command.interceptors;

import java.util.List;
import java.util.function.BiFunction;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.dzmdre.ProductsService.command.CreateProductCommand;
import org.dzmdre.ProductsService.core.data.ProductLookupEntity;
import org.dzmdre.ProductsService.core.data.ProductLookupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class CreateProductCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {
	private static final Logger LOGGER = LoggerFactory.getLogger(CreateProductCommandInterceptor.class);
	private final ProductLookupRepository productLookupRepository;
	
	public CreateProductCommandInterceptor(ProductLookupRepository productLookupRepository) {
		this.productLookupRepository = productLookupRepository;
	}
 
	@Override
	public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(
			List<? extends CommandMessage<?>> messages) {
		return (index, command) -> {
			LOGGER.info("Intercepted command: " + command.getPayloadType());
			if(!CreateProductCommand.class.equals(command.getPayloadType())) {
				return command;
			}
			CreateProductCommand createProductCommand = (CreateProductCommand)command.getPayload();
			ProductLookupEntity productLookupEntity =  productLookupRepository.findByProductIdOrTitle(createProductCommand.getProductId(),
					createProductCommand.getTitle());
			if(productLookupEntity != null) {
				throw new IllegalStateException(
						String.format("Product with productId %s or title %s already exist",
								createProductCommand.getProductId(), createProductCommand.getTitle())
						);
			}
			return command;
		};
	}

}
