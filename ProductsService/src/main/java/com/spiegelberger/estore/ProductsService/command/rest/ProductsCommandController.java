package com.spiegelberger.estore.ProductsService.command.rest;

import java.util.UUID;

import javax.validation.Valid;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spiegelberger.estore.ProductsService.command.CreateProductCommand;


@RestController
@RequestMapping("/products")
public class ProductsCommandController {

	private final Environment env;
	private final CommandGateway commandGateway;
	
	
	@Autowired
	public ProductsCommandController(Environment env, CommandGateway commandGateway) {
		this.env = env;
		this.commandGateway = commandGateway;
	}

	@PostMapping
	public String createProduct(@Valid @RequestBody CreateProductRestModel createProductRestModel) {
		
		CreateProductCommand createProductCommand =
		CreateProductCommand.builder()
		.title(createProductRestModel.getTitle())
		.price(createProductRestModel.getPrice())
		.quantity(createProductRestModel.getQuantity())
		.productId(UUID.randomUUID().toString())
		.build();
		
		String returnValue = null;
		try {
			returnValue = commandGateway.sendAndWait(createProductCommand);
		} catch (Exception e) {
			returnValue = e.getLocalizedMessage();
		}
		
		return returnValue;
	}
	/*
	@GetMapping
	public String getProduct() {
		
		
		return "HTTP GET Handled " + env.getProperty("local.server.port");
	}
	
	@PutMapping
	public String updateProduct() {
		return "HTTP PUT Handled";
	}
	
	@DeleteMapping
	public String deleteProduct() {
		return "HTTP DELETE Handled";
	}
	*/
}
