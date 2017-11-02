package is.hi.repository;

import is.hi.model.Campinfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface campinfoRepository extends JpaRepository<Campinfo, Long>{

    /**
     * Get all campinfo
     */
    @Query(value = "SELECT n FROM Campinfo n")
    List<Campinfo> getAll();


    @Transactional
    @Modifying
    @Query(value = "insert into campsitebigdata (campname, campaddress, campzip, campemail, campphone, campwebsite, campseason, maincategory, category, region, description, xval, yval) VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, ?10, ?11, ?12, ?13)"
            , nativeQuery = true)
    void addCamp(String name, String address, String zip, String email, String phone, String website, String season, String mainCategory, String campCategory, String campRegion, String descript, int xVal, int yVal);

    @Transactional
    @Modifying
    @Query(value = "delete from campsitebigdata where campname=?1"
            , nativeQuery = true)
    void deleteCamp(String name);

}