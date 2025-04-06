package com.seti.ms.franchises.application.port.in;

import com.seti.ms.franchises.domain.model.Branch;
import com.seti.ms.franchises.domain.model.Franchise;
import com.seti.ms.franchises.domain.model.Product;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IFranchiseService {

    Mono<Franchise> getFranchiseById(String id);
    Mono<Franchise> saveFranchise(Franchise franchise);
    Mono<Branch> addProductToBranch(String franchiseId, String branchName, Product product);
    Mono<Branch> deleteProductFromBranch(String franchiseId, String branchName, String productName);
    Mono<Product> updateProductStock(String franchiseId, String branchName, String productName, int newStock);
    Mono<List<Branch>> getTopProductsByBranch(String franchiseId);
    Mono<Franchise> updateFranchiseName(String franchiseId, String newName);

}
