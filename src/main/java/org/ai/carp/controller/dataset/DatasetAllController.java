package org.ai.carp.controller.dataset;

import org.ai.carp.model.Database;
import org.ai.carp.model.dataset.BaseDataset;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/dataset/all")
public class DatasetAllController {

    @GetMapping
    public DatasetAllResponse get(HttpSession session) {
        List<BaseDataset> datasets = new ArrayList<>();
        datasets.addAll(Database.getInstance().getCarpDatasets().findAll());
        datasets.addAll(Database.getInstance().getIseDatasets().findAll());
        datasets.addAll(Database.getInstance().getImpDatasets().findAll());
        return new DatasetAllResponse(datasets);
    }

}

class DatasetAllResponse {
    private List<BaseDataset> datasets;

    public DatasetAllResponse(List<BaseDataset> datasets) {
        this.datasets = datasets;
    }

    public List<BaseDataset> getDatasets() {
        return datasets;
    }
}
