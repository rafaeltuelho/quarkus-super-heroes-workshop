package io.quarkus.workshop.superheroes.villain;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VillainResourceTest {
    private static final String API_CONTEXT_PATH = "/api/villains";
    private static final int NB_VILLAINS = 581;
    private static final Logger logger = Logger.getLogger(VillainResourceTest.class);
    private static String villainId;

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get(API_CONTEXT_PATH + "/hello")
          .then()
             .statusCode(OK.getStatusCode())
             .body(is("Hello Villain Resource"));
    }

    /**
     * giving a random Villain identifier, the VillainResource endpoint should return a 204 (No content)
     */
    @Test
    void shouldNotGetUnknownVillain() {
        int randomId = new Random().nextInt();
        logger.debugf("looking for id %s", randomId);
        given()
            .pathParam("id", randomId)
            .when().get(API_CONTEXT_PATH + "/{id}")
            .then()
            .statusCode(NO_CONTENT.getStatusCode());
    }

    /**
     * checks that the VillainResource endpoint returns a random villain
     */
    @Test
    void shouldGetRandomVillain() {
        given()
            .accept(ContentType.JSON)
            .when().get(API_CONTEXT_PATH + "/random")
            .then()
            .statusCode(is(OK.getStatusCode()));        
    }
    
    /**
     * passing an invalid Villain should fail when creating it (thanks to the <code>@Valid</code> annotation)
     */
    @Test
    void shouldNotAddInvalidItem() {
        Villain villain = new Villain();
        villain.level = -1; //invalid level
        villain.name = "Sonic";
        villain.picture = "/unknown.png";
        villain.powers = "p1, p2, p3";

        given()
            .body(villain)
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .when().post(API_CONTEXT_PATH)
            .then()
            .statusCode(is(BAD_REQUEST.getStatusCode()));

    }

    /**
     * checks that the VillainResource endpoint returns the list of heroes
     */
    @Test
    @Order(1)
    void shouldGetInitialItems() {
        given()
            .accept(ContentType.JSON)
            .when().get(API_CONTEXT_PATH)
            .then()
            .statusCode(is(OK.getStatusCode()))
            .and()
            .body("size()", is(NB_VILLAINS));
            // .extract().body().as(getVillainTypeRef());

    }

    private TypeRef<List<Villain>> getVillainTypeRef() {
        return new TypeRef<List<Villain>>() {
            // Kept empty on purpose
        };
    }

    /**
     * checks that the VillainResource endpoint creates a valid Villain
     */
    @Test
    @Order(2)
    void shouldAddAnItem() {
        Villain villain = new Villain();
        villain.level = 7;
        villain.name = "Sonic";
        villain.picture = "https://static.wikia.nocookie.net/sonic/images/3/36/SFClassicRender.png/revision/latest/scale-to-width-down/568?cb=20171108085157";
        villain.powers = "speed";

        String resourceLocation = given()
            .body(villain)
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .when().post(API_CONTEXT_PATH)
            .then()
                .statusCode(is(CREATED.getStatusCode()))
                .extract().header("Location");
        assertTrue(resourceLocation.contains(API_CONTEXT_PATH));

        // Stores the id
        String[] segments = resourceLocation.split("/");
        // store id for the next test
        villainId = segments[segments.length - 1];
        logger.debugf("villainId: %s", villainId);
        assertNotNull(villainId);

        given()
            .pathParam("id", villainId)
            .when().get("/api/villains/{id}")
            .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .body("name", is(villain.name))
                .body("otherName", is(villain.otherName))
                .body("level", is(villain.level))
                .body("picture", is(villain.picture))
                .body("powers", is(villain.powers));

        given()
            .accept(ContentType.JSON)
            .when().get(API_CONTEXT_PATH)
            .then()
                .statusCode(is(OK.getStatusCode()))
                .and()
                .body("size()", is(NB_VILLAINS +1));
    }
    
    /**
     * checks that the VillainResource endpoint updates a newly created Villain
     */
    @Test
    @Order(3)
    void shouldUpdateAnItem() {
        Villain villain = new Villain();
        villain.id = Long.valueOf(villainId);
        villain.name = "Sonic 2";
        villain.otherName = "Sonic faster";
        villain.picture = "https://assets-prd.ignimgs.com/2022/03/14/sonic-the-hedgehog-two-button-1647271446364.jpg?width=300";
        villain.powers = "super speed";
        villain.level = 8;

        given()
            .body(villain)
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .when().put(API_CONTEXT_PATH)
            .then()
                .statusCode(OK.getStatusCode())
                .body("name", is(villain.name))
                .body("otherName", is(villain.otherName))
                .body("level", is(villain.level))
                .body("picture", is(villain.picture))
                .body("powers", is(villain.powers));

        given()
            .accept(ContentType.JSON)
            .when().get(API_CONTEXT_PATH)
            .then()
                .statusCode(is(OK.getStatusCode()))
                .and()
                .body("size()", is(NB_VILLAINS +1));
    }
    
    /**
     * checks that the VillainResource endpoint deletes a villain from the database    
     */
    @Test
    @Order(4)
    void shouldRemoveAnItem() {
        given()
            .pathParam("id", villainId)
            .when().delete(API_CONTEXT_PATH + "/{id}")
            .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given()
            .accept(ContentType.JSON)
            .when().get(API_CONTEXT_PATH)
            .then()
                .statusCode(is(OK.getStatusCode()))
                .and()
                .body("size()", is(NB_VILLAINS));
    }

    @Test
    void shouldPingOpenAPI() {
        given()
            .accept(ContentType.JSON)
            .when().get("/q/openapi")
            .then()
            .statusCode(OK.getStatusCode());
    }
}