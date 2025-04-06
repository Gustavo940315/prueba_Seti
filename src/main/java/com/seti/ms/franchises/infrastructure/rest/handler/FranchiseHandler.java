package com.seti.ms.franchises.infrastructure.rest.handler;

import com.seti.ms.franchises.application.port.in.IFranchiseService;
import com.seti.ms.franchises.domain.model.Franchise;
import com.seti.ms.franchises.domain.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

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
                        .ok().bodyValue(savedFranchise)
                );
    }

    public Mono<ServerResponse> addProductToBranch(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchName = request.pathVariable("branchName");

        return request.bodyToMono(Product.class)
                .flatMap(product ->
                        franchiseService.addProductToBranch(franchiseId, branchName, product)
                )
                .flatMap(branch ->
                        ServerResponse.ok().bodyValue(branch)
                );
    }

    public Mono<ServerResponse> deleteProductFromBranch(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchName = request.pathVariable("branchName");
        String productName = request.pathVariable("productName");

        return franchiseService.deleteProductFromBranch(franchiseId, branchName, productName)
                .flatMap(updatedBranch -> ServerResponse.ok().bodyValue(updatedBranch));
    }

    public Mono<ServerResponse> updateProductStock(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchName = request.pathVariable("branchName");
        String productName = request.pathVariable("productName");

        return request.bodyToMono(Product.class)
                .flatMap(product ->
                        franchiseService.updateProductStock(franchiseId, branchName, productName, product.getStock())
                )
                .flatMap(updatedProduct -> ServerResponse.ok().bodyValue(updatedProduct));
    }

    public Mono<ServerResponse> getTopProductsByBranch(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");

        return franchiseService.getTopProductsByBranch(franchiseId)
                .flatMap(branches -> ServerResponse.ok().bodyValue(branches));
    }
}
