package org.ai.carp.model.dataset;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface IMPDatasetRepository extends MongoRepository<IMPDataset, String> {

    IMPDataset findDatasetByName(String name);

}
