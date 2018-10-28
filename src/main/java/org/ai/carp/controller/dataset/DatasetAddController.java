package org.ai.carp.controller.dataset;

import org.ai.carp.controller.util.ResponseBase;
import org.ai.carp.controller.util.UserUtils;
import org.ai.carp.model.Database;
import org.ai.carp.model.dataset.Dataset;
import org.ai.carp.model.user.User;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/dataset/add")
public class DatasetAddController {

    @PostMapping
    public ResponseBase post(@RequestBody PostDataset dataset, HttpSession session) {
        Object opt = UserUtils.getUser(session, User.ADMIN);
        if (opt instanceof ResponseBase) {
            return (ResponseBase)opt;
        }
        if (StringUtils.isEmpty(dataset.name)) {
            return new DatasetAddResponse("No name specified!");
        }
        if (Database.getInstance().getDatasets().findDatasetByName(dataset.name) != null) {
            return new DatasetAddResponse("Dataset name already exists!");
        }
        if (StringUtils.isEmpty(dataset.data)) {
            return new DatasetAddResponse("No data!");
        }
        if (dataset.cpu <= 0) {
            return new DatasetAddResponse("No cpu!");
        }
        if (dataset.memory <= 0) {
            return new DatasetAddResponse("No memory!");
        }
        if (dataset.time <= 0) {
            return new DatasetAddResponse("No time!");
        }
        Dataset inserted = Database.getInstance().getDatasets().insert(
                new Dataset(dataset.name, dataset.time, dataset.memory, dataset.cpu, dataset.data)
        );
        return new DatasetAddResponse(true, null, inserted.getId());
    }

}

class DatasetAddResponse extends ResponseBase {

    private String id;

    public DatasetAddResponse(String reason) {
        this(false, reason, null);
    }

    public DatasetAddResponse(boolean ok, String reason, String id) {
        super(ok, reason);
        this.id = id;
    }

    public String getId() {
        return id;
    }
}

class PostDataset {
    public String name;
    public int time;
    public int memory;
    public int cpu;
    public String data;
}
