package app2.richardson;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RichardsonParser {

    private String row;

    public RichardsonParser(String row) {
        this.row = row;
    }

    public String getApplicationNumber() {
        Pattern p = Pattern.compile("(APPLICATION[\\s]*#:\\s)(([\\d|-])*)");
        Matcher m = p.matcher(row);
        String applicationNumber = "";
        while (m.find()) {
            applicationNumber = m.group(2);
        }
        return applicationNumber;
    }

    public String getType() {
        Pattern p = Pattern.compile("(\\sTYPE:\\s)(.*)(\\s{4,})");
        Matcher m = p.matcher(row);
        String type = "";
        while (m.find()) {
            type = m.group(2);
        }
        return type;
    }

    public String getZoning() {
        Pattern p = Pattern.compile("(\\sZONING:\\s)(.*)(\\r||\\n)");
        Matcher m = p.matcher(row);
        String zoning = "";
        while (m.find()) {
            zoning = m.group(2);
        }
        return zoning;
    }

    public String getLocation() {
        Pattern p = Pattern.compile("(LOCATION:\\s)(.*)(CONSTRUCTION AREA:)");
        Matcher m = p.matcher(row);
        String location = "";
        while (m.find()) {
            location = m.group(2);
        }
        return location;
    }

    public String getConstructionArea() {
        Pattern p = Pattern.compile("(CONSTRUCTION AREA:\\s)(.*)(VALUATION:)");
        Matcher m = p.matcher(row);
        String constructionArea = "";
        while (m.find()) {
            constructionArea = m.group(2);
        }
        return constructionArea.trim();
    }

    public String getValuation() {
        Pattern p = Pattern.compile("(VALUATION:\\s)(.*)(\\r|\\n)");
        Matcher m = p.matcher(row);
        String valuation = "";
        while (m.find()) {
            valuation = m.group(2);
        }
        return valuation.trim();
    }

    public String getAltAddress() {
        Pattern p = Pattern.compile("(ALT. ADDRESS/TENANT:\\s)(.*)(SUITE:)");
        Matcher m = p.matcher(row);
        String location = "";
        while (m.find()) {
            location = m.group(2);
        }
        return location;
    }

    public String getSuite() {
        Pattern p = Pattern.compile("(SUITE:\\s)(.*)(\\r|\\n:)");
        Matcher m = p.matcher(row);
        String suite = "";
        while (m.find()) {
            suite = m.group(2);
        }
        return suite;
    }

    public String getOwner() {
        Pattern p = Pattern.compile("(OWNER:\\s)(.*)(PROPERTY:)");
        Matcher m = p.matcher(row);
        String owner = "";
        while (m.find()) {
            owner = m.group(2);
        }
        return owner;
    }

    public String getProperty() {
        Pattern p = Pattern.compile("(PROPERTY:\\s)(.*)(GENERAL:)");
        Matcher m = p.matcher(row);
        String property = "";
        while (m.find()) {
            property = m.group(2);
        }
        return property;
    }

    public String getGeneral() {
        Pattern p = Pattern.compile("(GENERAL:\\s)(.*)(\\r|\\n)");
        Matcher m = p.matcher(row);
        String general = "";
        while (m.find()) {
            general = m.group(2);
        }
        return general;
    }

    public String getPermit() {
        Pattern p = Pattern.compile("(PERMIT:\\s)(.*)(ISSUED:\\s)");
        Matcher m = p.matcher(row);
        String permit = "";
        while (m.find()) {
            permit = m.group(2);
        }
        return permit;
    }

    public String getIssued() {
        Pattern p = Pattern.compile("(ISSUED:\\s)(.*)(STATUS:\\s)");
        Matcher m = p.matcher(row);
        String issuedDate = "";
        while (m.find()) {
            issuedDate = m.group(2);
        }
        return issuedDate;
    }

    public String getStatus() {
        Pattern p = Pattern.compile("(STATUS:\\s)(.*)(STRUCTURE #:)");
        Matcher m = p.matcher(row);
        String status = "";
        while (m.find()) {
            status = m.group(2);
        }
        return status;
    }

    public String getStructureNumber() {
        Pattern p = Pattern.compile("(STRUCTURE #:\\s)(.*)(SEQ #:)");
        Matcher m = p.matcher(row);
        String StructureNumber = "";
        while (m.find()) {
            StructureNumber = m.group(2);
        }
        return StructureNumber;
    }

    public String getSeqNumber() {
        Pattern p = Pattern.compile("(SEQ #:\\s)(.*)(\\r|\\n)");
        Matcher m = p.matcher(row);
        String seq = "";
        while (m.find()) {
            seq = m.group(2);
        }
        return seq;
    }

    public String getSubContractor() {
        Pattern p = Pattern.compile("(SUB-CONTR:\\s)(.*)(WORK DESC:)");
        Matcher m = p.matcher(row);
        String subContractor = "";
        while (m.find()) {
            subContractor = m.group(2);
        }
        return subContractor;
    }

    public String getWorkDescription() {
        Pattern p = Pattern.compile("( WORK DESC:\\s)(.*)(DESC:)");
        Matcher m = p.matcher(row);
        String workdescription = "";
        while (m.find()) {
            workdescription = m.group(2);
        }
        return workdescription;
    }

    public String getDescription() {
        Pattern p = Pattern.compile("(DESC:\\s)(.*)(\\r|\\n)");
        Matcher m = p.matcher(row);
        String location = "";
        while (m.find()) {
            location = m.group(2);
        }
        return location;
    }

    public String[] extractSubRow() {
        String[] lines = row.split("\\r\\n");

        String ownerInfo = "";
        String propertyInfo = "";
        String contractor = "";
        String contractorInfo = "";

        String line5 = lines[5];

        Pattern p = Pattern.compile("(INFO:\\s)(.*)(INFO:\\s)");
        Matcher m = p.matcher(line5);
        while (m.find()) {
            ownerInfo += m.group(2).trim();
        }
        p = Pattern.compile("(\\s{4,}INFO:\\s)(.*)(CONTRACTOR:)");
        m = p.matcher(line5);
        while (m.find()) {
            propertyInfo += m.group(2).trim();
        }
        contractor = line5.substring(line5.lastIndexOf("CONTRACTOR:") + 11, line5.length());

        for (int i = 6; i < 9; i++) {
            String[] line = lines[i].split(": ");
            try {
                if (i == 7) {
                    ownerInfo += ": " + line[1].trim();
                } else {
                    ownerInfo += " " + line[1].trim();
                }
                if (i == 6) {
                    propertyInfo += " " + (line[2].substring(0, line[2].lastIndexOf("INFO:"))).trim();
                } else {
                    propertyInfo += " " + line[2].trim();
                }
                if (i == 8) {
                    contractorInfo += " " + line[3].trim().replaceAll("(CONTRACTOR\\s#.*)", "");
                } else {
                    contractorInfo += " " + line[3].trim();
                }
            } catch (Exception exception) {
            }
        }

        return new String[]{ownerInfo, propertyInfo, contractorInfo, contractor};
        /*
        System.out.println("owner - " + ownerInfo);
        System.out.println("property - " + propertyInfo);
        System.out.println("contractor - " + contractor);
        System.out.println("contractorInfo - " + contractorInfo);
         */
    }

    public String getContractorNumber() {
        Pattern p = Pattern.compile("(CONTRACTOR #)(.*)(\\r|\\n)");
        Matcher m = p.matcher(row);
        String contractorNumber = "";
        while (m.find()) {
            contractorNumber = m.group(2);
        }
        return contractorNumber.trim();
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }
}
