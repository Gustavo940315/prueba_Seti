package com.seti.ms.franchises.application.usecase;

import com.seti.ms.franchises.application.port.in.IFranchiseService;
import com.seti.ms.franchises.application.port.out.FranchiseRepository;
import com.seti.ms.franchises.config.exception.MyHandleException;
import com.seti.ms.franchises.domain.model.Branch;
import com.seti.ms.franchises.domain.model.Franchise;
import com.seti.ms.franchises.domain.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class FranchiseServiceImpl implements IFranchiseService {

    private final FranchiseRepository franchiseRepository;

    @Override
    public Mono<Franchise> getFranchiseById(String id)  {
        return franchiseRepository.getFranchiseById(id);

    }

    @Override
    public Mono<Franchise> saveFranchise(Franchise franchise) {
        return franchiseRepository.saveFranchise(franchise);
    }

    @Override
    public Mono<Branch> addProductToBranch(String franchiseId, String branchName, Product product) {
        return franchiseRepository.getFranchiseById(franchiseId)
                .flatMap(franchise -> {
                    List<Branch> branches = franchise.getBranches();

                    Branch matchedBranch = branches.stream()
                            .filter(b -> b.getName().equalsIgnoreCase(branchName))
                            .findFirst()
                            .orElseThrow(() -> new MyHandleException("Sucursal " + branchName + " no encontrada"));

                    if (Objects.isNull(matchedBranch.getProducts())) {
                        matchedBranch.setProducts(new ArrayList<>());
                    }

                    boolean existsProduct = matchedBranch.getProducts().stream()
                            .anyMatch(p -> p.getName().equalsIgnoreCase(product.getName()));

                    if (existsProduct) {
                        throw new MyHandleException("Ya existe un producto con el nombre: " + product.getName());
                    }

                    matchedBranch.getProducts().add(product);
                    franchise.setBranches(branches);

                    return franchiseRepository.saveFranchise(franchise)
                            .thenReturn(matchedBranch);
                });

    }

    @Override
    public Mono<Branch> deleteProductFromBranch(String franchiseId, String branchName, String productName) {
        return franchiseRepository.getFranchiseById(franchiseId)
                .flatMap(franchise -> {
                    Branch branch = getBranchWithProductsOrThrow(franchise, branchName);

                    boolean removedProduct = branch.getProducts().removeIf(
                            p -> p.getName().equalsIgnoreCase(productName)
                    );

                    if (!removedProduct) {
                        throw new MyHandleException("Producto " + productName + " no encontrado en la sucursal");
                    }

                    return franchiseRepository.saveFranchise(franchise)
                            .thenReturn(branch);
                });
    }

    @Override
    public Mono<Product> updateProductStock(String franchiseId, String branchName, String productName, int newStock) {
        return franchiseRepository.getFranchiseById(franchiseId)
                .flatMap(franchise -> {
                    Branch branch = getBranchWithProductsOrThrow(franchise, branchName);
                    Product product = getProductOrThrow(branch, productName);

                    product.setStock(newStock);

                    return franchiseRepository.saveFranchise(franchise)
                            .thenReturn(product);
                });
    }

    @Override
    public Mono<List<Branch>> getTopProductsByBranch(String franchiseId) {
        return franchiseRepository.getFranchiseById(franchiseId)
                .map(franchise -> franchise.getBranches().stream()
                        .filter(branch -> branch.getProducts() != null && !branch.getProducts().isEmpty())
                        .map(branch -> {
                            Product topProduct = branch.getProducts().stream()
                                    .max(Comparator.comparingInt(Product::getStock))
                                    .orElse(null);

                            branch.setProducts(List.of(topProduct));
                            return branch;
                        })
                        .collect(Collectors.toList())
                );
    }

    @Override
    public Mono<Franchise> updateFranchiseName(String franchiseId, String newName) {
        return franchiseRepository.getFranchiseById(franchiseId)
                .map(franchise -> {
                    franchise.setName(newName);
                    return franchise;
                })
                .flatMap(franchiseRepository::saveFranchise);
    }

    @Override
    public Mono<Branch> updateBranchName(String franchiseId, String branchName, String newBranchName) {
        return franchiseRepository.getFranchiseById(franchiseId)
                .map(franchise -> {
                    Branch branch = franchise.getBranches().stream()
                            .filter(b -> b.getName().equalsIgnoreCase(branchName))
                            .findFirst()
                            .orElseThrow(() -> new MyHandleException("La Sucursal " + branchName + " no fue encontrada"));

                    branch.setName(newBranchName);

                    return franchise;
                })
                .flatMap(franchiseRepository::saveFranchise)
                .map(franchise ->
                        franchise.getBranches().stream()
                                .filter(b -> b.getName().equalsIgnoreCase(newBranchName))
                                .findFirst()
                                .orElseThrow(() -> new MyHandleException("La Sucursal no fue actualizada, no encontrada"))
                );
    }

    private Branch getBranchWithProductsOrThrow(Franchise franchise, String branchName) {
        return franchise.getBranches().stream()
                .filter(b -> b.getName().equalsIgnoreCase(branchName))
                .findFirst()
                .map(branch -> {
                    if (Objects.isNull(branch.getProducts()) || branch.getProducts().isEmpty()) {
                        throw new MyHandleException("La sucursal no tiene productos");
                    }
                    return branch;
                })
                .orElseThrow(() -> new MyHandleException("La Sucursal " + branchName + " no fue encontrada"));
    }

    private Product getProductOrThrow(Branch branch, String productName) {
        return branch.getProducts().stream()
                .filter(p -> p.getName().equalsIgnoreCase(productName))
                .findFirst()
                .orElseThrow(() -> new MyHandleException("El producto " + productName + " no fue encontrado"));
    }

}
