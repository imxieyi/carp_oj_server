package org.ai.carp.controller.util;

import org.ai.carp.controller.exceptions.InvalidRequestException;
import org.ai.carp.model.Database;
import org.ai.carp.model.dataset.BaseDataset;
import org.ai.carp.model.dataset.CARPDataset;
import org.ai.carp.model.dataset.IMPDataset;
import org.ai.carp.model.dataset.ISEDataset;
import org.springframework.util.StringUtils;

import java.util.Optional;

public class DatasetUtils {

    public static BaseDataset apiGetById(String did) {
        if (StringUtils.isEmpty(did)) {
            throw new InvalidRequestException("No dataset id!");
        }
        BaseDataset dataset = DatasetUtils.findById(did);
        if (dataset == null) {
            throw new InvalidRequestException("Invalid dataset!");
        }
        return dataset;
    }

    public static BaseDataset findById(String did) {
        Optional<CARPDataset> optionalCARPDataset = Database.getInstance().getCarpDatasets().findById(did);
        if (optionalCARPDataset.isPresent()) {
            return optionalCARPDataset.get();
        }
        Optional<ISEDataset> optionalISEDataset = Database.getInstance().getIseDatasets().findById(did);
        if (optionalISEDataset.isPresent()) {
            return optionalISEDataset.get();
        }
        Optional<IMPDataset> optionalIMPDataset = Database.getInstance().getImpDatasets().findById(did);
        return optionalIMPDataset.orElse(null);
    }

    public static BaseDataset findByName(String name) {
        CARPDataset carpDataset = Database.getInstance().getCarpDatasets().findDatasetByName(name);
        if (carpDataset != null) {
            return carpDataset;
        }
        ISEDataset iseDataset = Database.getInstance().getIseDatasets().findDatasetByName(name);
        if (iseDataset != null) {
            return iseDataset;
        }
        IMPDataset impDataset = Database.getInstance().getImpDatasets().findDatasetByName(name);
        if (impDataset != null) {
            return impDataset;
        }
        return null;
    }
    
}
