package com.seti.ms.franchises.infrastructure.db.dynamodb;

import com.seti.ms.franchises.application.port.out.FranchiseRepository;
import com.seti.ms.franchises.domain.model.Franchise;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seti.ms.franchises.config.exception.MyHandleException;
import com.seti.ms.franchises.domain.model.Branch;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DynamoFranchiseRepository implements FranchiseRepository {

    private final DynamoDbClient dynamoDbClient;

    @Value("${aws.dynamodb.table-name}")
    private String tableName;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Franchise> getFranchiseById(String id) {
        return Mono.fromCallable(() -> {
            var key = Map.of("id", AttributeValue.fromS(id));

            var response = dynamoDbClient.getItem(builder -> builder
                    .tableName(tableName)
                    .key(key)
            );

            if (!response.hasItem()) {
                throw new MyHandleException("La franquicia con ID '" + id + "' no fue encontrada");
            }

            return response.item();
        }).flatMap(item -> {
            Franchise franchise = new Franchise();
            franchise.setId(item.get("id").s());
            franchise.setName(item.get("name").s());


            return Mono.fromCallable(() -> {
                String branchesJson = item.get("branches").s();
                franchise.setBranches(
                        objectMapper.readValue(
                                branchesJson,
                                objectMapper.getTypeFactory().constructCollectionType(List.class, Branch.class)
                        )
                );
                return franchise;
            });
        });

    }

    @Override
    public Mono<Franchise> saveFranchise(Franchise franchise) {

        return Mono.fromCallable(() -> {
            String branchesJson = objectMapper.writeValueAsString(franchise.getBranches());

            Map<String, AttributeValue> item = new HashMap<>();
            item.put("id", AttributeValue.fromS(franchise.getId()));
            item.put("name", AttributeValue.fromS(franchise.getName()));
            item.put("branches", AttributeValue.fromS(branchesJson));

            PutItemRequest request = PutItemRequest.builder()
                    .tableName(tableName)
                    .item(item)
                    .build();

            dynamoDbClient.putItem(request);
            log.info("✅ Franquicia '{}' guardada exitosamente en DynamoDB", franchise.getId());
            return franchise;
        });
    }
}
