package org.ai.carp.controller.judge;

import org.ai.carp.controller.util.CaseUtils;
import org.ai.carp.controller.util.UserUtils;
import org.ai.carp.model.user.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/judge/remain")
public class RemainSubmitController {

    private static final int DAILY_LIMIT = 10;

    @GetMapping
    public RemainSubmitResponse get(HttpSession session) {
        User user = UserUtils.getUser(session, User.USER);
        int count = CaseUtils.countPreviousDay(user);
        return new RemainSubmitResponse(DAILY_LIMIT - count, DAILY_LIMIT);
    }

}

class RemainSubmitResponse {

    private int remain;
    private int total;

    public RemainSubmitResponse(int remain, int total) {
        this.remain = remain;
        this.total = total;
    }

    public int getRemain() {
        return remain;
    }

    public int getTotal() {
        return total;
    }
}
