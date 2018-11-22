package org.ai.carp.model.dataset;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ISEDatasetRepository extends MongoRepository<ISEDataset, String> {

    ISEDataset findDatasetByName(String name);

}
