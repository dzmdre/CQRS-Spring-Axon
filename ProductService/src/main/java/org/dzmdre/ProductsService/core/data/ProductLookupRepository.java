package org.dzmdre.ProductsService.core.data;


import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductLookupRepository extends MongoRepository<ProductLookupEntity, String> {
	ProductLookupEntity findByProductIdOrTitle(String productId, String title);
}
