package org.ai.carp.controller.judge;

import org.ai.carp.controller.exceptions.InvalidRequestException;
import org.ai.carp.controller.util.CaseUtils;
import org.ai.carp.controller.util.UserUtils;
import org.ai.carp.model.judge.BaseCase;
import org.ai.carp.model.user.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/api/judge/archive", produces = "application/zip")
public class GetArchiveController {

    @GetMapping
    public ResponseEntity<byte[]> get(@RequestParam("cid") String cid, HttpSession session) {
        User user = UserUtils.getUser(session, User.USER);
        if (StringUtils.isEmpty(cid)) {
            throw new InvalidRequestException("No cid!");
        }
        BaseCase baseCase = CaseUtils.findById(cid);
        if (baseCase == null || (user.getType() > User.ADMIN && !user.getId().equals(baseCase.getUserId()))) {
            throw new InvalidRequestException("Case does not exist!");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "zip"));
        headers.setContentDispositionFormData("attachment", cid + ".zip");
        headers.setContentLength(baseCase.getArchive().length());
        return new ResponseEntity<>(baseCase.getArchive().getData(), headers, HttpStatus.OK);
    }

}
