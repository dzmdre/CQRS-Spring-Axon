package org.dzmdre.ProductsService.core.data;


import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductsRepository extends MongoRepository<ProductEntity, String> {
	
	ProductEntity findByProductId(String productId);
	ProductEntity findByProductIdOrTitle(String productId, String title);

}
