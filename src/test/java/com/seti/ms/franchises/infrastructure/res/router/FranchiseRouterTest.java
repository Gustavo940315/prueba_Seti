package com.seti.ms.franchises.infrastructure.res.router;

import com.seti.ms.franchises.infrastructure.rest.handler.FranchiseHandler;
import com.seti.ms.franchises.infrastructure.rest.router.FranchiseRouter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FranchiseRouterTest {

    @Mock
    private FranchiseHandler franchiseHandler;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        RouterFunction<ServerResponse> route = new FranchiseRouter().routes(franchiseHandler);
        this.webTestClient = WebTestClient.bindToRouterFunction(route).build();
    }

    @Test
    void shouldRouteToGetFranchiseById() {
        String franchiseId = "f1";
        when(franchiseHandler.getFranchiseForById(any()))
                .thenReturn(ServerResponse.ok().bodyValue("Franchise " + franchiseId));

        webTestClient.get()
                .uri("/franchises/v1/{id}", franchiseId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Franchise " + franchiseId);
    }

    @Test
    void shouldRouteToSaveFranchise() {
        when(franchiseHandler.saveFranchise(any()))
                .thenReturn(ServerResponse.ok().bodyValue("saved"));

        webTestClient.post()
                .uri("/franchises/v1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("saved");
    }
}
