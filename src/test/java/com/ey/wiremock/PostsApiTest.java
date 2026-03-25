package com.ey.wiremock;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PostsApiTest {

    private static final Properties CONFIG = loadConfig();

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = resolveBaseUrl();
    }

    private static Properties loadConfig() {
        Properties properties = new Properties();
        try (InputStream in = PostsApiTest.class.getClassLoader().getResourceAsStream("api-test-config.properties")) {
            if (in == null) {
                throw new IllegalStateException("Missing test config file: api-test-config.properties");
            }
            properties.load(in);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load api-test-config.properties", e);
        }
    }

    private static String resolveBaseUrl() {
        String baseUrlOverride = System.getProperty("api.base.url");
        if (baseUrlOverride != null && !baseUrlOverride.isBlank()) {
            return baseUrlOverride;
        }

        String legacyWiremockUrl = System.getProperty("wiremock.base.url");
        if (legacyWiremockUrl != null && !legacyWiremockUrl.isBlank()) {
            return legacyWiremockUrl;
        }

        String target = System.getProperty("api.target", CONFIG.getProperty("api.target", "local")).trim();
        String key = "api.baseUrl." + target;
        String configuredUrl = CONFIG.getProperty(key);
        if (configuredUrl == null || configuredUrl.isBlank()) {
            throw new IllegalStateException("No URL configured for target '" + target + "' in key '" + key + "'");
        }
        return configuredUrl.trim();
    }

    // ---------------------------------------------------------------
    // GET /posts
    // ---------------------------------------------------------------

    @Test
    @Order(1)
    void getAllPosts_returns200WithArray() {
        given()
            .when()
                .get("/posts")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", not(empty()))
                .body("[0].id", notNullValue())
                .body("[0].userId", notNullValue())
                .body("[0].title", notNullValue())
                .body("[0].body", notNullValue());
    }

    // ---------------------------------------------------------------
    // GET /posts/{id}
    // ---------------------------------------------------------------

    @Test
    @Order(2)
    void getPostById_returns200WithPost() {
        given()
            .when()
                .get("/posts/1")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("userId", notNullValue())
                .body("title", not(emptyOrNullString()))
                .body("body", not(emptyOrNullString()));
    }

    @Test
    @Order(3)
    void getPostById_anyNumericId_returns200() {
        given()
            .when()
                .get("/posts/5")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    // ---------------------------------------------------------------
    // GET /posts/{id}/comments
    // ---------------------------------------------------------------

    @Test
    @Order(4)
    void getCommentsForPost_returns200WithArray() {
        given()
            .when()
                .get("/posts/1/comments")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", not(empty()))
                .body("[0].postId", notNullValue())
                .body("[0].id", notNullValue())
                .body("[0].name", not(emptyOrNullString()))
                .body("[0].email", not(emptyOrNullString()))
                .body("[0].body", not(emptyOrNullString()));
    }

    // ---------------------------------------------------------------
    // POST /posts
    // ---------------------------------------------------------------

    @Test
    @Order(5)
    void createPost_returns201WithBody() {
        String payload = """
                {
                  "title": "Test Post",
                  "body": "Test body content",
                  "userId": 1
                }
                """;

        given()
            .contentType(ContentType.JSON)
            .body(payload)
            .when()
                .post("/posts")
            .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("title", not(emptyOrNullString()))
                .body("body", not(emptyOrNullString()));
    }

    @Test
    @Order(6)
        void createPost_returnsCreatedPayload() {
        String payload = """
                {
                  "title": "My Dynamic Title",
                  "body": "My Dynamic Body",
                  "userId": 1
                }
                """;

        given()
            .contentType(ContentType.JSON)
            .body(payload)
            .when()
                .post("/posts")
            .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("title", not(emptyOrNullString()))
                .body("body", not(emptyOrNullString()));
    }

    // ---------------------------------------------------------------
    // PUT /posts/{id}
    // ---------------------------------------------------------------

    @Test
    @Order(7)
    void updatePost_returns200() {
        String payload = """
                {
                  "title": "Updated Title",
                  "body": "Updated body",
                  "userId": 1
                }
                """;

        given()
            .contentType(ContentType.JSON)
            .body(payload)
            .when()
                .put("/posts/1")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("title", not(emptyOrNullString()))
                .body("body", not(emptyOrNullString()));
    }

    @Test
    @Order(8)
        void updatePost_returnsUpdatedPayload() {
        String payload = """
                {
                  "title": "Echo Title",
                  "body": "Echo Body",
                  "userId": 1
                }
                """;

        given()
            .contentType(ContentType.JSON)
            .body(payload)
            .when()
                .put("/posts/1")
            .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("title", not(emptyOrNullString()))
                .body("body", not(emptyOrNullString()));
    }

    // ---------------------------------------------------------------
    // PATCH /posts/{id}
    // ---------------------------------------------------------------

    @Test
    @Order(9)
    void patchPost_returns200() {
        String payload = """
                {
                  "title": "Patched Title"
                }
                """;

        given()
            .contentType(ContentType.JSON)
            .body(payload)
            .when()
                .patch("/posts/1")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("title", not(emptyOrNullString()));
    }

    // ---------------------------------------------------------------
    // DELETE /posts/{id}
    // ---------------------------------------------------------------

    @Test
    @Order(10)
    void deletePost_returns200WithEmptyBody() {
        given()
            .when()
                .delete("/posts/1")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    // ---------------------------------------------------------------
    // Response header assertion
    // ---------------------------------------------------------------

    @Test
    @Order(11)
    void getAllPosts_hasJsonContentType() {
        given()
            .when()
                .get("/posts")
            .then()
                .statusCode(200)
                .header("Content-Type", containsString("application/json"));
    }
}
