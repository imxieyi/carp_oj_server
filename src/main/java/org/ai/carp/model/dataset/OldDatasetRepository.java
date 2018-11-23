package org.ai.carp.model.dataset;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface OldDatasetRepository extends MongoRepository<OldDataset, String> {
}
