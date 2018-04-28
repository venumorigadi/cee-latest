package app2.harris;

import app2.CountyObject;
import java.util.Date;
import java.util.StringTokenizer;

public class HarrisObject {

    private String permit = "";
    private String date = "";
    private String street = "";
    private String empty1 = "";
    private String name = "";
    private String code = "";
    private String applicant = "";
    private String sqft = "";
    private String empty2 = "";
    private String cost = "";
    private String empty3 = "";
    private String empty4 = "";
    private String empty5 = "";
    private String empty6 = "";
    private String sec = "";
    private String block = "";
    private String lot = "";
    private String res = "";
    private String a_no = "";
    private String abs_name = "";
    private String zip = "";
    private String census = "";
    private String code2 = "";
    private String insp = "";
    private String fc = "";
    private String subDivision = "";

    public HarrisObject() {
        
    }

    public void setObject(int index, String str) {
        switch (index) {
            case 1:
                setPermit(str);
                break;
            case 2:
                setDate(str);
                break;
            case 3:
                setStreet(str);
                break;
            case 4:
                empty1 = str;
                break;
            case 5:
                setName(str);
                break;
            case 6:
                setCode(str);
                break;
            case 7:
                setApplicant(str);
                break;
            case 8:
                setSqft(str);
                break;
            case 9:
                empty2 = str;
                break;
            case 10:
                setCost(str);
                break;
            case 11:
                empty3 = str;
                break;
            case 12:
                empty4 = str;
                break;
            case 13:
                empty5 = str;
                break;
            case 14:
                subDivision = str;
                break;
            case 15:
                setSec(str);
                break;
            case 16:
                setBlock(str);
                break;
            case 17:
                setLot(str);
                break;
            case 18:
                setRes(str);
                break;
            case 19:
                setA_no(str);
                break;
            case 20:
                setZip(str);
                break;
            case 21:
                setZip(str);
                break;
            case 22:
                setCensus(str);
                break;
            case 23:
                setCode2(str);
                break;
            case 24:
                setInsp(str);
                break;
            case 25:
                setInsp(str);
                break;
            default:
                break;
        }
    }

    public String getA_no() {
        return a_no;
    }

    public String getAbs_name() {
        return abs_name;
    }

    public String getApplicant() {
        return applicant;
    }

    public String getBlock() {
        return block;
    }

    public String getCensus() {
        return census;
    }

    public String getCode() {
        return code;
    }

    public String getCode2() {
        return code2;
    }

    public String getCost() {
        return cost;
    }

    public String getDate() {
        return date;
    }

    public String getFc() {
        return fc;
    }

    public String getInsp() {
        return insp;
    }

    public String getLot() {
        return lot;
    }

    public String getName() {
        return name;
    }

    public String getPermit() {
        return permit;
    }

    public String getRes() {
        return res;
    }

    public String getSec() {
        return sec;
    }

    public String getSqft() {
        return sqft;
    }

    public String getStreet() {
        return street;
    }

    public String getZip() {
        return zip;
    }

    public void setA_no(String a_no) {
        this.a_no = a_no;
    }

    public void setAbs_name(String abs_name) {
        this.abs_name = abs_name;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public void setCensus(String census) {
        this.census = census;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setCode2(String code2) {
        this.code2 = code2;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setFc(String fc) {
        this.fc = fc;
    }

    public void setInsp(String insp) {
        this.insp = insp;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPermit(String permit) {
        char[] c = permit.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (!Character.isDigit(c[i])) {
                c[i] = '-';
            }
        }
        this.permit = new String(c);
    }

    public void setRes(String res) {
        this.res = res;
    }

    public void setSec(String sec) {
        this.sec = sec;
    }

    public void setSqft(String sqft) {
        this.sqft = sqft;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getEmpty1() {
        return empty1;
    }

    public String getEmpty2() {
        return empty2;
    }

    public String getEmpty3() {
        return empty3;
    }

    public String getEmpty4() {
        return empty4;
    }

    public String getEmpty5() {
        return empty5;
    }

    public String getEmpty6() {
        return empty6;
    }

    public String getSubDivision() {
        return subDivision;
    }

    public void setEmpty1(String empty1) {
        this.empty1 = empty1;
    }

    public void setEmpty2(String empty2) {
        this.empty2 = empty2;
    }

    public void setEmpty3(String empty3) {
        this.empty3 = empty3;
    }

    public void setEmpty4(String empty4) {
        this.empty4 = empty4;
    }

    public void setEmpty5(String empty5) {
        this.empty5 = empty5;
    }

    public void setEmpty6(String empty6) {
        this.empty6 = empty6;
    }

    public void setSubDivision(String subDivision) {
        this.subDivision = subDivision;
    }

    public CountyObject toCountyObject() {
        CountyObject object = new CountyObject();
        object.setUploadDate(new Date().toString());
        object.setState("TX");
        object.setCounty("Harris");
        object.setPermitNumber(permit);
        object.setPermitDate(date);
        object.setJobValue(cost);
        object.setJobSqFt(sqft);
        String jobAddress=getStreet() + " " + getEmpty1() + " " + getName();
        StringTokenizer st = new StringTokenizer(jobAddress, " ");
        StringBuilder jbadbff = new StringBuilder();
        while(st.hasMoreElements()) {
            jbadbff.append(st.nextElement()).append(" ");
        }
        object.setJobAddress(jbadbff.toString().trim());
        object.setJobState("TX");
       /* try {
            object.setJobZip(String.valueOf(Integer.parseInt(getAbs_name())));
        } catch(Exception exception) {
            object.setJobZip(getZip());
        }*/
        object.setJobZip(getZip());
        object.setJobSubDivision(getSubDivision());
        object.setJobLotNumber(getLot());
        object.setOwner(getAbs_name());
        object.setContractorAddress(getApplicant());
        object.setJobCity("Houston");
        return object;
    }

    public boolean isRow() {
        try {
            Double c = Double.parseDouble(getCost());
            if (c > 30000) {
                return true;
            }
            return false;
        } catch (Exception exception) {
            return false;
        }
    }
}
