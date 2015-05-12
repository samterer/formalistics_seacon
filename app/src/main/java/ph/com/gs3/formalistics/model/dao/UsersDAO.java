package ph.com.gs3.formalistics.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

import ph.com.gs3.formalistics.global.constants.UserUpdateOptions;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.tables.CompaniesTable;
import ph.com.gs3.formalistics.model.tables.UsersTable;
import ph.com.gs3.formalistics.model.values.business.User;

/**
 * Created by Ervinne on 4/7/2015.
 */
public class UsersDAO extends DataAccessObject {

    public static final String TAG = UsersDAO.class.getSimpleName();

    /**
     * Represents (last) update fields used for determining when's the last time the
     * forms, documents, or comments are updated for this user.
     */
    public enum UpdateField {
        ALL_FORMS, ALL_DOCUMENTS, ALL_COMMENTS
    }

    public UsersDAO(Context context) {
        super(context);
    }

    public UsersDAO(Context context, SQLiteDatabase preOpenedDatabaseWithTransaction) {
        super(context, preOpenedDatabaseWithTransaction);
    }

// <editor-fold desc="Query Methods">

    /**
     * Gets the currently active user account saved in this device. If no such use is
     * found, null is returned.
     *
     * @return a User object containing the data of the active user if it's found,
     * otherwise, null.
     */
    public User getActiveUser() {
        String whereClause = String.format("u.is_active = %d", User.ACTIVE);
        return getUser(whereClause);
    }

    public User getUserWithWebAndCompanyId(int webId, int companyId) {
        String whereClause = String.format(Locale.ENGLISH, "u.web_id = %d AND u.company_id = %d", webId, companyId);
        return getUser(whereClause);
    }

    public User getSimilarUserFromDifferentServer(User user) {

        // @formatter:off
        String whereClause
                = "u." + UsersTable.COL_WEB_ID      + "=%d AND "
                + "u." + UsersTable.COL_EMAIL       + "='%s' AND "
                + "c." + CompaniesTable.COL_WEB_ID  + "=%d AND "
                + "c." + CompaniesTable.COL_NAME    + "='%s' AND "
                + "c." + CompaniesTable.COL_SERVER  + "!='%s'";
        whereClause = String.format(whereClause,
                user.getWebId(),
                user.getEmail(),
                user.getCompany().getWebId(),
                user.getCompany().getName(),
                user.getCompany().getServer()
        );
        // @formatter:on

        return getUser(whereClause);
    }

    /**
     * Gets a user by its local database id.
     *
     * @param id the id of the user to get.
     * @return a User object containing the data of the active user if it's found,
     * otherwise, null.
     */
    public User getUserWithId(int id) {
        String whereClause = String.format(Locale.ENGLISH, "u._id = %d", id);
        return getUser(whereClause);
    }

    public List<User> getCompanyUsers(int companyId) {
        String whereClause = String.format("u.company_id = %d", companyId);
        return getUsers(whereClause);
    }

    private User getUser(String whereClause) {
        List<User> users = getUsers(whereClause);
        if (users.size() > 0) {
            return users.get(0);
        } else {
            return null;
        }
    }

    private List<User> getUsers(String whereClause) {

        // @formatter:off
		String query = "SELECT "
							+ "u._id, u.web_id, u.email, u.display_name, u.image_url, "
							+ "u.user_level_id, u.forms_last_update_date, u.password, u.is_active, "
							+ "c._id AS company_id, c.web_id AS company_web_id, "
							+ "u.position_id, u.department_position_level_id, u.position_name, "
							+ "c.name AS company_name, c.server "
						+ "FROM Users u "
						+ "LEFT JOIN Companies c ON u.company_id = c._id "
						+ "WHERE " + whereClause;
        // @formatter:on

        List<User> users = new ArrayList<>();

        try {
            open();

            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {

                User user = cursorToUser(cursor);
                user.setCompany(CompaniesDAO.cursorToCompanyWithAliasedColumns(cursor));
                users.add(user);

                cursor.moveToNext();

            }

            return users;
        } finally {
            close();
        }

    }

    // </editor-fold>

    // <editor-fold desc="Update & Insert Methods" defaultState=collapsed>

    public User saveUser(User user) {

        ContentValues cv = createCVFromUser(user);

        try {
            open();

            int insertId = (int) database.insert(UsersTable.NAME, null, cv);
            // log any possible errors
            if (insertId == -1) {    // if no data is inserted
                FLLogger.e(TAG, "No user data was inserted to database upon registration. User data is: " + cv.toString());
            }

            return getUserWithId(insertId);
        } finally {
            close();
        }

    }

    public void updateUser(int dbId, User newUserData) throws SQLiteException {
        updateUser(dbId, newUserData, EnumSet.of(UserUpdateOptions.UPDATE_ALL));
    }

    public void updateUser(int dbId, User newUserData, EnumSet<UserUpdateOptions> updateOptions)
            throws SQLiteException {

        ContentValues cv = createCVFromUser(newUserData);

        if (updateOptions.contains(UserUpdateOptions.UPDATE_EXCEPT_PASSWORD)) {
            cv.remove(UsersTable.COL_PASSWORD);
        }

        if (updateOptions.contains(UserUpdateOptions.UPDATE_EXCEPT_IS_ACTIVE)) {
            cv.remove(UsersTable.COL_IS_ACTIVE);
        }

        String whereClause = UsersTable.COL_ID + " = ?";
        String[] whereArgs = {Integer.toString(dbId)};

        try {
            open();
            int affectedRows = database.update(UsersTable.NAME, cv, whereClause, whereArgs);

            if (affectedRows <= 0) {
                throw new SQLiteException("Failed updating user with id " + dbId + ". No records updated");
            }

            if (affectedRows > 1) {
                FLLogger.w(TAG, "There are more than one user records affected by the recent update done to user with id " + dbId);
            }
        } finally {
            close();
        }

    }


    public void setFormsLastUpdate(int dbId, String lastUpdateDateString)
            throws SQLiteException {

        ContentValues cv = new ContentValues();
        cv.put(UsersTable.COL_FORMS_LAST_UPDATE_DATE, lastUpdateDateString);

        String whereClause = UsersTable.COL_ID + " = ?";
        String[] whereArgs = {Integer.toString(dbId)};

        try {
            open();
            int affectedRows = database.update(UsersTable.NAME, cv, whereClause, whereArgs);
            if (affectedRows <= 0) {
                throw new SQLiteException();
            }
        } finally {
            close();
        }
    }

    /**
     * Marks the specified user as active in the database and deactivates all other users.
     *
     * @param userId
     * @return
     * @throws SQLiteException
     */
    public void activateUser(int userId) throws SQLiteException {

        ContentValues activeUsersCV = new ContentValues();
        ContentValues inactiveUsersCV = new ContentValues();

        activeUsersCV.put(UsersTable.COL_IS_ACTIVE, User.ACTIVE);
        inactiveUsersCV.put(UsersTable.COL_IS_ACTIVE, User.INACTIVE);

        String whereClause = UsersTable.COL_ID + " = ?";
        String[] whereArgs = {Integer.toString(userId)};

        try {
            open();
            int affectedRows = database.update(UsersTable.NAME, activeUsersCV, whereClause, whereArgs);
            if (affectedRows <= 0) {
                // Unable to update anything, the record of the user is not found.
                throw new SQLiteException("Failed to activate user with id = " + userId
                        + ", the specified user is not found.");
            } else {
                // Deactivate remaining users
                String deactivateWhereClause = UsersTable.COL_ID + " != ?";
                database.update(UsersTable.NAME, inactiveUsersCV, deactivateWhereClause, whereArgs);
            }
        } finally {
            close();
        }

    }

    public void deactivateAllUsers() {

        ContentValues cv = new ContentValues();

        cv.put(UsersTable.COL_IS_ACTIVE, User.INACTIVE);

        try {
            open();
            int affectedRows = database.update(UsersTable.NAME, cv, null, null);
            if (affectedRows <= 0) {
                FLLogger.w(TAG, "Warning, no user deactivated");
            }
        } finally {
            close();
        }
    }

    // </editor-fold>

    // <editor-fold desc="Parser Methods" defaultState="collapsed">

    /**
     * Creates a new ContentValues object based on the passed user object. The keys are
     * based on the columns of the Users table. The created ContentValues object does not
     * contain the id of the user object, only the web_id so that the ContentValues object
     * can be ready for insert.
     *
     * @param user the user object where the new ContentValues object will be based on.
     * @return
     */
    public ContentValues createCVFromUser(User user) {

        ContentValues cv = new ContentValues();

        // Note: the id is intentionally not set, it's auto generated

        cv.put(UsersTable.COL_WEB_ID, user.getWebId());
        cv.put(UsersTable.COL_EMAIL, user.getEmail());
        cv.put(UsersTable.COL_DISPLAY_NAME, user.getDisplayName());

        cv.put(UsersTable.COL_IMAGE_URL, user.getImageURL());
        cv.put(UsersTable.COL_USER_LEVEL_ID, user.getUserLevelId());

        cv.put(UsersTable.COL_POSITION_ID, user.getPositionId());
        cv.put(UsersTable.COL_DEPARTMENT_POSITION_LEVEL_ID, user.getDepartmentPositionId());
        cv.put(UsersTable.COL_POSITION_NAME, user.getPositionName());

        cv.put(UsersTable.COL_FORMS_LAST_UPDATE_DATE, user.getFormsLastUpdateDate());

        cv.put(UsersTable.COL_COMPANY_ID, user.getCompany().getId());

        cv.put(UsersTable.COL_PASSWORD, user.getPassword());
        cv.put(UsersTable.COL_IS_ACTIVE, user.isActive());

        // Note: the update fields are intentionally not set

        return cv;

    }

    private User cursorToUser(Cursor cursor) {

        User user = new User();

        //	@formatter:off
        //	Initialize indices
        int idIndex 			= cursor.getColumnIndexOrThrow(UsersTable.COL_ID);

        int webIdIndex			= cursor.getColumnIndexOrThrow(UsersTable.COL_WEB_ID);
        int emailIndex			= cursor.getColumnIndexOrThrow(UsersTable.COL_EMAIL);
        int displayNameIndex	= cursor.getColumnIndexOrThrow(UsersTable.COL_DISPLAY_NAME);
        int imageURLIndex		= cursor.getColumnIndexOrThrow(UsersTable.COL_IMAGE_URL);

        int companyPositionIdIndex 			= cursor.getColumnIndexOrThrow(UsersTable.COL_POSITION_ID);
        int departmentPositionLevelIdIndex 	= cursor.getColumnIndexOrThrow(UsersTable.COL_DEPARTMENT_POSITION_LEVEL_ID);
        int positionNameIndex   = cursor.getColumnIndexOrThrow(UsersTable.COL_POSITION_NAME);

        int passwordIndex		= cursor.getColumnIndex(UsersTable.COL_PASSWORD);
        int userLevelIndex		= cursor.getColumnIndexOrThrow(UsersTable.COL_USER_LEVEL_ID);

        int lastFormsUpdateIndex	= cursor.getColumnIndexOrThrow(UsersTable.COL_FORMS_LAST_UPDATE_DATE);

        //	Initialize values
        int id				= cursor.getInt(idIndex);
        int webId		    = cursor.getInt(webIdIndex);

        String email		= cursor.getString(emailIndex);
        String displayName 	= cursor.getString(displayNameIndex);
        String imageURL		= cursor.getString(imageURLIndex);

        int companyPositionId 	    = cursor.getInt(companyPositionIdIndex);
        String departmentPositionId = cursor.getString(departmentPositionLevelIdIndex);
        String positionName         = cursor.getString(positionNameIndex);

        String userLevelId	        = cursor.getString(userLevelIndex);
        String lastFormsUpdate      = cursor.getString(lastFormsUpdateIndex);

        //	@formatter:on

        // Set the user values
        user.setId(id);
        user.setWebId(webId);

        user.setEmail(email);
        user.setDisplayName(displayName);
        user.setImageURL(imageURL);

        user.setPositionId(companyPositionId);
        user.setDepartmentPositionId(departmentPositionId);
        user.setPositionName(positionName);

        user.setUserLevelId(userLevelId);

        user.setFormsLastUpdateDate(lastFormsUpdate);

        if (passwordIndex > 0) {
            String password = cursor.getString(passwordIndex);
            user.setPassword(password);
        }

        return user;

    }

    // </editor-fold>

}
