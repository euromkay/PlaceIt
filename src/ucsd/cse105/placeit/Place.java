package ucsd.cse105.placeit;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

public class Place {
    private String id;
    private String icon;
    private String name;
    private String vicinity;
    private Double latitude;
    private Double longitude;
    public ArrayList<String> types = new ArrayList<String>();

    //gets id of place
    public String getId() {
        return id;
    }
    
    //sets id of place
    public void setId(String id) {
        this.id = id;
    }
    
    //gets icon of place
    public String getIcon() {
        return icon;
    }
    //sets icon of place
    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    //gets latitude of place
    public Double getLatitude() {
        return latitude;
    }
    //sets latitude of place
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    //gets longittude of place
    public Double getLongitude() {
        return longitude;
    }
    //sets longitutde of place
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
    //gets name of place
    public String getName() {
        return name;
    }
    //sets name of place
    public void setName(String name) {
        this.name = name;
    }
    
    //gets vicitinty of place
    public String getVicinity() {
        return vicinity;
    }
    //sets vicinity of palce
    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    //some method that does something
    static Place jsonToPontoReferencia(JSONObject pontoReferencia) {
        try {
            Place result = new Place();
            JSONObject geometry = (JSONObject) pontoReferencia.get("geometry");
            JSONObject location = (JSONObject) geometry.get("location");
            result.setLatitude((Double) location.get("lat"));
            result.setLongitude((Double) location.get("lng"));
            result.setIcon(pontoReferencia.getString("icon"));
            result.setName(pontoReferencia.getString("name"));
            result.setVicinity(pontoReferencia.getString("vicinity"));
            //result.setId(pontoReferencia.getString("id"));
            //result.setAddress(pontoReferencia.getString("formatted_address"));
            return result;
        } catch (JSONException ex) {
            Logger.getLogger(Place.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    //get string representation of object
    @Override
    public String toString() {
        return "Place{" + "id=" + id + ", icon=" + icon + ", name=" + name + ", latitude=" + latitude + ", longitude=" + longitude + '}';
    }

}