package com.spiegelberger.estore.ProductsService.query;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.spiegelberger.estore.ProductsService.core.data.ProductEntity;
import com.spiegelberger.estore.ProductsService.core.data.ProductsRepository;
import com.spiegelberger.estore.ProductsService.core.events.ProductCreatedEvent;
import com.spiegelberger.estore.core.events.ProductReservedEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ProcessingGroup("product-group")
public class ProductEventsHandler {
	
	private ProductsRepository productsRepository;
	
	@Autowired
	public ProductEventsHandler(ProductsRepository productsRepository) {
		this.productsRepository = productsRepository;
	}
	
	//General Exception handling
	@ExceptionHandler(resultType=Exception.class)
	public void handle(Exception exception) throws Exception {
		throw exception;
	}

	@ExceptionHandler(resultType=IllegalArgumentException.class)
	public void handle(IllegalArgumentException exception) throws Exception {
		throw exception;
	}

	@EventHandler
	public void on(ProductCreatedEvent event) throws Exception {
		
		ProductEntity productEntity = new ProductEntity();
		BeanUtils.copyProperties(event, productEntity);
		
		try {
			productsRepository.save(productEntity);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		//Transaction will be rolled back:
		
//		if(true) {
//			throw new Exception("Forcing exception in the Event Handler class");
//		}
		
	}
	
	@EventHandler
	public void on(ProductReservedEvent productReservedEvent) {
		
		ProductEntity productEntity = 
				productsRepository.findByProductId(productReservedEvent.getProductId());
		
		productEntity.setQuantity(productEntity.getQuantity()-productReservedEvent.getQuantity());
		
		productsRepository.save(productEntity);
		
		log.info("productReservedEvent is called for productId: " + productReservedEvent.getProductId() + 
				" and orderId: " + productReservedEvent.getOrderId());
	}
	
	
}
