package com.seti.ms.franchises.infrastructure.rest.router;

import com.seti.ms.franchises.infrastructure.rest.handler.FranchiseHandler;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class FranchiseRouter {

    @Bean
    public RouterFunction<ServerResponse> routes(FranchiseHandler handler){
        return route(GET("/franchises/v1/{id}"), handler::getFranchiseForById)
                .andRoute(POST("/franchises/v1"), handler::saveFranchise)
                .andRoute(POST("/franchises/v1/{franchiseId}/branches/{branchName}/products"), handler::addProductToBranch)
                .andRoute(DELETE("/franchises/v1/{franchiseId}/branches/{branchName}/products/{productName}"), handler::deleteProductFromBranch)
                .andRoute(PUT("/franchises/v1/{franchiseId}/branches/{branchName}/products/{productName}/stock"), handler::updateProductStock)
                .andRoute(GET("/franchises/v1/{franchiseId}/top-stock-products"), handler::getTopProductsByBranch)
                .andRoute(PUT("/franchises/v1/{franchiseId}/update-name"), handler::updateFranchiseName);
    }
}