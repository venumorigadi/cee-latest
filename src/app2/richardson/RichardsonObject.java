package app2.richardson;

import app2.CountyObject;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RichardsonObject {

    private String applicationNumber;
    private String type;
    private String zoning;
    private String location;
    private String constructionArea;
    private String valuation;
    private String altAddress;
    private String suite;
    private String owner;
    private String property;
    private String general;
    private String ownerInfo;
    private String propertyInfo;
    private String contractorInfo;
    private String contractorNumber;
    private String permit;
    private String issuedDate;
    private String status;
    private String structure;
    private String seq;
    private String workDescription;
    private String subContractor;
    private String description;
    private String contractor;

    public RichardsonObject() {
    }

    /**
     * There are many possible combination of writing a lot number.
     * Sometimes it may be mentioned twice and sometimes previous lot
     * number may be added as comment. This method will find the lot
     * number from the given info.
     * @param text
     * @return
     */
    private String findLotNumber(String text) {
        String group = "";

        int lt = text.indexOf("LT ");
        int lot = text.indexOf("LOT ");
        int lts = text.indexOf("LTS ");
        int lots = text.indexOf("LOTS ");

        if ((lt > -1 || lot > -1) && (lts + lots > -2 ? (lt + lot < lts + lots) : true)) {
            Pattern p = Pattern.compile("((LOT|LT)\\s+([A-Z|a-z|0-9]*))");
            Matcher m = p.matcher(text);
            while (m.find()) {
                group = m.group(3);
                break;
            }
        }
        if((lts>-1 || lots>-1) && (lt+lot>-2?(lt+lot>lts+lots):true)) {
            Pattern p = Pattern.compile("((LOTS|LTS)\\s+([A-Z|a-z|0-9]*)((\\s*[&|,|-]\\s*[A-Z|a-z|0-9]*)*))");
            Matcher m = p.matcher(text);
            while (m.find()) {
                group = m.group(3) + m.group(4);
                break;
            }
            if (group.indexOf("&") > -1) {
                group = group.replaceAll("&", ",");
            }
        }
        return group;
    }

    public String getAltAddress() {
        return altAddress;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public String getConstructionArea() {
        return constructionArea;
    }

    public String getContractorInfo() {
        return contractorInfo;
    }

    public String getContractorNumber() {
        return contractorNumber;
    }

    public String getDescription() {
        return description;
    }

    public String getContractor() {
        return contractor;
    }

    public String getGeneral() {
        return general;
    }

    public String getIssuedDate() {
        return issuedDate;
    }

    public String getLocation() {
        return location;
    }

    public String getOwner() {
        return owner;
    }

    public String getOwnerInfo() {
        return ownerInfo;
    }

    public String getPermit() {
        return permit;
    }

    public String getProperty() {
        return property;
    }

    public String getPropertyInfo() {
        return propertyInfo;
    }

    public String getSeq() {
        return seq;
    }

    public String getStatus() {
        return status;
    }

    public String getStructure() {
        return structure;
    }

    public String getSubContractor() {
        return subContractor;
    }

    public String getSuite() {
        return suite;
    }

    public String getType() {
        return type;
    }

    public String getValuation() {
        return valuation;
    }

    public String getWorkDescription() {
        return workDescription;
    }

    public String getZoning() {
        return zoning;
    }

    public void setAltAddress(String altAddress) {
        this.altAddress = altAddress;
    }

    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public void setConstructionArea(String constructionArea) {
        this.constructionArea = constructionArea;
    }

    public void setContractorInfo(String contractorInfo) {
        this.contractorInfo = contractorInfo;
    }

    public void setContractorNumber(String contractorNumber) {
        this.contractorNumber = contractorNumber;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setGeneral(String general) {
        this.general = general;
    }

    public void setIssuedDate(String issuedDate) {
        this.issuedDate = issuedDate;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setOwnerInfo(String ownerInfo) {
        this.ownerInfo = ownerInfo;
    }

    public void setPermit(String permit) {
        this.permit = permit;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public void setPropertyInfo(String propertyInfo) {
        this.propertyInfo = propertyInfo;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public void setSubContractor(String subContractor) {
        this.subContractor = subContractor;
    }

    public void setSuite(String suite) {
        this.suite = suite;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValuation(String valuation) {
        this.valuation = valuation;
    }

    public void setWorkDescription(String workDescription) {
        this.workDescription = workDescription;
    }

    public void setZoning(String zoning) {
        this.zoning = zoning;
    }

    public void setContractor(String contractor) {
        this.contractor = contractor;
    }

    public boolean isRow() {
        try {
            Double d = Double.parseDouble(getValuation().replaceAll(",", ""));
            if (d > 30000) {
                return true;
            }
        } catch (Exception exception) {
        }
        return false;
    }

    private String findOwnerZip(String fulltext) {
        try {
            String found = fulltext;
            Pattern pattern = Pattern.compile("([A-Z|a-z]{2,4}[.])(\\s)(\\d{5,10})");
            Matcher matcher = pattern.matcher(fulltext);
            while (matcher.find()) {
                found = matcher.group(3);
                break;
            }
            if(Pattern.matches("\\d{9}", found)) {
                char[] foundc = found.toCharArray();
                found = foundc[0]+""+foundc[1]+""+foundc[2]+""+foundc[3]+""+foundc[4]+"-";
                found += foundc[5]+""+foundc[6]+""+foundc[7]+""+foundc[8]+"";
            }
            return found;
        } catch (Exception exception) {
            return fulltext;
        }
    }

    private String findOwnerState(String fulltext) {
        String found = fulltext;
        try {
            Pattern pattern = Pattern.compile("([A-Z]{2})([.]\\s)");
            Matcher matcher = pattern.matcher(fulltext);
            while (matcher.find()) {
                found = matcher.group(1);
                break;
            }
            return found;
        } catch (Exception exception) {
            System.out.println("findOwnerState - " + fulltext);
            return fulltext;
        }
    }

    private String findOwnerCity(String fulltext) {
        try {
            String found = fulltext;
            Pattern pattern = Pattern.compile("([:])(.*)([,]\\s)");
            Matcher matcher = pattern.matcher(fulltext);
            while (matcher.find()) {
                found = matcher.group(2);
                break;
            }
            return found.replace(":", "").trim();
        } catch (Exception exception) {
            System.out.println("findOwnerCity() - " + fulltext);
            return fulltext;
        }
    }

    private String findContractorCity(String fulltext) {
        try {
            String found = fulltext;
            found = found.substring(0, found.indexOf(","));
            return found.trim();
        } catch (Exception exception) {
            return fulltext;
        }
    }

    private String findContractorState(String fulltext) {
        try {
            String found = fulltext;
            found = found.substring(found.indexOf(",") + 1, found.indexOf("."));
            return found.trim();
        } catch (Exception exception) {
            return fulltext;
        }
    }

    private String findContractorZip(String fulltext) {
        try {
            String found = fulltext;
            Pattern pattern = Pattern.compile("(\\d{5})");
            Matcher matcher = pattern.matcher(fulltext);
            while (matcher.find()) {
                found = matcher.group(1);
                break;
            }
            return found.replace(":", "").trim();
        } catch (Exception exception) {
            return fulltext;
        }
    }

    private String findContractorContact(String fulltext) {
        try {
            String found = fulltext;
            Pattern pattern = Pattern.compile("(\\d{3}\\s\\d{3}[-]\\d{4})");
            Matcher matcher = pattern.matcher(fulltext);
            while (matcher.find()) {
                found = matcher.group(1);
                break;
            }
            
            found = found.replace(":", "").trim();

            if(found.indexOf(" ")>-1) {
                String[] numberparts = found.split(" ");
                found = "(" + numberparts[0] + ") "+ numberparts[1];
            }
            return found;
        } catch (Exception exception) {
            exception.printStackTrace(System.err);
            return fulltext;
        }
    }

    public CountyObject toCountyObject() {
        CountyObject object = new CountyObject();
        object.setUploadDate(new Date().toString());
        object.setState("TX");
        object.setCounty("Dallas");
        object.setPermitNumber(applicationNumber);
        object.setPermitDescription(workDescription);
        object.setPermitType(type);
        object.setPermitDate(issuedDate);
        object.setJobValue(valuation);
        object.setJobSqFt(constructionArea);
        object.setJobAddress(location);
        object.setJobCity("Richardson");
        object.setJobState("TX");
        object.setJobSubDivision(property.trim() + " " + propertyInfo.trim());
        object.setOwner(owner.trim() + " " + ownerInfo.replace(":", "").trim());
        object.setOwnerAddress(ownerInfo.replace(":", ""));
        object.setContractor(getGeneral());
        object.setContractorAddress(getContractor().trim()+" "+getContractorInfo());
        object.setContractorCity(findContractorCity(contractorInfo));
        object.setContractorState(findContractorState(contractorInfo));
        object.setContractorZip(findContractorZip(contractorInfo));
        object.setContractorPhone(findContractorContact(contractorInfo));
        object.setOwnerState(findOwnerState(ownerInfo));
        object.setOwnerZip(findOwnerZip(ownerInfo));
        object.setOwnerCity(findOwnerCity(ownerInfo));
        object.setJobLotNumber(findLotNumber(propertyInfo));
        return object;
    }
}
