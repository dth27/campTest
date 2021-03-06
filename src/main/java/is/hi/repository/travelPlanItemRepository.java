package is.hi.repository;

import is.hi.model.TravelPlanItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.transaction.Transactional;
import java.sql.Date;
import java.util.List;

/**
 * @author Diljá, Ólöf, Sandra og Kristín
 * @date september 2017
 * HBV501G Hugbúnaðarverkefni 1
 * Háskóli Íslands
 *
 * Repository for travelplanitems
 */

public interface travelPlanItemRepository extends JpaRepository<TravelPlanItem, Long>{
    /**
     * gets all travelplanitems
     * @return a list of travelplanitems
     */
    @Query(value ="SELECT a FROM TravelPlanItem a")
    List<TravelPlanItem> getAll();

    /**
     * Gets one travelplan
     * @param name - name of travelplan
     * @param user - username
     * @return
     */
    @Query(value="SELECT a FROM travelplanitem a where a.travelplanname = ?1 AND a.username=?2", nativeQuery = true)
    List<TravelPlanItem> getUserItems(String name, String user);

    List<TravelPlanItem> findByTravelplannameAndUsername(String travelplanname, String username);
    List<TravelPlanItem> findByUsername(String user);
    /**
     * Deletes travelplanitem
     * @param campname - name of camp
     * @param planname - plan name
     */
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM travelplanitem where campname = ?1 AND travelplanname = ?2", nativeQuery = true)
    void deletItem(String campname, String planname);

    /**
     * Deletes travelplan item
     * @param planname - travelplan name
     * @param user - usernam
     */
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM travelplanitem where travelplanname = ?1 AND username = ?2", nativeQuery = true)
    void deleteTravel(String planname, String user);

    /**
     * inserts into travelplanitem an item
     * @param planname - travelplan name
     * @param camp - camp name
     * @param datearr - date arrive
     * @param datedep - date depart
     * @param price - price
     * @param user - username
     */
    @Transactional
    @Modifying
    @Query(value="insert into travelplanitem(travelplanname, campname, datearrive, datedepart, totalprice, username) VALUES (?1, ?2, ?3, ?4, ?5, ?6)", nativeQuery = true)
    void addItem(String planname, String camp, java.util.Date datearr, java.util.Date datedep, int price, String user);
    /**
     * Adss travelplanitems
     * @param travelplanItem
     */
    //void add(TravelPlanItem travelplanItem);


}
