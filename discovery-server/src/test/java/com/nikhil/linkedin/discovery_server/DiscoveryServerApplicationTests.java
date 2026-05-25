package com.nikhil.linkedin.discovery_server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class DiscoveryServerApplicationTests {

	@Test
	void applicationClassLoads() {
		assertNotNull(DiscoveryServerApplication.class);
	}

}
