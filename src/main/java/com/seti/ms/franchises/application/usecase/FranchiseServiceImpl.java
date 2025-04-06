package com.seti.ms.franchises.application.usecase;

import com.seti.ms.franchises.application.port.in.IFranchiseService;
import com.seti.ms.franchises.application.port.out.FranchiseRepository;
import com.seti.ms.franchises.domain.model.Franchise;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


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


}
