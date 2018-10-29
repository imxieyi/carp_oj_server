package org.ai.carp.controller.util;

import org.ai.carp.controller.exceptions.PermissionDeniedException;
import org.ai.carp.model.Database;
import org.ai.carp.model.user.User;

import javax.servlet.http.HttpSession;
import java.util.Optional;

public class UserUtils {

    public static User getUser(HttpSession session, int maxType) {
        String uid = (String) session.getAttribute("uid");
        if (uid == null) {
            throw new PermissionDeniedException("Not logged in!");
        }
        Optional<User> optionalUser = Database.getInstance().getUsers().findById(uid);
        if (!optionalUser.isPresent()) {
            session.invalidate();
            throw new PermissionDeniedException("User does not exist!");
        }
        if (optionalUser.get().getType() > maxType) {
            throw new PermissionDeniedException("Permission denied!");
        }
        if (optionalUser.get().getType() == User.WORKER && maxType != User.MAX && maxType != User.WORKER) {
            throw new PermissionDeniedException("Not available for worker!");
        }
        return optionalUser.get();
    }

}
