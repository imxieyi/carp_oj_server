package org.ai.carp.controller.admin.user;

import org.ai.carp.model.user.User;

public class ValidTypes {

    public static final int[] ROOT = {
            User.ADMIN, User.USER, User.WORKER
    };
    public static final int[] ADMIN = {
            User.USER
    };

}
