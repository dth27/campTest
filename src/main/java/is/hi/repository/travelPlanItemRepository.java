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
//TODO ætti þetta að vera Long??

public interface travelPlanItemRepository extends JpaRepository<TravelPlanItem, Long>{
    /**
     * gets all travelplanitems
     * @return a list of travelplanitems
     */
    @Query(value ="SELECT a FROM TravelPlanItem a")
    List<TravelPlanItem> getAll();

    @Transactional
    @Modifying
    @Query(value = "DELET FROM travelplanitem where campname = ?1 AND travelplanname = ?2", nativeQuery = true)
    void deletItem(String campname, String planname);

    /**
     * @param planname
     * @param camp
     * @param datearr
     * @param datedep
     * @param price
     * @param user
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
