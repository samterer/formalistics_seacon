package ph.com.gs3.formalistics.model.values.business;

import java.io.Serializable;

public class User implements Serializable {

    public static final int ACTIVE = 1;
    public static final int INACTIVE = 0;

    private int id;
    private int webId;
    private String email;
    private String displayName;

    private Company company;

    private int positionId;
    private String positionName;
    private String departmentPositionId;

    private String imageURL;
    private String userLevelId;
    private String password;

    private String formsLastUpdateDate;
    private int isActive;

    @Override
    public String toString() {
        return "User: " + webId + "-" + displayName;
    }

    public boolean hasSameIdentityWith(User user) {

        if (this.webId == user.webId) {
            return false;
        }

        if (!this.email.equals(user.email)) {
            return false;
        }

        return this.company.getId() == user.company.getId();

    }

    @Override
    public boolean equals(Object object) {

        User compareTo;

        try {
            compareTo = (User) object;
        } catch (ClassCastException e) {
            return false;
        }

        if (this.webId == compareTo.webId) {
            return false;
        }

        // if (!this.email.equals(compareTo.email)) {
        // return false;
        // }

        if (this.company.getId() != compareTo.company.getId()) {
            return false;
        }

        if (!this.imageURL.equals(compareTo.imageURL)) {
            return false;
        }

        // if (!this.userLevelId.equals(compareTo.userLevelId)) {
        // return false;
        // }

        return true;

    }

    // =========================================================================
    // {{ Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWebId() {
        return webId;
    }

    public void setWebId(int webId) {
        this.webId = webId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public int getPositionId() {
        return positionId;
    }

    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }

    public String getDepartmentPositionId() {
        return departmentPositionId;
    }

    public void setDepartmentPositionId(String departmentPositionId) {
        this.departmentPositionId = departmentPositionId;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getUserLevelId() {
        return userLevelId;
    }

    public void setUserLevelId(String userLevelId) {
        this.userLevelId = userLevelId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFormsLastUpdateDate() {
        return formsLastUpdateDate;
    }

    public void setFormsLastUpdateDate(String formsLastUpdateDate) {
        this.formsLastUpdateDate = formsLastUpdateDate;
    }

    public int isActive() {
        return isActive;
    }

    public void setActive(int isActive) {
        this.isActive = isActive;
    }

    // }}

}
