package org.ai.carp.controller.admin.user;

import org.ai.carp.model.user.User;

public class ValidTypes {

    public static final int[] ROOT = {
            User.ADMIN, User.WORKER, User.USER
    };
    public static final int[] ADMIN = {
            User.USER
    };

}
