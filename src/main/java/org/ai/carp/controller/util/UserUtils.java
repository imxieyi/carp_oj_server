package org.ai.carp.controller.util;

import org.ai.carp.controller.exceptions.NotLoggedInException;
import org.ai.carp.controller.exceptions.PermissionDeniedException;
import org.ai.carp.model.Database;
import org.ai.carp.model.user.User;

import javax.servlet.http.HttpSession;
import java.util.Optional;

public class UserUtils {

    public static User getUser(HttpSession session, int maxType) {
        String uid = (String) session.getAttribute("uid");
        if (maxType < User.GUEST && uid == null) {
            throw new NotLoggedInException("Not logged in!");
        }
        if (maxType >= User.GUEST && uid == null) {
            return User.GUEST_USER;
        }
        Optional<User> optionalUser = Database.getInstance().getUsers().findById(uid);
        if (!optionalUser.isPresent()) {
            session.invalidate();
            throw new PermissionDeniedException("User does not exist!");
        }
        if (optionalUser.get().getType() > maxType) {
            throw new PermissionDeniedException("Permission denied!");
        }
        return optionalUser.get();
    }

}
