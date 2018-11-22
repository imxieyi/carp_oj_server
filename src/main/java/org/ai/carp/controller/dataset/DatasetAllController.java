package org.ai.carp.controller.dataset;

import org.ai.carp.model.Database;
import org.ai.carp.model.dataset.CARPDataset;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/dataset/all")
public class DatasetAllController {

    @GetMapping
    public DatasetAllResponse get(HttpSession session) {
        List<CARPDataset> datasets = Database.getInstance().getDatasets().findAll();
        return new DatasetAllResponse(datasets);
    }

}

class DatasetAllResponse {
    private List<CARPDataset> datasets;

    public DatasetAllResponse(List<CARPDataset> datasets) {
        this.datasets = datasets;
    }

    public List<CARPDataset> getDatasets() {
        return datasets;
    }
}
