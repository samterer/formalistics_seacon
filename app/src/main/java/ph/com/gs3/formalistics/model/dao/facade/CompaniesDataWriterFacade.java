package ph.com.gs3.formalistics.model.dao.facade;

import android.content.Context;
import android.database.sqlite.SQLiteException;

import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.dao.CompaniesDAO;
import ph.com.gs3.formalistics.model.values.business.Company;

/**
 * Created by Ervinne on 4/11/2015.
 */
public class CompaniesDataWriterFacade {

    public static final String TAG = CompaniesDataWriterFacade.class.getSimpleName();

    private CompaniesDAO companiesDAO;

    public CompaniesDataWriterFacade(Context context) {
        companiesDAO = new CompaniesDAO(context);
    }

    public Company registerCompany(Company company) {

        Company registeredCompany;

        Company existingCompany = companiesDAO.getCompany(company.getWebId(), company.getServer());

        if (existingCompany == null) {
            try {
                FLLogger.d(TAG, "Registering new company " + company.toString());
                registeredCompany = companiesDAO.saveCompany(company);
            } catch (SQLiteException e) {
                // Unrecoverable error
                throw new RuntimeException(e);
            }
        } else {
            registeredCompany = existingCompany;
        }

        return registeredCompany;
    }

}
