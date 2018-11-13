package org.ai.carp.controller.admin.judge;

import org.ai.carp.controller.exceptions.InvalidRequestException;
import org.ai.carp.controller.util.UserUtils;
import org.ai.carp.model.Database;
import org.ai.carp.model.judge.CARPCase;
import org.ai.carp.model.user.User;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/judge/output")
public class GetOutputController {

    @GetMapping
    public OutputResponse get(@RequestParam("cid") String cid, HttpSession session) {
        UserUtils.getUser(session, User.ADMIN);
        if (StringUtils.isEmpty(cid)) {
            throw new InvalidRequestException("No cid!");
        }
        Optional<CARPCase> optCarpCase = Database.getInstance().getCarpCases().findById(cid);
        if (!optCarpCase.isPresent()) {
            throw new InvalidRequestException("Case does not exist!");
        }
        CARPCase carpCase = optCarpCase.get();
        if (carpCase.getStatus() != CARPCase.FINISHED && carpCase.getStatus() != CARPCase.ERROR) {
            throw new InvalidRequestException("Case has not finished!");
        }
        return new OutputResponse(carpCase.getExitcode(), carpCase.getStdout(), carpCase.getStderr());
    }

}

class OutputResponse {

    private int exitcode;
    private String stdout;
    private String stderr;

    public OutputResponse(int exitcode, String stdout, String stderr) {
        this.exitcode = exitcode;
        this.stdout = stdout;
        this.stderr = stderr;
    }

    public int getExitcode() {
        return exitcode;
    }

    public String getStdout() {
        return stdout;
    }

    public String getStderr() {
        return stderr;
    }
}
