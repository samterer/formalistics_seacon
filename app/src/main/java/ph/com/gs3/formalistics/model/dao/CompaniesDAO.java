package ph.com.gs3.formalistics.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import ph.com.gs3.formalistics.model.tables.CompaniesTable;
import ph.com.gs3.formalistics.model.values.business.Company;

/**
 * Created by Ervinne on 4/7/2015.
 */
public class CompaniesDAO extends DataAccessObject {

    public CompaniesDAO(Context context) {
        super(context);
    }

    /**
     * @param webId
     * @param name
     * @return
     */
    public Company updateCompanyServer(int webId, String name, String newServer) {
        ContentValues cv = new ContentValues();
        cv.put(CompaniesTable.COL_SERVER, newServer);

        Company updatedCompany = null;

        String whereClause = CompaniesTable.COL_WEB_ID + "=? AND "
                + CompaniesTable.COL_NAME + "=?";
        String[] whereArgs = {Integer.toString(webId), name};

        try {
            open();
            int updatedCompanyCount = database.update(CompaniesTable.NAME, cv, whereClause, whereArgs);
            if (updatedCompanyCount > 0) {
                updatedCompany = getCompany(webId, newServer);
            }
        } finally {
            close();
        }

        return updatedCompany;
    }

    public Company saveCompany(Company company) throws SQLiteException {

        ContentValues cv = createCVFromCompany(company);

        try {
            open();
            long insertId = database.insert(CompaniesTable.NAME, null, cv);

            if (insertId <= 0) {
                throw new SQLiteException("Failed inserting company.");
            }
            return getCompany((int) insertId);
        } finally {
            close();
        }
    }

    public Company getCompany(int id) {

        String whereClause = CompaniesTable.COL_ID + " = ?";
        String[] whereArgs = {Integer.toString(id)};

        try {
            open();
            Cursor cursor = database.query(CompaniesTable.NAME, CompaniesTable.COLUMN_COLLECTION,
                    whereClause, whereArgs, null, null, null);

            cursor.moveToFirst();

            Company company = null;
            if (!cursor.isAfterLast()) {
                company = cursorToCompany(cursor);
            }
            cursor.close();

            return company;
        } finally {
            close();
        }
    }

    public Company getSimilarCompanyFromDifferentServer(Company company) {

        int webId = company.getWebId();
        String companyName = company.getName();
        String server = company.getServer();

        String whereClause = CompaniesTable.COL_WEB_ID + "=? AND "
                + CompaniesTable.COL_NAME + "=? AND "
                + CompaniesTable.COL_SERVER + "!=?";
        String[] whereArgs = {Integer.toString(webId), companyName, server};

        try {
            open();

            Cursor cursor = database.query(CompaniesTable.NAME, CompaniesTable.COLUMN_COLLECTION,
                    whereClause, whereArgs, null, null, null);

            cursor.moveToFirst();

            Company similarCompany = null;
            if (!cursor.isAfterLast()) {
                similarCompany = cursorToCompany(cursor);
            }
            cursor.close();

            return similarCompany;
        } finally {
            close();
        }

    }

    public Company getCompany(int webId, String server) {

        // @formatter:off
		String whereClause 	= CompaniesTable.COL_WEB_ID + " = ? AND "
							+ CompaniesTable.COL_SERVER + " = ?";
		String[] whereArgs 	= { Integer.toString(webId), server };
		// @formatter:on

        try {
            open();
            Cursor cursor = database.query(CompaniesTable.NAME, CompaniesTable.COLUMN_COLLECTION,
                    whereClause, whereArgs, null, null, null);

            cursor.moveToFirst();

            Company company = null;
            if (!cursor.isAfterLast()) {
                company = cursorToCompany(cursor);
            }
            cursor.close();

            return company;
        } finally {
            close();
        }
    }

    private ContentValues createCVFromCompany(Company company) {

        ContentValues cv = new ContentValues();

        cv.put(CompaniesTable.COL_WEB_ID, company.getWebId());
        cv.put(CompaniesTable.COL_NAME, company.getName());
        cv.put(CompaniesTable.COL_SERVER, company.getServer());

        return cv;

    }

    private Company cursorToCompany(Cursor cursor) {

        // @formatter:off
		int idIndex 	= cursor.getColumnIndexOrThrow(CompaniesTable.COL_ID);
		int webIdIndex 	= cursor.getColumnIndexOrThrow(CompaniesTable.COL_WEB_ID);
		int nameIndex 	= cursor.getColumnIndexOrThrow(CompaniesTable.COL_NAME);
		int serverIndex = cursor.getColumnIndexOrThrow(CompaniesTable.COL_SERVER);

		int id 			= cursor.getInt(idIndex);
		int webId 	    = cursor.getInt(webIdIndex);
		String name 	= cursor.getString(nameIndex);
		String server 	= cursor.getString(serverIndex);
		// @formatter:on

        Company company = new Company();

        company.setId(id);
        company.setWebId(webId);
        company.setName(name);
        company.setServer(server);

        return company;

    }

    public static Company cursorToCompanyWithAliasedColumns(Cursor cursor) {

        // @formatter:off
		int idIndex 	= cursor.getColumnIndexOrThrow("company" + CompaniesTable.COL_ID);
		int webIdIndex 	= cursor.getColumnIndexOrThrow("company_" + CompaniesTable.COL_WEB_ID);
		int nameIndex 	= cursor.getColumnIndexOrThrow("company_" + CompaniesTable.COL_NAME);
        int serverIndex = cursor.getColumnIndexOrThrow(CompaniesTable.COL_SERVER);

		int id 			= cursor.getInt(idIndex);
        int webId 	    = cursor.getInt(webIdIndex);
		String name 	= cursor.getString(nameIndex);
		String server 	= cursor.getString(serverIndex);
		// @formatter:on

        Company company = new Company();

        company.setId(id);
        company.setWebId(webId);
        company.setName(name);
        company.setServer(server);

        return company;
    }

}
