package com.example;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerPort;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest
@AutoConfigureStubRunner(ids = "com.example:beer-api-producer", stubsMode = StubRunnerProperties.StubsMode.LOCAL)
public class ConsumerTests {

	@StubRunnerPort("beer-api-producer") int port;

	@Test
	void should_grant_beer_when_old_enough() {
		var exchange = new RestTemplate().exchange(RequestEntity.post("http://localhost:" + port + "/check")
				.contentType(MediaType.APPLICATION_JSON)
				.body("""
						{ "age": 45 }
						"""), String.class);

		then(exchange.getStatusCodeValue()).isEqualTo(200);
		then(exchange.getBody()).contains("OK");
	}

	@Test
	void should_reject_beer_when_too_young() {
		var exchange = new RestTemplate().exchange(RequestEntity.post("http://localhost:" + port + "/check")
				.contentType(MediaType.APPLICATION_JSON)
				.body("""
						{ "age": 17 }
						"""), String.class);

		then(exchange.getStatusCodeValue()).isEqualTo(200);
		then(exchange.getBody()).contains("NOT_OK");
	}

}
