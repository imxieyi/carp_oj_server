package org.ai.carp.controller.dataset;

import org.ai.carp.controller.util.UserUtils;
import org.ai.carp.model.Database;
import org.ai.carp.model.dataset.Dataset;
import org.ai.carp.model.user.User;
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
        UserUtils.getUser(session, User.USER);
        List<Dataset> datasets = Database.getInstance().getDatasets().findAll();
        return new DatasetAllResponse(datasets);
    }

}

class DatasetAllResponse {
    private List<Dataset> datasets;

    public DatasetAllResponse(List<Dataset> datasets) {
        this.datasets = datasets;
    }

    public List<Dataset> getDatasets() {
        return datasets;
    }
}
