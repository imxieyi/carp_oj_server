package org.ai.carp.controller.dataset;

import org.ai.carp.controller.util.ResponseBase;
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
    public ResponseBase get(HttpSession session) {
        Object opt = UserUtils.getUser(session, User.USER);
        if (opt instanceof ResponseBase) {
            return (ResponseBase) opt;
        }
        List<Dataset> datasets = Database.getInstance().getDatasets().findAll();
        return new DatasetAllResponse(datasets);
    }

}

class DatasetAllResponse extends ResponseBase {
    private List<Dataset> datasets;

    public DatasetAllResponse(String reason) {
        this(false, reason, null);
    }

    public DatasetAllResponse(List<Dataset> datasets) {
        this(true, "", datasets);
    }

    public DatasetAllResponse(boolean ok, String reason, List<Dataset> datasets) {
        super(ok, reason);
        this.datasets = datasets;
    }

    public List<Dataset> getDatasets() {
        return datasets;
    }
}
