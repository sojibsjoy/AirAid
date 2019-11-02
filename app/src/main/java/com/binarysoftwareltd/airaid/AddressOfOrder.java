package com.binarysoftwareltd.airaid;

public class AddressOfOrder {

    private String nameOfPerson, phoneNumber, areaName, houseNo, wardNo, roadNo, colony, othersOfAddress;

    public AddressOfOrder(String nameOfPerson, String phoneNumber, String areaName, String houseNo, String wardNo, String roadNo, String colony, String othersOfAddress) {
        this.nameOfPerson = nameOfPerson;
        this.phoneNumber = phoneNumber;
        this.areaName = areaName;
        this.houseNo = houseNo;
        this.wardNo = wardNo;
        this.roadNo = roadNo;
        this.colony = colony;
        this.othersOfAddress = othersOfAddress;
    }

    public String getNameOfPerson() {
        return nameOfPerson;
    }

    public void setNameOfPerson(String nameOfPerson) {
        this.nameOfPerson = nameOfPerson;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    public String getWardNo() {
        return wardNo;
    }

    public void setWardNo(String wardNo) {
        this.wardNo = wardNo;
    }

    public String getRoadNo() {
        return roadNo;
    }

    public void setRoadNo(String roadNo) {
        this.roadNo = roadNo;
    }

    public String getColony() {
        return colony;
    }

    public void setColony(String colony) {
        this.colony = colony;
    }

    public String getOthersOfAddress() {
        return othersOfAddress;
    }

    public void setOthersOfAddress(String othersOfAddress) {
        this.othersOfAddress = othersOfAddress;
    }
}
