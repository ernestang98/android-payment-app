package tech.beepbeep.beept05;

public class ChargerObject {
    private String chargerName;
    private String chargerPower;
    private String chargerLocation;
    private String chargerPrice;
    private String chargerDescription;
    public ChargerObject(
            String chargerName,
            String chargerPower,
            String chargerLocation,
            String chargerPrice,
            String chargerDescription
    ) {
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
}
