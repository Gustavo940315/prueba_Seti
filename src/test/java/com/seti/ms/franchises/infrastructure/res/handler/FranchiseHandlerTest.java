package com.seti.ms.franchises.infrastructure.res.handler;

import com.seti.ms.franchises.application.port.in.IFranchiseService;
import com.seti.ms.franchises.domain.model.Franchise;

import com.seti.ms.franchises.infrastructure.rest.handler.FranchiseHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FranchiseHandlerTest {

    @Mock
    private IFranchiseService franchiseService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        FranchiseHandler handler = new FranchiseHandler(franchiseService);
        RouterFunction<ServerResponse> routerFunction = RouterFunctions.route()
                .GET("/franchises/v1/{id}", handler::getFranchiseForById)
                .POST("/franchises/v1", handler::saveFranchise)
                .build();

        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build();
    }

    @Test
    void shouldGetFranchiseById() {
        String franchiseId = "f1";
        Franchise franchise = new Franchise(franchiseId, "Franquicia X", List.of());

        when(franchiseService.getFranchiseById(franchiseId)).thenReturn(Mono.just(franchise));

        webTestClient.get()
                .uri("/franchises/v1/{id}", franchiseId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(franchiseId)
                .jsonPath("$.name").isEqualTo("Franquicia X");
    }

    @Test
    void shouldSaveFranchise() {
        Franchise franchise = new Franchise("f2", "Nueva Franquicia", List.of());

        when(franchiseService.saveFranchise(any())).thenReturn(Mono.just(franchise));

        webTestClient.post()
                .uri("/franchises/v1")
                .bodyValue(franchise)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Nueva Franquicia");
    }

}
