package ph.com.gs3.formalistics.model.dao.facade;

import android.content.Context;
import android.database.sqlite.SQLiteException;

import java.util.EnumSet;

import ph.com.gs3.formalistics.global.constants.UserUpdateOptions;
import ph.com.gs3.formalistics.model.dao.UsersDAO;
import ph.com.gs3.formalistics.model.values.business.User;

/**
 * Created by Ervinne on 4/7/2015.
 */
public class UsersDataWriterFacade {

    private final UsersDAO usersDAO;

    public UsersDataWriterFacade(Context context) {
        usersDAO = new UsersDAO(context);
    }

    public UsersDataWriterFacade(UsersDAO usersDAO) {
        this.usersDAO = usersDAO;
    }

    public User registerOrUpdateUser(User user) {
        return registerOrUpdateUser(user, EnumSet.of(UserUpdateOptions.UPDATE_ALL));
    }

    public User registerOrUpdateUser(User user, EnumSet<UserUpdateOptions> updateOptions) {

        User registeredUser = null;

        // Check first if there is already a user with the same web id and company
        User existingUser = usersDAO.getUserWithWebAndCompanyId(user.getWebId(), user.getCompany().getId());

        if (existingUser == null) {
            // Save a new user
            registeredUser = usersDAO.saveUser(user);
        } else {
            // Update the user using the new user's data
            int existingUserId = existingUser.getId();

            try {
                usersDAO.updateUser(existingUserId, user, updateOptions);
                registeredUser = usersDAO.getUserWithId(existingUserId);
            } catch (SQLiteException e) {
                // Unrecoverable error
                throw new RuntimeException(e);
            }
        }

        return registeredUser;

    }

}
