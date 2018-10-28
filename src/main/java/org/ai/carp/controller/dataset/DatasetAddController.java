package org.ai.carp.controller.dataset;

import org.ai.carp.controller.exceptions.InvalidRequestException;
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
    public DatasetAddResponse post(@RequestBody PostDataset dataset, HttpSession session) {
        UserUtils.getUser(session, User.ADMIN);
        if (StringUtils.isEmpty(dataset.name)) {
            throw new InvalidRequestException("No name specified!");
        }
        if (Database.getInstance().getDatasets().findDatasetByName(dataset.name) != null) {
            throw new InvalidRequestException("Dataset name already exists!");
        }
        if (StringUtils.isEmpty(dataset.data)) {
            throw new InvalidRequestException("No data!");
        }
        if (dataset.cpu <= 0) {
            throw new InvalidRequestException("No cpu!");
        }
        if (dataset.memory <= 0) {
            throw new InvalidRequestException("No memory!");
        }
        if (dataset.time <= 0) {
            throw new InvalidRequestException("No time!");
        }
        Dataset inserted = Database.getInstance().getDatasets().insert(
                new Dataset(dataset.name, dataset.time, dataset.memory, dataset.cpu, dataset.data)
        );
        return new DatasetAddResponse(inserted.getId());
    }

}

class DatasetAddResponse {

    private String id;

    public DatasetAddResponse(String id) {
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
