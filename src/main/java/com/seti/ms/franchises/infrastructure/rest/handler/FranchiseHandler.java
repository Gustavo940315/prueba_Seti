package com.seti.ms.franchises.infrastructure.rest.handler;

import com.seti.ms.franchises.application.port.in.IFranchiseService;
import com.seti.ms.franchises.domain.model.Franchise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class FranchiseHandler {

    @Autowired
    private IFranchiseService franchiseService;

    public Mono<ServerResponse> getFranchiseForById(ServerRequest request) {

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(franchiseService.getFranchiseById(request.pathVariable("id")),
                        Franchise.class);

    }


    public Mono<ServerResponse> saveFranchise(ServerRequest request) {
        return request.bodyToMono(Franchise.class)
                .flatMap(franchiseService::saveFranchise)
                .flatMap(savedFranchise -> ServerResponse
                        .created(URI.create("/franchises/v1" + savedFranchise.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedFranchise)
                );
    }
}
