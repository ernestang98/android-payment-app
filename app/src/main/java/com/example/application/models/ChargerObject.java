package com.example.application.models;

import java.io.Serializable;

public class ChargerObject implements Serializable {

    private String chargerId;
    private String chargerName;
    private String chargerPower;
    private String chargerPowerUnit;
    private String chargerLocation;
    private String chargerPrice;
    private String chargerCurrency;
    private String chargerRate;
    private String chargerDescription;
    private Boolean chargerIsAvailable;

    public ChargerObject(
            String chargerId,
            String chargerName,
            String chargerPower,
            String chargerPowerUnit,
            String chargerLocation,
            String chargerCurrency,
            String chargerPrice,
            String chargerRate,
            String chargerDescription
    ) {
        this.chargerId = chargerId;
        this.chargerName = chargerName;
        this.chargerPower = chargerPower;
        this.chargerPowerUnit = chargerPowerUnit;
        this.chargerLocation = chargerLocation;
        this.chargerCurrency = chargerCurrency;
        this.chargerPrice = chargerPrice;
        this.chargerRate = chargerRate;
        this.chargerDescription = chargerDescription;
    }

    public String getChargerName() {
        return chargerName;
    }

    public void setChargerName(String chargerName) {
        this.chargerName = chargerName;
    }

    public String getChargerPower() {
        return chargerPower;
    }

    public void setChargerPower(String chargerPower) {
        this.chargerPower = chargerPower;
    }

    public String getChargerLocation() {
        return chargerLocation;
    }

    public void setChargerLocation(String chargerLocation) {
        this.chargerLocation = chargerLocation;
    }

    public String getChargerPrice() {
        return chargerPrice;
    }

    public void setChargerPrice(String chargerPrice) {
        this.chargerPrice = chargerPrice;
    }

    public String getChargerDescription() {
        return chargerDescription;
    }

    public void setChargerDescription(String chargerDescription) {
        this.chargerDescription = chargerDescription;
    }

    public String getChargerId() {
        return chargerId;
    }

    public void setChargerId(String chargerId) {
        this.chargerId = chargerId;
    }

    public Boolean getChargerIsAvailable() {
        return chargerIsAvailable;
    }

    public void setChargerIsAvailable(Boolean chargerIsAvailable) {
        this.chargerIsAvailable = chargerIsAvailable;
    }

    public String getChargerCurrency() {
        return chargerCurrency;
    }

    public void setChargerCurrency(String chargerCurrency) {
        this.chargerCurrency = chargerCurrency;
    }

    public String getChargerRate() {
        return chargerRate;
    }

    public void setChargerRate(String chargerRate) {
        this.chargerRate = chargerRate;
    }

    public String getChargerPowerUnit() {
        return chargerPowerUnit;
    }

    public void setChargerPowerUnit(String chargerPowerUnit) {
        this.chargerPowerUnit = chargerPowerUnit;
    }


    @Override
    public String toString() {
        return "Charger Id: "
                + this.chargerId
                + "Charger Name: "
                + this.chargerName
                + ", Charger Power: "
                + this.chargerPower + " " + this.chargerPowerUnit
                + ", Charger Location: "
                + this.chargerLocation
                + ", Charger Price: "
                + this.chargerCurrency + this.chargerPrice + this.chargerRate
                + ", Charger Description: "
                + this.chargerDescription;
    }

}
