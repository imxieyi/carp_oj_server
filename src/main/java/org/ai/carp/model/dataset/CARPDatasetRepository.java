package org.ai.carp.model.dataset;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CARPDatasetRepository extends MongoRepository<CARPDataset, String> {

    CARPDataset findDatasetByName(String name);

}
