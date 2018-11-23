package org.ai.carp.controller.util;

import org.ai.carp.model.Database;
import org.ai.carp.model.dataset.BaseDataset;
import org.ai.carp.model.dataset.CARPDataset;
import org.ai.carp.model.dataset.IMPDataset;
import org.ai.carp.model.dataset.ISEDataset;

import java.util.Optional;

public class DatasetUtils {

    public static BaseDataset findById(String cid) {
        Optional<CARPDataset> optionalCARPDataset = Database.getInstance().getCarpDatasets().findById(cid);
        if (optionalCARPDataset.isPresent()) {
            return optionalCARPDataset.get();
        }
        Optional<ISEDataset> optionalISEDataset = Database.getInstance().getIseDatasets().findById(cid);
        if (optionalISEDataset.isPresent()) {
            return optionalISEDataset.get();
        }
        Optional<IMPDataset> optionalIMPDataset = Database.getInstance().getImpDatasets().findById(cid);
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
