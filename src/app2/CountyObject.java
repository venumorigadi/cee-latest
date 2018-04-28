package app2;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Collection of 44 fields to be appended conditionally on
 * the counties.
 * @author user
 */
public class CountyObject {

    private String uploadDate;
    private String state;
    private String county;
    private String permitNumber;
    private String permitDescription;
    private String permitType;
    private String permitDate;
    private String jobValue;
    private String jobSqFt;
    private String jobAddress;
    private String jobCity;
    private String jobState;
    private String jobZip;
    private String jobSubDivision;
    private String jobLotNumber;
    private String owner;
    private String ownerAddress;
    private String ownerCity;
    private String ownerState;
    private String ownerZip;
    private String ownerPhone;
    private String ownerType;
    private String ownerUrl;
    private String ownerFax;
    private String ownerPrimaryContact;
    private String ownerEmail;
    private String bldCode;
    private String units;
    private String buildings;
    private String ctype;
    private String legal;
    private String contractor;
    private String contractorAddress;
    private String contractorCity;
    private String contractorState;
    private String contractorZip;
    private String contractorPhone;
    private String contractorUrl;
    private String contractorFax;
    private String contractorPrimaryContact;
    private String conotractorEmail;
    private String contractorLastActivity;
    private String contractorStatus;
    private final String comma_separator = ",";
    private final String row_separator = "\n";
    private final String dcv = "[ X ]";
    private final String[] headers = {
        "upload date",
        "state",
        "county",
        "permit number",
        "permit description",
        "permit type",
        "permit date",
        "job value",
        "job square feet",
        "job address",
        "job city",
        "job state",
        "job zip",
        "job subdivision",
        "job lot number",
        "owner",
        "owner address",
        "owner city",
        "owner state",
        "owner zip",
        "owner phone",
        "owner type",
        "owner url",
        "owner fax",
        "owner primary contact",
        "owner email",
        "bldcode",
        "units",
        "buildings",
        "ctype",
        "legal",
        "contractor",
        "contractor address",
        "contractor city",
        "contractor state",
        "contractor zip",
        "contractor phone",
        "contractor url",
        "contractor fax",
        "contractor primary contact",
        "contractor email",
        "contractor last activity",
        "contractor status"
    };

    /**
     *
     * @return
     */
    public String getHeader() {
        CSVBuilder builder = new CSVBuilder();
        int i = 0;
        for (String header : headers) {
            builder.append(header);
            i++;
            if (i == headers.length - 1) {
                builder.append(row_separator);
                i=0;
            } else {
                builder.append(comma_separator);
            }
        }
        return builder.toString();
    }

    /**
     *
     * @return
     */
    public String getRow() {
        CSVBuilder builder = new CSVBuilder();
        builder.append(uploadDate);
        builder.append(comma_separator);
        builder.append(state);
        builder.append(comma_separator);
        builder.append(county);
        builder.append(comma_separator);
        builder.append(permitNumber);
        builder.append(comma_separator);
        builder.append(permitDescription);
        builder.append(comma_separator);
        builder.append(permitType);
        builder.append(comma_separator);
        builder.append(permitDate);
        builder.append(comma_separator);
        builder.append(jobValue);
        builder.append(comma_separator);
        builder.append(jobSqFt);
        builder.append(comma_separator);
        builder.append(jobAddress);
        builder.append(comma_separator);
        builder.append(jobCity);
        builder.append(comma_separator);
        builder.append(jobState);
        builder.append(comma_separator);
        builder.append(jobZip);
        builder.append(comma_separator);
        builder.append(jobSubDivision);
        builder.append(comma_separator);
        builder.append(jobLotNumber);
        builder.append(comma_separator);
        builder.append(owner);
        builder.append(comma_separator);
        builder.append(ownerAddress);
        builder.append(comma_separator);
        builder.append(ownerCity);
        builder.append(comma_separator);
        builder.append(ownerState);
        builder.append(comma_separator);
        builder.append(ownerZip);
        builder.append(comma_separator);
        builder.append(ownerPhone);
        builder.append(comma_separator);
        builder.append(ownerType);
        builder.append(comma_separator);
        builder.append(ownerUrl);
        builder.append(comma_separator);
        builder.append(ownerFax);
        builder.append(comma_separator);
        builder.append(ownerPrimaryContact);
        builder.append(comma_separator);
        builder.append(ownerEmail);
        builder.append(comma_separator);
        builder.append(bldCode);
        builder.append(comma_separator);
        builder.append(units);
        builder.append(comma_separator);
        builder.append(buildings);
        builder.append(comma_separator);
        builder.append(ctype);
        builder.append(comma_separator);
        builder.append(legal);
        builder.append(comma_separator);
        builder.append(contractor);
        builder.append(comma_separator);
        builder.append(contractorAddress);
        builder.append(comma_separator);
        builder.append(contractorCity);
        builder.append(comma_separator);
        builder.append(contractorState);
        builder.append(comma_separator);
        builder.append(contractorZip);
        builder.append(comma_separator);
        builder.append(contractorPhone);
        builder.append(comma_separator);
        builder.append(contractorUrl);
        builder.append(comma_separator);
        builder.append(contractorFax);
        builder.append(comma_separator);
        builder.append(contractorPrimaryContact);
        builder.append(comma_separator);
        builder.append(conotractorEmail);
        builder.append(comma_separator);
        builder.append(contractorLastActivity);
        builder.append(comma_separator);
        builder.append(contractorStatus);
        builder.append(row_separator);
        return builder.toString();
    }

    /**
     *
     */
    public CountyObject() {
        Field[] fields = CountyObject.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                if (field.getType() == String.class) {
                    if (!Modifier.isFinal(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
                        field.setAccessible(true);
                        field.set(this, dcv);
                        field.setAccessible(false);
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace(System.out);
            }
        }
    }

    /**
     *
     * @return
     */
    public String getBldCode() {
        return bldCode;
    }

    /**
     *
     * @return
     */
    public String getBuildings() {
        return buildings;
    }

    /**
     *
     * @return
     */
    public String getConotractorEmail() {
        return conotractorEmail;
    }

    /**
     *
     * @return
     */
    public String getContractor() {
        return contractor;
    }

    /**
     *
     * @return
     */
    public String getContractorAddress() {
        return contractorAddress;
    }

    /**
     *
     * @return
     */
    public String getContractorCity() {
        return contractorCity;
    }

    /**
     *
     * @return
     */
    public String getContractorFax() {
        return contractorFax;
    }

    /**
     *
     * @return
     */
    public String getContractorLastActivity() {
        return contractorLastActivity;
    }

    /**
     *
     * @return
     */
    public String getContractorPhone() {
        return contractorPhone;
    }

    /**
     *
     * @return
     */
    public String getContractorPrimaryContact() {
        return contractorPrimaryContact;
    }

    /**
     *
     * @return
     */
    public String getContractorState() {
        return contractorState;
    }

    /**
     *
     * @return
     */
    public String getContractorStatus() {
        return contractorStatus;
    }

    /**
     *
     * @return
     */
    public String getContractorUrl() {
        return contractorUrl;
    }

    /**
     *
     * @return
     */
    public String getContractorZip() {
        return contractorZip;
    }

    /**
     *
     * @return
     */
    public String getCounty() {
        return county;
    }

    /**
     *
     * @return
     */
    public String getCtype() {
        return ctype;
    }

    /**
     *
     * @return
     */
    public String getJobAddress() {
        return jobAddress;
    }

    /**
     *
     * @return
     */
    public String getJobCity() {
        return jobCity;
    }

    /**
     *
     * @return
     */
    public String getJobLotNumber() {
        return jobLotNumber;
    }

    /**
     *
     * @return
     */
    public String getJobSqFt() {
        return jobSqFt;
    }

    /**
     *
     * @return
     */
    public String getJobState() {
        return jobState;
    }

    /**
     *
     * @return
     */
    public String getJobSubDivision() {
        return jobSubDivision;
    }

    /**
     *
     * @return
     */
    public String getJobValue() {
        return jobValue;
    }

    /**
     *
     * @return
     */
    public String getJobZip() {
        return jobZip;
    }

    /**
     *
     * @return
     */
    public String getLegal() {
        return legal;
    }

    /**
     *
     * @return
     */
    public String getOwner() {
        return owner;
    }

    /**
     *
     * @return
     */
    public String getOwnerAddress() {
        return ownerAddress;
    }

    /**
     *
     * @return
     */
    public String getOwnerCity() {
        return ownerCity;
    }

    /**
     *
     * @return
     */
    public String getOwnerEmail() {
        return ownerEmail;
    }

    /**
     * 
     * @return
     */
    public String getOwnerFax() {
        return ownerFax;
    }

    /**
     *
     * @return
     */
    public String getOwnerPhone() {
        return ownerPhone;
    }

    /**
     *
     * @return
     */
    public String getOwnerPrimaryContact() {
        return ownerPrimaryContact;
    }

    /**
     *
     * @return
     */
    public String getOwnerState() {
        return ownerState;
    }

    /**
     *
     * @return
     */
    public String getOwnerType() {
        return ownerType;
    }

    /**
     *
     * @return
     */
    public String getOwnerUrl() {
        return ownerUrl;
    }

    /**
     *
     * @return
     */
    public String getOwnerZip() {
        return ownerZip;
    }

    /**
     *
     * @return
     */
    public String getPermitDate() {
        return permitDate;
    }

    /**
     *
     * @return
     */
    public String getPermitDescription() {
        return permitDescription;
    }

    /**
     *
     * @return
     */
    public String getPermitNumber() {
        return permitNumber;
    }

    /**
     *
     * @return
     */
    public String getPermitType() {
        return permitType;
    }

    /**
     *
     * @return
     */
    public String getState() {
        return state;
    }

    /**
     *
     * @return
     */
    public String getUnits() {
        return units;
    }

    /**
     *
     * @return
     */
    public String getUploadDate() {
        return uploadDate;
    }

    /**
     *
     * @param bldCode
     */
    public void setBldCode(String bldCode) {
        this.bldCode = bldCode;
    }

    /**
     *
     * @param buildings
     */
    public void setBuildings(String buildings) {
        this.buildings = buildings;
    }

    /**
     *
     * @param conotractorEmail
     */
    public void setConotractorEmail(String conotractorEmail) {
        this.conotractorEmail = conotractorEmail;
    }

    /**
     *
     * @param contractor
     */
    public void setContractor(String contractor) {
        this.contractor = contractor;
    }

    /**
     *
     * @param contractorAddress
     */
    public void setContractorAddress(String contractorAddress) {
        this.contractorAddress = contractorAddress;
    }

    /**
     *
     * @param contractorCity
     */
    public void setContractorCity(String contractorCity) {
        this.contractorCity = contractorCity;
    }

    /**
     *
     * @param contractorFax
     */
    public void setContractorFax(String contractorFax) {
        this.contractorFax = contractorFax;
    }

    /**
     *
     * @param contractorLastActivity
     */
    public void setContractorLastActivity(String contractorLastActivity) {
        this.contractorLastActivity = contractorLastActivity;
    }

    /**
     *
     * @param contractorPhone
     */
    public void setContractorPhone(String contractorPhone) {
        this.contractorPhone = contractorPhone;
    }

    /**
     *
     * @param contractorPrimaryContact
     */
    public void setContractorPrimaryContact(String contractorPrimaryContact) {
        this.contractorPrimaryContact = contractorPrimaryContact;
    }

    /**
     *
     * @param contractorState
     */
    public void setContractorState(String contractorState) {
        this.contractorState = contractorState;
    }

    /**
     *
     * @param contractorStatus
     */
    public void setContractorStatus(String contractorStatus) {
        this.contractorStatus = contractorStatus;
    }

    /**
     *
     * @param contractorUrl
     */
    public void setContractorUrl(String contractorUrl) {
        this.contractorUrl = contractorUrl;
    }

    /**
     *
     * @param contractorZip
     */
    public void setContractorZip(String contractorZip) {
        this.contractorZip = contractorZip;
    }

    /**
     *
     * @param county
     */
    public void setCounty(String county) {
        this.county = county;
    }

    /**
     *
     * @param ctype
     */
    public void setCtype(String ctype) {
        this.ctype = ctype;
    }

    /**
     *
     * @param jobAddress
     */
    public void setJobAddress(String jobAddress) {
        this.jobAddress = jobAddress;
    }

    /**
     *
     * @param jobCity
     */
    public void setJobCity(String jobCity) {
        this.jobCity = jobCity;
    }

    /**
     *
     * @param jobLotNumber
     */
    public void setJobLotNumber(String jobLotNumber) {
        this.jobLotNumber = jobLotNumber;
    }

    /**
     *
     * @param jobSqFt
     */
    public void setJobSqFt(String jobSqFt) {
        this.jobSqFt = jobSqFt;
    }

    /**
     *
     * @param jobState
     */
    public void setJobState(String jobState) {
        this.jobState = jobState;
    }

    /**
     *
     * @param jobSubDivision
     */
    public void setJobSubDivision(String jobSubDivision) {
        this.jobSubDivision = jobSubDivision;
    }

    /**
     *
     * @param jobValue
     */
    public void setJobValue(String jobValue) {
        this.jobValue = jobValue;
    }

    /**
     *
     * @param jobZip
     */
    public void setJobZip(String jobZip) {
        this.jobZip = jobZip;
    }

    /**
     *
     * @param legal
     */
    public void setLegal(String legal) {
        this.legal = legal;
    }

    /**
     *
     * @param owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     *
     * @param ownerAddress
     */
    public void setOwnerAddress(String ownerAddress) {
        this.ownerAddress = ownerAddress;
    }

    /**
     *
     * @param ownerCity
     */
    public void setOwnerCity(String ownerCity) {
        this.ownerCity = ownerCity;
    }

    /**
     *
     * @param ownerEmail
     */
    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    /**
     *
     * @param ownerFax
     */
    public void setOwnerFax(String ownerFax) {
        this.ownerFax = ownerFax;
    }

    /**
     *
     * @param ownerPhone
     */
    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    /**
     *
     * @param ownerPrimaryContact
     */
    public void setOwnerPrimaryContact(String ownerPrimaryContact) {
        this.ownerPrimaryContact = ownerPrimaryContact;
    }

    /**
     *
     * @param ownerState
     */
    public void setOwnerState(String ownerState) {
        this.ownerState = ownerState;
    }

    /**
     *
     * @param ownerType
     */
    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
    }

    /**
     *
     * @param ownerUrl
     */
    public void setOwnerUrl(String ownerUrl) {
        this.ownerUrl = ownerUrl;
    }

    /**
     *
     * @param ownerZip
     */
    public void setOwnerZip(String ownerZip) {
        this.ownerZip = ownerZip;
    }

    /**
     *
     * @param permitDate
     */
    public void setPermitDate(String permitDate) {
        this.permitDate = permitDate;
    }

    /**
     *
     * @param permitDescription
     */
    public void setPermitDescription(String permitDescription) {
        this.permitDescription = permitDescription;
    }

    /**
     *
     * @param permitNumber
     */
    public void setPermitNumber(String permitNumber) {
        this.permitNumber = permitNumber;
    }

    /**
     *
     * @param permitType
     */
    public void setPermitType(String permitType) {
        this.permitType = permitType;
    }

    /**
     *
     * @param state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     *
     * @param units
     */
    public void setUnits(String units) {
        this.units = units;
    }

    /**
     *
     * @param uploadDate
     */
    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }
}
