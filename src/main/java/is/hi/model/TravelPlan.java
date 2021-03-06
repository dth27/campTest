package is.hi.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @author Diljá, Ólöf, Sandra og Kristín
 * @date september 2017
 * HBV501G Hugbúnaðarverkefni 1
 * Háskóli Íslands
 *
 * TravelPlan class that holds information about travelplan
 */
@Entity
@Table (name = "travelplan")
public class TravelPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Size(min = 3, max = 15)
    String travelplanname;
    String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public TravelPlan(String travelplanname, String username){
        this.travelplanname = travelplanname;
        this.username = username;
    }

    public TravelPlan() {
    }

    public String getTravelplanname() {
        return travelplanname;
    }

    public void setTravelplanname(String travelPlanName) {
        this.travelplanname = travelPlanName;
    }

}
