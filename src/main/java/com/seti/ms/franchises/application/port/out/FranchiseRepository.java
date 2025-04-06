package com.seti.ms.franchises.application.port.out;

import com.seti.ms.franchises.domain.model.Franchise;
import reactor.core.publisher.Mono;

public interface FranchiseRepository {

    Mono<Franchise> getFranchiseById(String id);
    Mono<Franchise> saveFranchise(Franchise franchise);

}
