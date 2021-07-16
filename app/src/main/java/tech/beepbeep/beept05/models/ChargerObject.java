package tech.beepbeep.beept05.models;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

public class ChargerObject implements Serializable {

    private String chargerId;
    private String chargerName;
    private String chargerPower;
    private String chargerLocation;
    private String chargerPrice;
    private String chargerDescription;
    private Boolean chargerIsAvailable;

    public ChargerObject(
            String chargerId,
            String chargerName,
            String chargerPower,
            String chargerLocation,
            String chargerPrice,
            String chargerDescription
    ) {
        this.chargerId = chargerId;
        this.chargerName = chargerName;
        this.chargerPower = chargerPower;
        this.chargerLocation = chargerLocation;
        this.chargerPrice = chargerPrice;
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

    @Override
    public String toString() {
        return "Charger Id: "
                + this.chargerId
                + "Charger Name: "
                + this.chargerName
                + ", Charger Power: "
                + this.chargerPower
                + ", Charger Location: "
                + this.chargerLocation
                + ", Charger Price: "
                + this.chargerPrice
                + ", Charger Description: "
                + this.chargerDescription;
    }

}
