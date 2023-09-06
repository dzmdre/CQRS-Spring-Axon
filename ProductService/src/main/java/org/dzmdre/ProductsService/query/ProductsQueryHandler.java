package org.dzmdre.ProductsService.query;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.axonframework.queryhandling.QueryHandler;
import org.dzmdre.ProductsService.core.data.ProductEntity;
import org.dzmdre.ProductsService.core.data.ProductsRepository;
import org.dzmdre.ProductsService.query.rest.ProductRestModel;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class ProductsQueryHandler {
	
	private final ProductsRepository productsRepository;
	
	public ProductsQueryHandler(ProductsRepository productsRepository) {
		this.productsRepository = productsRepository;
	}
	
	@QueryHandler
	public List<ProductRestModel> findProducts(FindProductsQuery query) {
		List<ProductEntity> storedProducts =  productsRepository.findAll();
		return storedProducts.stream()
				.map(this::getProductRestModel)
				.collect(Collectors.toList());
	}

	private ProductRestModel getProductRestModel(ProductEntity productEntity){
		ProductRestModel productRestModel = new ProductRestModel();
		BeanUtils.copyProperties(productEntity, productRestModel);
		return productRestModel;
	}
}
