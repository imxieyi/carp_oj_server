package org.ai.carp.controller.util;

import org.ai.carp.model.Database;
import org.ai.carp.model.user.User;

import javax.servlet.http.HttpSession;
import java.util.Optional;

public class UserUtils {

    public static Object getUser(HttpSession session, int maxType) {
        String uid = (String) session.getAttribute("uid");
        if (uid == null) {
            return new ResponseBase("Not logged in!");
        }
        Optional<User> optionalUser = Database.getInstance().getUsers().findById(uid);
        if (!optionalUser.isPresent()) {
            session.invalidate();
            return new ResponseBase("User does not exist!");
        }
        if (optionalUser.get().getType() > maxType) {
            return new ResponseBase("Permission denied!");
        }
        return optionalUser.get();
    }

}
