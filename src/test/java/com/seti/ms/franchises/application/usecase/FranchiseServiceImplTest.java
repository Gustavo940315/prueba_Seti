package com.seti.ms.franchises.application.usecase;

import com.seti.ms.franchises.application.port.out.FranchiseRepository;
import com.seti.ms.franchises.config.exception.MyHandleException;
import com.seti.ms.franchises.domain.model.Branch;
import com.seti.ms.franchises.domain.model.Franchise;
import com.seti.ms.franchises.domain.model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FranchiseServiceImplTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    @InjectMocks
    private FranchiseServiceImpl franchiseService;

    @Test
    void shouldUpdateFranchiseName() {
        Franchise existing = new Franchise("franchise-001", "Super shopper", new ArrayList<>());
        Franchise updated = new Franchise("franchise-001", "Super D1", new ArrayList<>());

        when(franchiseRepository.getFranchiseById("franchise-001")).thenReturn(Mono.just(existing));
        when(franchiseRepository.saveFranchise(any())).thenReturn(Mono.just(updated));

        Franchise result = franchiseService.updateFranchiseName("franchise-001", "Super D1").block();

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Super D1");

        verify(franchiseRepository).saveFranchise(existing);
    }

    @Test
    void shouldAddProductToBranch() {
        String franchiseId = "f1";
        String branchName = "Sucursal A";

        Product newProduct = new Product("Café", 10);
        Branch branch = new Branch(branchName, new ArrayList<>());
        Franchise franchise = new Franchise(franchiseId, "Franquicia X", List.of(branch));

        Franchise updatedFranchise = new Franchise(franchiseId, "Franquicia X", List.of(
                new Branch(branchName, List.of(newProduct))
        ));

        when(franchiseRepository.getFranchiseById(franchiseId)).thenReturn(Mono.just(franchise));
        when(franchiseRepository.saveFranchise(any())).thenReturn(Mono.just(updatedFranchise));

        Branch result = franchiseService.addProductToBranch(franchiseId, branchName, newProduct).block();

        assertThat(result.getProducts().get(0).getName()).isEqualTo("Café");
        verify(franchiseRepository).saveFranchise(any());
    }

    @Test
    void shouldThrowWhenBranchNotFound() {
        String franchiseId = "franchise1";
        String branchName = "Sur";
        Product product = new Product("Café", 10);

        Franchise franchise = new Franchise(franchiseId, "Franquicia X", List.of(
                new Branch("Otra Sucursal", new ArrayList<>())
        ));

        when(franchiseRepository.getFranchiseById(franchiseId)).thenReturn(Mono.just(franchise));

        assertThatThrownBy(() ->
                franchiseService.addProductToBranch(franchiseId, branchName, product).block()
        )
                .isInstanceOf(MyHandleException.class)
                .hasMessageContaining("Sucursal Sur no encontrada");
    }

    @Test
    void shouldDeleteProductFromBranch() {
        String franchiseId = "f1";
        String branchName = "Sucursal A";
        String productName = "Café";

        Product p1 = new Product(productName, 10);
        Branch branch = new Branch(branchName, new ArrayList<>(List.of(p1)));
        Franchise franchise = new Franchise(franchiseId, "Franquicia X", List.of(branch));

        when(franchiseRepository.getFranchiseById(franchiseId)).thenReturn(Mono.just(franchise));
        when(franchiseRepository.saveFranchise(any())).thenReturn(Mono.just(franchise));

        Branch result = franchiseService.deleteProductFromBranch(franchiseId, branchName, productName).block();

        assertThat(result.getProducts()).isNotNull();
        verify(franchiseRepository).saveFranchise(franchise);
    }

    @Test
    void shouldUpdateProductStockSuccessfully() {
        String franchiseId = "f1";
        String branchName = "Sucursal A";
        String productName = "Café";
        int newStock = 20;

        Product product = new Product(productName, 5);
        Branch branch = new Branch(branchName, new ArrayList<>(List.of(product)));
        Franchise franchise = new Franchise(franchiseId, "Franquicia X", List.of(branch));

        when(franchiseRepository.getFranchiseById(franchiseId)).thenReturn(Mono.just(franchise));
        when(franchiseRepository.saveFranchise(any())).thenReturn(Mono.just(franchise));

        Product result = franchiseService.updateProductStock(franchiseId, branchName, productName, newStock).block();

        assertThat(result).isNotNull();
        assertThat(result.getStock()).isEqualTo(newStock);
        verify(franchiseRepository).saveFranchise(franchise);
    }

    @Test
    void shouldReturnFranchiseById() {
        String id = "f1";
        Franchise franchise = new Franchise(id, "Franquicia X", new ArrayList<>());

        when(franchiseRepository.getFranchiseById(id)).thenReturn(Mono.just(franchise));

        Franchise result = franchiseService.getFranchiseById(id).block();

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("Franquicia X");

        verify(franchiseRepository).getFranchiseById(id);
    }

    @Test
    void shouldSaveFranchise() {
        Franchise franchise = new Franchise("f2", "Nueva Franquicia", new ArrayList<>());

        when(franchiseRepository.saveFranchise(franchise)).thenReturn(Mono.just(franchise));

        Franchise result = franchiseService.saveFranchise(franchise).block();

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Nueva Franquicia");

        verify(franchiseRepository).saveFranchise(franchise);
    }

    @Test
    void shouldReturnTopProductByBranch() {
        String franchiseId = "f1";

        Product p1 = new Product("Café", 5);
        Product p2 = new Product("Té", 10);
        Product p3 = new Product("Jugo", 7);

        Branch b1 = new Branch("Sucursal A", List.of(p1, p2));
        Branch b2 = new Branch("Sucursal B", List.of(p3));

        Franchise franchise = new Franchise(franchiseId, "Franquicia X", List.of(b1, b2));

        when(franchiseRepository.getFranchiseById(franchiseId)).thenReturn(Mono.just(franchise));

        List<Branch> result = franchiseService.getTopProductsByBranch(franchiseId).block();

        Branch branchA = result.get(0);
        assertThat(branchA.getProducts()).isNotNull();
        assertThat(branchA.getProducts().size()).isEqualTo(1);
        assertThat(branchA.getProducts().get(0).getName()).isEqualTo("Té");

        Branch branchB = result.get(1);
        assertThat(branchA.getProducts()).isNotNull();
        assertThat(branchA.getProducts().size()).isEqualTo(1);
        assertThat(branchB.getProducts().get(0).getName()).isEqualTo("Jugo");
    }

    @Test
    void shouldReturnEmptyWhenNoProducts() {
        String franchiseId = "f2";

        Branch emptyBranch = new Branch("Sucursal A", new ArrayList<>());
        Branch nullBranch = new Branch("Sucursal B", null);

        Franchise franchise = new Franchise(franchiseId, "Vacía", List.of(emptyBranch, nullBranch));

        when(franchiseRepository.getFranchiseById(franchiseId)).thenReturn(Mono.just(franchise));

        List<Branch> result = franchiseService.getTopProductsByBranch(franchiseId).block();
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    void shouldUpdateBranchNameSuccessfully() {
        String franchiseId = "f1";
        String oldBranchName = "Sucursal A";
        String newBranchName = "Sucursal Renombrada";

        Branch branch = new Branch(oldBranchName, new ArrayList<>());
        Franchise originalFranchise = new Franchise(franchiseId, "Franquicia X", new ArrayList<>(List.of(branch)));

        Branch updatedBranch = new Branch(newBranchName, new ArrayList<>());
        Franchise updatedFranchise = new Franchise(franchiseId, "Franquicia X", List.of(updatedBranch));

        when(franchiseRepository.getFranchiseById(franchiseId)).thenReturn(Mono.just(originalFranchise));
        when(franchiseRepository.saveFranchise(any())).thenReturn(Mono.just(updatedFranchise));

        Branch result = franchiseService.updateBranchName(franchiseId, oldBranchName, newBranchName).block();

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(newBranchName);
        verify(franchiseRepository).saveFranchise(any());
    }
}
