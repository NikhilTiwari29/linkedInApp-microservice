package com.nikhil.linkedin.api_gateway;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ApiGatewayApplicationTests {

	@Test
	void applicationClassLoads() {
		assertNotNull(ApiGatewayApplication.class);
	}

}
