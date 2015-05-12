package ph.com.gs3.formalistics.service.synchronizers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ph.com.gs3.formalistics.global.constants.UserUpdateOptions;
import ph.com.gs3.formalistics.model.dao.UsersDAO;
import ph.com.gs3.formalistics.model.dao.facade.UsersDataWriterFacade;
import ph.com.gs3.formalistics.model.values.business.User;

public class UsersSynchronizer {

    public static final String TAG = UsersSynchronizer.class.getSimpleName();

    private final UsersDAO usersDAO;
    private final UsersDataWriterFacade usersDataWriterFacade;
    private Map<Integer, User> userCache;

    private final User activeUser;

    public UsersSynchronizer(Context context, User activeUser) {

        this.activeUser = activeUser;
        usersDAO = new UsersDAO(context);
        usersDataWriterFacade = new UsersDataWriterFacade(usersDAO);

    }

    public UsersSynchronizer(Context context, SQLiteDatabase preOpenedDatabaseWithTransaction, User activeUser) {

        this.activeUser = activeUser;
        usersDAO = new UsersDAO(context, preOpenedDatabaseWithTransaction);
        usersDataWriterFacade = new UsersDataWriterFacade(usersDAO);

    }

    /**
     * Loads the user cache if it is not loaded yet.
     */
    protected void lazyLoadUserCache() {
        if (userCache == null) {
            loadUserCache();
        }
    }

    /**
     * Queries the database for all the users under the same company as the current user
     * and places it to the user cache (userCache).
     */
    protected void loadUserCache() {
        List<User> users = usersDAO.getCompanyUsers(activeUser.getCompany().getId());

        userCache = new HashMap<>();

        for (User user : users) {
            userCache.put(user.getWebId(), user);
        }
    }

    /**
     * Updates the user in the database if there are changes to it. Otherwise, this will
     * just return the user from the database with updates. In case that the user is not
     * yet registered, this method will register the user and return its data.
     *
     * @param user          The user to update
     * @param updateOptions Determines how the user is updated in the database
     * @return The data of the user from the database (including database id)
     */
    public User updateUser(User user, EnumSet<UserUpdateOptions> updateOptions) {
        lazyLoadUserCache();

        int webId = user.getWebId();

        // Find the user in the cache, if it is already cached and it's values are equal
        // to the cached user instance, return that instance and do not do any updates.
        if (userCache.containsKey(webId)) {
            User cachedUser = userCache.get(webId);

            if (cachedUser.equals(user)) {
                return cachedUser;
            }
        }

        // The user is either not yet registered or needs an update
        return usersDataWriterFacade.registerOrUpdateUser(user, updateOptions);

    }

}
