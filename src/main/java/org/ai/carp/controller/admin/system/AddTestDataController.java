package org.ai.carp.controller.admin.system;

import org.ai.carp.controller.util.UserUtils;
import org.ai.carp.model.Database;
import org.ai.carp.model.dataset.Dataset;
import org.ai.carp.model.user.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/api/admin/system/addtestdata")
public class AddTestDataController {

    @GetMapping
    public String get(HttpSession session) throws IOException {
        UserUtils.getUser(session, User.ROOT);
        Database.getInstance().getUsers().insert(new User("admin", "123", User.ADMIN));
        Database.getInstance().getUsers().insert(new User("judge", "123", User.WORKER));
        Database.getInstance().getUsers().insert(new User("user", "123", User.USER));
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = classLoader.getResourceAsStream("gdb10.dat");
        byte[] bytes = new byte[is.available()];
        is.read(bytes);
        is.close();
        String data = new String(bytes);
        Dataset dataset = new Dataset("gdb10", 10, 256, 1, data);
        Database.getInstance().getDatasets().insert(dataset);
        return "done";
    }

}
