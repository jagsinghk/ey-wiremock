package com.ey.wiremock;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class PostsSimulation extends Simulation {

        private static final String DEFAULT_BASE_URL = "https://jsonplaceholder.typicode.com";

    private static final String BASE_URL =
                        System.getProperty("wiremock.base.url", DEFAULT_BASE_URL);

    HttpProtocolBuilder httpProtocol = http
            .baseUrl(BASE_URL)
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    ScenarioBuilder posts = scenario("Posts API Performance")
            .exec(
                    http("GET /posts")
                            .get("/posts")
                            .check(status().is(200))
            )
            .pause(1)
            .exec(
                    http("GET /posts/1")
                            .get("/posts/1")
                            .check(status().is(200))
            )
            .pause(1)
            .exec(
                    http("POST /posts")
                            .post("/posts")
                            .body(StringBody(
                                    "{\"title\":\"Perf Test\",\"body\":\"Load test body\",\"userId\":1}"
                            ))
                            .asJson()
                            .check(status().is(201))
            );

    {
        setUp(
                posts.injectOpen(rampUsers(10).during(20))
        )
        .protocols(httpProtocol)
        .assertions(
                global().responseTime().max().lt(2000),
                global().successfulRequests().percent().gt(95.0)
        );
    }
}
