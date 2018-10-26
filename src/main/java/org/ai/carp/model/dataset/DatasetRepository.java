package org.ai.carp.model.dataset;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface DatasetRepository extends MongoRepository<Dataset, String> {

    Dataset findDatasetByName(String name);

}
