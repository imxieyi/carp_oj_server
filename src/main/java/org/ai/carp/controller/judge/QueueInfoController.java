package org.ai.carp.controller.judge;

import org.ai.carp.controller.util.UserUtils;
import org.ai.carp.model.user.User;
import org.ai.carp.runner.JudgeRunner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/judge/queue")
public class QueueInfoController {

    @GetMapping
    public QueueInfoResponse get(HttpSession session) {
        UserUtils.getUser(session, User.USER);
        return new QueueInfoResponse(JudgeRunner.queue.size());
    }

}

class QueueInfoResponse {

    private int length;

    public QueueInfoResponse(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }
}
