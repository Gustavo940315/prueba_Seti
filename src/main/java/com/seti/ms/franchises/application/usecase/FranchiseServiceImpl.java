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
import java.util.List;
import java.util.Objects;


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
                    List<Branch> branches = franchise.getBranches();

                    Branch branchToUpdate = branches.stream()
                            .filter(b -> b.getName().equalsIgnoreCase(branchName))
                            .findFirst()
                            .orElseThrow(() -> new MyHandleException("La Sucursal" + branchName + " no fue encontrada"));

                    List<Product> products = branchToUpdate.getProducts();

                    if (Objects.isNull(products) || products.isEmpty()) {
                        throw new MyHandleException("La sucursal no tiene productos");
                    }

                    boolean removedProduct = products.removeIf(p -> p.getName().equalsIgnoreCase(productName));

                    if (!removedProduct) {
                        throw new MyHandleException("Producto " + productName + " no encontrado en la sucursal");
                    }

                    branchToUpdate.setProducts(products);
                    franchise.setBranches(branches);

                    return franchiseRepository.saveFranchise(franchise)
                            .thenReturn(branchToUpdate);
                });
    }

    @Override
    public Mono<Product> updateProductStock(String franchiseId, String branchName, String productName, int newStock) {
        return franchiseRepository.getFranchiseById(franchiseId)
                .flatMap(franchise -> {
                    List<Branch> branches = franchise.getBranches();

                    Branch branch = branches.stream()
                            .filter(b -> b.getName().equalsIgnoreCase(branchName))
                            .findFirst()
                            .orElseThrow(() -> new MyHandleException("La Sucursal" + branchName + " no fue encontrada"));

                    List<Product> products = branch.getProducts();
                    if (Objects.isNull(products) || products.isEmpty()) {
                        throw new MyHandleException("La sucursal no tiene productos");
                    }

                    Product product = products.stream()
                            .filter(p -> p.getName().equalsIgnoreCase(productName))
                            .findFirst()
                            .orElseThrow(() -> new MyHandleException("El producto " + productName + " no fue encontrado"));

                    product.setStock(newStock);

                    return franchiseRepository.saveFranchise(franchise)
                            .thenReturn(product);
                });
    }


}
