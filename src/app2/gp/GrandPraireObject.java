package app2.gp;

import app2.CountyObject;
import java.util.Date;

public class GrandPraireObject {

    private String permitKey = null;
    private String permitNumber = null;
    private String address = null;
    private String type = null;
    private String statusCode = null;
    private String piAddress = null;
    private String tenantNumber = null;
    private String piCity = null;
    private String piState = null;
    private String piZipCode = null;
    private String piLocationCode = null;
    private String piOwnerName = null;
    private String piParcelId = null;
    private String piAlternateId = null;
    private String piZoning = null;
    private String piSubDivision = null;
    private String aiApplicationStatus = null;
    private String aiStatusDate = null;
    private String aiPermitIssueDate = null;
    private String aiApplicationType = null;
    private String aiApplicationDate = null;
    private String aiValuation = null;
    private String aiSquareFootage = null;
    private String pdDescription = null;
    private String ciContractorName = null;
    private String ciMailingAddress = null;
    private String ciCityStZip = null;
    private String ciPhoneNumber = " ";

    public GrandPraireObject() {
    }

    public void setProperty(int position, String data) {
        switch (position) {
            case 0:
                permitKey = data;
                break;
            case 1:
                permitNumber = data;
                break;
            case 2:
                address = data;
                break;
            case 3:
                type = data;
                break;
            case 4:
                statusCode = data;
                break;
            case 5:
                piAddress = data;
                break;
            case 6:
                tenantNumber = data;
                break;
            case 7:
                piCity = data;
                break;
            case 8:
                piState = data;
                break;
            case 9:
                piZipCode = data;
                break;
            case 10:
                piLocationCode = data;
                break;
            case 11:
                piOwnerName = data;
                break;
            case 12:
                piParcelId = data;
                break;
            case 13:
                piAlternateId = data;
                break;
            case 14:
                piZoning = data;
                break;
            case 15:
                piSubDivision = data;
                break;
            case 16:
                aiApplicationStatus = data;
                break;
            case 17:
                aiStatusDate = data;
                break;
            case 18:
                aiPermitIssueDate = data;
                break;
            case 19:
                aiApplicationType = data;
                break;
            case 20:
                aiApplicationDate = data;
                break;
            case 21:
                aiValuation = data;
                break;
            case 22:
                aiSquareFootage = data;
                break;
            case 23:
                pdDescription = data;
                break;
            case 24:
                ciContractorName = data;
                break;
            case 25:
                ciMailingAddress = data;
                break;
            case 26:
                ciCityStZip = data;
                break;
            case 27:
                ciPhoneNumber = data;
                break;
            default:
                System.out.println(data);
        }
    }

    public String getAddress() {
        return address;
    }

    public String getAiApplicationDate() {
        return aiApplicationDate;
    }

    public String getAiApplicationStatus() {
        return aiApplicationStatus;
    }

    public String getAiApplicationType() {
        return aiApplicationType;
    }

    public String getAiPermitIssueDate() {
        return aiPermitIssueDate;
    }

    public String getAiSquareFootage() {
        return aiSquareFootage;
    }

    public String getAiStatusDate() {
        return aiStatusDate;
    }

    public String getAiValuation() {
        return aiValuation;
    }

    public String getCiCityStZip() {
        return ciCityStZip;
    }

    public String getCiContractorName() {
        return ciContractorName;
    }

    public String getCiMailingAddress() {
        return ciMailingAddress;
    }

    public String getCiPhoneNumber() {
        return ciPhoneNumber;
    }

    public String getPdDescription() {
        return pdDescription;
    }

    public String getPermitKey() {
        return permitKey;
    }

    public String getPermitNumber() {
        return permitNumber;
    }

    public String getPiAddress() {
        return piAddress;
    }

    public String getPiAlternateId() {
        return piAlternateId;
    }

    public String getPiCity() {
        return piCity;
    }

    public String getPiLocationCode() {
        return piLocationCode;
    }

    public String getPiOwnerName() {
        return piOwnerName;
    }

    public String getPiParcelId() {
        return piParcelId;
    }

    public String getPiState() {
        return piState;
    }

    public String getPiSubDivision() {
        return piSubDivision;
    }

    public String getPiZipCode() {
        return piZipCode;
    }

    public String getPiZoning() {
        return piZoning;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getTenantNumber() {
        return tenantNumber;
    }

    public String getType() {
        return type;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAiApplicationDate(String aiApplicationDate) {
        this.aiApplicationDate = aiApplicationDate;
    }

    public void setAiApplicationStatus(String aiApplicationStatus) {
        this.aiApplicationStatus = aiApplicationStatus;
    }

    public void setAiApplicationType(String aiApplicationType) {
        this.aiApplicationType = aiApplicationType;
    }

    public void setAiPermitIssueDate(String aiPermitIssueDate) {
        /* try {
        java.util.Date date = new Date(aiPermitIssueDate);
        this.aiPermitIssueDate = date.getMonth()+"/"+date.getDate()+"/"+date.getYear();
        } catch (Exception exception) {*/
        this.aiPermitIssueDate = aiPermitIssueDate;
        /*    Logger.getLogger(GrandPraireObject.class).error(exception.getMessage(), exception);
        }*/
    }

    public void setAiSquareFootage(String aiSquareFootage) {
        this.aiSquareFootage = aiSquareFootage;
    }

    public void setAiStatusDate(String aiStatusDate) {
        this.aiStatusDate = aiStatusDate;
    }

    public void setAiValuation(String aiValuation) {
        this.aiValuation = aiValuation;
    }

    public void setCiCityStZip(String ciCityStZip) {
        this.ciCityStZip = ciCityStZip;
    }

    public void setCiContractorName(String ciContractorName) {
        this.ciContractorName = ciContractorName;
    }

    public void setCiMailingAddress(String ciMailingAddress) {
        this.ciMailingAddress = ciMailingAddress;
    }

    public void setCiPhoneNumber(String ciPhoneNumber) {
        this.ciPhoneNumber = ciPhoneNumber;
    }

    public void setPdDescription(String pdDescription) {
        this.pdDescription = pdDescription;
    }

    public void setPermitKey(String permitKey) {
        this.permitKey = permitKey;
    }

    public void setPermitNumber(String permitNumber) {
        this.permitNumber = permitNumber;
    }

    public void setPiAddress(String piAddress) {
        this.piAddress = piAddress;
    }

    public void setPiAlternateId(String piAlternateId) {
        this.piAlternateId = piAlternateId;
    }

    public void setPiCity(String piCity) {
        this.piCity = piCity;
    }

    public void setPiLocationCode(String piLocationCode) {
        this.piLocationCode = piLocationCode;
    }

    public void setPiOwnerName(String piOwnerName) {
        this.piOwnerName = piOwnerName;
    }

    public void setPiParcelId(String piParcelId) {
        this.piParcelId = piParcelId;
    }

    public void setPiState(String piState) {
        this.piState = piState;
    }

    public void setPiSubDivision(String piSubDivision) {
        this.piSubDivision = piSubDivision;
    }

    public void setPiZipCode(String piZipCode) {
        this.piZipCode = piZipCode;
    }

    public void setPiZoning(String piZoning) {
        this.piZoning = piZoning;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public void setTenantNumber(String tenantNumber) {
        this.tenantNumber = tenantNumber;
    }

    public void setType(String type) {
        this.type = type;
    }

    public CountyObject toCountyObject() {
        CountyObject object = new CountyObject();
        object.setUploadDate(new Date().toString());
        object.setState("TX");
        object.setCounty("Dallas");
        object.setPermitNumber(permitNumber);
        object.setPermitDescription(aiApplicationType);
        object.setPermitType(type);
        object.setPermitDate(aiPermitIssueDate);
        object.setJobValue(aiValuation);
        object.setJobSqFt(aiSquareFootage);
        object.setJobAddress(address);
        object.setJobCity("Grand Praire");
        object.setJobState("TX");
        object.setJobZip(piZipCode);
        object.setJobSubDivision(piSubDivision);
        object.setOwner(piOwnerName);
        object.setContractor(ciContractorName);
        object.setContractorAddress(ciMailingAddress);
        object.setContractorCity(ciCityStZip);
        object.setContractorState(ciCityStZip);
        object.setContractorZip(ciCityStZip);
        object.setContractorPhone(ciPhoneNumber);
        return object;
    }
}
