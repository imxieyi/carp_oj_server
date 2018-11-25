package org.ai.carp.controller.admin.judge;

import org.ai.carp.controller.util.UserUtils;
import org.ai.carp.model.Database;
import org.ai.carp.model.judge.CARPCase;
import org.ai.carp.model.user.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping(value = "/api/admin/judge/archive/carpall", produces = "application/zip")
public class GetCARPArchives {

    @GetMapping
    public ResponseEntity<byte[]> get(HttpSession session) throws IOException {
        UserUtils.getUser(session, User.ADMIN);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        Date endTime = new Date(1542964624000L);
        // Query users
        List<User> users = Database.getInstance().getUsers().findAllByType(User.USER);
        for (User u : users) {
            CARPCase submission = Database.getInstance().getCarpCases()
                    .findFirstByUserAndValidAndSubmitTimeBeforeOrderBySubmitTimeDesc(u, true, endTime);
            if (submission == null || submission.getArchive() == null || !submission.isValid()) {
                continue;
            }
            ZipEntry entry = new ZipEntry(u.getUsername() + ".zip");
            zos.putNextEntry(entry);
            zos.write(submission.getArchive().getData());
            zos.closeEntry();
        }
        zos.close();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "zip"));
        headers.setContentDispositionFormData("attachment", "carp.zip");
        headers.setContentLength(baos.size());
        return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
    }

}
