package com.e_marketplace.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class BackendApplicationTests {

	@Test
	void contextLoads() {
		// This test verifies that the Spring application context loads successfully
		// Using H2 in-memory database for testing with disabled security
	}

}
