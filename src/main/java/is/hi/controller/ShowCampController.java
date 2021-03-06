package is.hi.controller;

import is.hi.model.*;
import is.hi.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;



/**
 * @author Diljá, Ólöf, Sandra og Kristín
 * @date september 2017
 * HBV501G Hugbúnaðarverkefni 1
 * Háskóli Íslands
 *
 * Tekur við skipunum frá vefviðmóti til að notandi geti loggað sig inn
 * skoðað lista yfir tjaldsvæði og nánari upplýsingar um hvert og eitt þeirra
 * búið til nýtt travel plan og bætt travelplanitems í travel planið
 * gefið tjaldsvæði einkunn og umsögn
 * bætt við nýju tjaldsvæði, breytt því eða eytt (einungis admin)
 */
@Controller
public class ShowCampController {
    /**
     * Connections to our services
     */
    @Autowired
    campSiteService CampsiteService;
    @Autowired
    UserService userService;
    @Autowired
    TravelPlanService travelplanService;
    @Autowired
    AlternativeService alternativeService;

    /**-------------------------------
     * Global lists for some models
     * -------------------------------
     */

    /**
     * An Arraylist of Travel Plan Items
     */
    ArrayList<TravelPlanItem> tpiList;

    /**
     * An Arraylist of Travel Plans
     */
    ArrayList<TravelPlan> tpList;

    /**
     * An Arraylist of Campinfo
     */
    ArrayList<Campinfo> cList2;

    /**
     * An Arraylist of users
     */
    ArrayList<userAccess> mylist;

    /**
     * An Arraylist of a single camp review
     */
    ArrayList<Review> rList;

    /**
     * An Arraylist of camp's average ratings
     */
    ArrayList<AverageRating> ratList;

    /**
     * An Arraylist of all camp's reviews
     */
    ArrayList<Review> allRevList;



    String user;
    String campValue;
    boolean isLoggedIn = false;


    // ===========================
    // FRONTPAGE HANDLING
    // ===========================
    /** Returns frontpage
     * @return frontpage
     */

    @RequestMapping("/forsida")
    public String forsida(Model model)
    {
        int count = CampsiteService.countCampInfo();
        model.addAttribute("camps", count);
        return "/frontpage";
    }

    @RequestMapping(value = "goToFrontpage")
    public String goToFrontpage(Model model){
        int count = CampsiteService.countCampInfo();
        model.addAttribute("camps", count);
        return "/frontpage";
    }


    // ===========================
    // ACCOUNT HANDLING
    // ===========================

    /**
     * Site to create a new user
     * @param model     model Object
     * @return          page that the user can sign up for a new account
     * @return          page that the user can sign up for a new account
     */
    @RequestMapping("/newAccountSite")
    public String newAccountSite(Map<String, Object> model) {
        model.put("newUserForm", new userAccess());
        return "newAccountSite";
    }

    /**
     * Site to handle user's information
     * @param model     model Object
     * @return          page that has the information on the user's account
     */
    @RequestMapping("/accountInfo")
    public String accountInfo(Model model) {
        userAccess accountinfo = userService.getUserInfo(user);
        model.addAttribute("user", accountinfo);
        model.addAttribute("username",user);
        return "accountInfo";
    }

    /**
     * Returns a site that allows user to change password
     * @param model
     * @return
     */
    @RequestMapping("/changePassword")
    public String changePassword(Model model) {
        model.addAttribute("username",user);
        return "changePassword";
    }

    /**
     * Changes password and returns a site that tells the user that the password has been changed.
     * @param oldPw - the old password
     * @param model
     * @param newPw1 - the new password
     * @param newPw2 - the new password again
     * @return
     */
    @RequestMapping(value = "/saveNewPassword", method = RequestMethod.POST)
    public String savePassword(@RequestParam(value = "oldPw") String oldPw, Model model,
                               @RequestParam(value = "newPw1") String newPw1,
                               @RequestParam(value = "newPw2") String newPw2) {
        if (oldPw.equals(userService.getUserInfo(user).getPassword()) && newPw1.equals(newPw2))
            userService.changePassword(newPw1, user);
        if(!oldPw.equals(userService.getUserInfo(user).getPassword())){
            model.addAttribute("error","The old password is incorrect");
            model.addAttribute("username",user);
            return "changePassword";
        }
        if(!newPw1.equals(newPw2)){
            model.addAttribute("error","The new password does not match");
            model.addAttribute("username",user);
            return "changePassword";
        }
        model.addAttribute("user", userService.getUserInfo(user));
        model.addAttribute("passwordChange", "your password has been changed");
        model.addAttribute("username",user);
        return "accountInfo";
    }

    /**
     * Site to create a new login account
     * @param villur    villur (String)
     * @param model    Model Object
     * @return          page that informs that an account has been created
     */
    @RequestMapping(value = "/newAccount", method = RequestMethod.POST)
    public String newAccount(@Valid @ModelAttribute("newUserForm")
                                     userAccess newUser, BindingResult villur,
                             Model model, @RequestParam(value = "pw") String pw) {
        if (villur.hasErrors()) {
            return "newAccountSite";
        } else {
            if (userService.arePWidentical(pw, newUser.getPassword())) {
                if (userService.doesUserExist(newUser.getUsername(), newUser.getEmail())){
                    model.addAttribute("userExists", "This username or email is taken");
                    return "newAccountSite";
                }
                userService.newLoginUser(newUser);
                model.addAttribute("user", newUser);
                model.addAttribute("username",user);
                return "userAccount";
            } else {
                model.addAttribute("passwordError", "The passwords do not match");
                return "newAccountSite";
            }
        }



    }

    // ===========================
    // LOGIN HANDLING
    // ===========================
    /**
     * Site that asks user to log in and checks if user exists
     * @param name      name of the user (String)
     * @param psw       password of the user (String)
     * @param model     model Object
     * @return annaðhvort forsíðu eða notendasíðu eftir því hvort aðgangurinn er til
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String synaNotanda(@RequestParam(value = "uname") String name, @RequestParam(value = "psw") String psw, Model model ) {
        if (userService.isPwCorr(name, psw)) {
            user = name;
            cList2 = CampsiteService.getCampinfo();
            model.addAttribute("camps", cList2);
            ArrayList<TravelPlan> tpList;
            tpList = travelplanService.getUserTravelplan(user);
            model.addAttribute("travelplans", tpList);
            isLoggedIn = true;
            if(userService.hasAdminAuthority(name, psw)){
                model.addAttribute("username",user);
                return "adminLoginSite";
            }
            model.addAttribute("username",user);
            model.addAttribute("user1", user);

            return "notendasida";
        } else {
            model.addAttribute("error", "Username or password is incorrect");
            int count = CampsiteService.countCampInfo();
            model.addAttribute("camps", count);
            return "frontpage";
        }
    }

    /**
     * Logs out user and returns frontpage
     * @param model
     * @return
     */
    @RequestMapping(value="logOut")
    public String logOut(Model model){
        int count = CampsiteService.countCampInfo();
        model.addAttribute("camps", count);
        isLoggedIn = false;
        return "frontpage";
    }

    /**
     * Returns user to usersite.
     * @param model
     * @return
     */
    @RequestMapping(value="goToNotendasida")
    public String goToNotendasida(Model model){
        cList2 = CampsiteService.getCampinfo();
        model.addAttribute("camps", cList2);
        ArrayList<TravelPlan> tpList;
        tpList = travelplanService.getUserTravelplan(user);
        model.addAttribute("travelplans", tpList);
        model.addAttribute("username",user);
        model.addAttribute("user1", user);

        return "notendasida";
    }


    // ===========================
    // TRAVELPLAN HANDLING
    // ===========================

    /**
     * Deletes travelplan that user owns
     * @param model
     * @param planName - the name of the travelplan
     * @return
     */
    @RequestMapping(value="deleteTravelPlan", method = RequestMethod.POST)
    public String deleteTravelPlan(Model model, @RequestParam(value="planName") String planName){
        travelplanService.deleteTraveplan(planName, user);
        tpList = travelplanService.getUserTravelplan(user);
        model.addAttribute("travelplans",tpList);
        model.addAttribute("user", user);
        model.addAttribute("user1", user);

        return "notendasida";
    }

    @RequestMapping(value="aboutCampALot")
    public String aboutCamp(){

        return "aboutCampALot";
    }
    /**
     * Site to create a new travel plan
     * @param model     model object
     * @return          page showing the new travel plan
     */
    @RequestMapping(value="newTravelPlan", method = RequestMethod.GET)
    public String newTravelPlan(Model model){
        System.out.println(user);
        mylist = userService.getUser(user);
        if (mylist.isEmpty()){}
        else
        model.addAttribute("users", mylist.get(0));
        model.addAttribute("username",user);

        return "newTravelPlan";
    }

    /**
     * Site to add travel plan
     * @param model     model Object
     * @return          user page of the logged in user (notendasíða)
     */
    @RequestMapping(value = "/addTravel", method = RequestMethod.POST)
    public String addTravel(Model model){
        try{
            tpList = travelplanService.getTravelplans();

        }catch (Exception e){
            System.out.println(e + " getTravelplans in /addTravel");
        }
        model.addAttribute("travelplans", tpList);
        model.addAttribute("username",user);
        model.addAttribute("user1", user);

        return "notendasida";
    }

    /**
     * Site that allows user to make a new travelplan
     * @param planName      name of the new travel plan
     * @param model         model object
     * @return goes back to usersite
     */
    @RequestMapping(value = "/newTravel", method = RequestMethod.POST)
    public String newTravel(@RequestParam(value="planName") String planName, Model model)
    {   try {


        travelplanService.createTravelplan(planName, user);
        tpList = travelplanService.getUserTravelplan(user);


        model.addAttribute("travelplans", tpList);
    } catch (Exception e){
        System.out.println("newTravel error");
    }
        model.addAttribute("username",user);
        model.addAttribute("user1", user);

        return "notendasida";
    }

    /**
     * Site to add a camp item to a single travel plan
     * @param campname     name of the camp to be added
     * @param model        model Object
     * @return             page to handle adding the travel plan item to travel plan
     */
    @RequestMapping(value="/addToPlan", method = RequestMethod.POST)
    public String addToPlan(@RequestParam(value="Campname") String campname, Model model){
        Campinfo camp = CampsiteService.getOneCampinfo(campname);
        model.addAttribute("camp", camp);
        //tpList = travelplanService.getTravelplans();
        //ArrayList Greta;
        //Greta = travelplanService.getTravelplans();
        tpList = travelplanService.getUserTravelplan(user);
        campValue = campname;

        model.addAttribute("travelplans", tpList);
        model.addAttribute("username",user);
        model.addAttribute("user1", user);

        return "newTravelPlanItem";

    }
    /**
     *
     * Page to handle adding the travel plan item to travel plan
     * //@param campname    name of the camp to be added
     * //@param date        date when travelplanitem/camp is to be used
     * @param model         model Object
     * @return              page of user (notendasíða)
     */
    @RequestMapping(value = "/addTravelitem", method = RequestMethod.POST)
    public String addTravelItem(
                            @RequestParam(value="datearrive") String datearr,
                            @RequestParam(value="datedepart") String datedep,
                            @RequestParam(value="travels") String travel,
                             Model model)
    {

        Date realDatearr, realDatedep;
        System.out.println(datearr + " " + datedep);

        realDatearr = alternativeService.dateMaker(datearr);
        realDatedep = alternativeService.dateMaker(datedep);
        TravelPlanItem travelplanItem = new TravelPlanItem(realDatearr, realDatedep, 1000, campValue, user, travel);
        travelplanService.addItemtoPlan(travel, travelplanItem);
        tpiList = new ArrayList<TravelPlanItem>();
        System.out.println(travel);
        try{

            tpiList.add(travelplanItem);

        }catch (Exception e){
            System.out.println("addTravelItem error" +" "+e);
        }



        //model.addAttribute("travelplanItems", tpiList);
        //tpList = travelplanService.getTravelplans();
        tpList = travelplanService.getUserTravelplan(user);
        //System.out.println(tpList.get(1).getTravelplanname());
        model.addAttribute("travel", tpList);
        model.addAttribute("username",user);
        model.addAttribute("travelplans", tpList);
        model.addAttribute("user1", user);

        return "notendasida";
    }


    // ===========================
    // NAVIGATION MENU HANDLING
    // ===========================

    /**
     * Site that handles view of user's reviews
     * @param model     model object
     * @return          the page that shows all the user's reviews
     */
    @RequestMapping(value="/UserReviews", method = RequestMethod.GET)
    public String seeUserReviews(Model model){
        allRevList = userService.getAllReviews();
        model.addAttribute("reviews", allRevList);
        model.addAttribute("username",user);
        return "UserReviews";
    }

    @RequestMapping("help")
    public String help(Model model){
        model.addAttribute("username",user);
        return "help";
    }

    /**
     * Site that handles view of the user's travel plans
     * @param model     model object
     * @return          page that shows all the user's travel plans
     */
    @RequestMapping(value = "/myTravelplans", method = RequestMethod.GET)
    public String seeTravelPlans(Model model) {
        ArrayList<TravelPlan> userList = travelplanService.getUserTravelplan(user);
        model.addAttribute("travelplans", userList);
        model.addAttribute("username",user);
        return "myTravelPlans";
    }


    // =====================================
    // NAVIGATION MENU HANDLING -ADMIN SITE
    // =====================================


    @RequestMapping(value="goToAdminsida")
    public String goToAdminsida(Model model){
        cList2 = CampsiteService.getCampinfo();
        model.addAttribute("camps", cList2);
        model.addAttribute("username",user);
        return "adminLoginSite";
    }


    @RequestMapping("helpAdmin")
    public String helpAdmin(Model model){
        model.addAttribute("username",user);
        return "helpAdmin";
    }




    // ===========================
    // CAMP INFO HANDLING
    // ===========================

    /**
     * Site that handles a list of all the camps
     * @param model     model object
     * @return          page with a list of all campsites
     */
    @RequestMapping(value = "/listofcamps", method = RequestMethod.GET)
    public String listCamps(Model model) {

        ArrayList<Campinfo> cList2;

        cList2 = CampsiteService.getCampinfo();
        model.addAttribute("camps", cList2);
        model.addAttribute("username",user);
        if(isLoggedIn)
            return "allCampsites";
        else
            return "allCampsitesNotLoggedIn";

    }

    /**
     * Returns a site with all campsites for those who are not logged in
     * @return
     */
    @RequestMapping("allCampsitesNotLoggedIn")
    public String allCampsitesNL(){
        return "allCampsitesNotLoggedIn";
    }

    @RequestMapping("campInfoNotLoggedIn")
    public String campInfoNotLoggedIn(){
        return "campInfoNotLoggedIn";
    }

    /**
     * Site for the camp info
     * @return      page that shows the camp info
     */
    @RequestMapping("/campInfo")
    public String campInfo(Model model) {
        model.addAttribute("username",user);
        return "campInfo";
    }

    /**
     * Gets all campsites and categories them after area.
     * @param model     model object
     * @param area      area where the campsite is (String)
     * @return          site with all the campsites
     */
    @RequestMapping(value = "/showCamps", method = RequestMethod.POST)
    public String showCamps(Model model,
                            @RequestParam(value = "area") String area) {

        cList2 = CampsiteService.getCampinfo();
        ArrayList<Campinfo> cList3 = new ArrayList<Campinfo>();

        for (Campinfo c : cList2) {
            if (c.getRegion().equals(area)) {
                cList3.add(c);

            }
            model.addAttribute("camps", cList3);
        }
        if (area.equals("All"))
            model.addAttribute("camps", cList2);
        model.addAttribute("username",user);
        if(isLoggedIn)
            return "allCampsites";
        else
            return"allCampsitesNotLoggedIn";
    }
    @RequestMapping(value = "showHofud", method = RequestMethod.GET)
    public String showHofud(Model model) {

        cList2 = CampsiteService.getCampinfo();
        ArrayList<Campinfo> cList3 = new ArrayList<Campinfo>();
        String Hofudborgarsvaedid = "Hofudborgarsvaedi";
        for (Campinfo c : cList2) {
            if (c.getRegion().equals(Hofudborgarsvaedid)) {
                cList3.add(c);

            }
            model.addAttribute("camps", cList3);
        }
        if (Hofudborgarsvaedid.equals("All"))
            model.addAttribute("camps", cList2);
        model.addAttribute("username",user);
        if(isLoggedIn)
            return "allCampsites";
        else
            return"allCampsitesNotLoggedIn";
    }

    @RequestMapping(value = "showReykjanes", method = RequestMethod.GET)
    public String showReykjanes(Model model) {

        cList2 = CampsiteService.getCampinfo();
        ArrayList<Campinfo> cList3 = new ArrayList<Campinfo>();
        String Hofudborgarsvaedid = "Reykjanes";
        for (Campinfo c : cList2) {
            if (c.getRegion().equals(Hofudborgarsvaedid)) {
                cList3.add(c);

            }
            model.addAttribute("camps", cList3);
        }
        if (Hofudborgarsvaedid.equals("All"))
            model.addAttribute("camps", cList2);
        model.addAttribute("username",user);
        if(isLoggedIn)
            return "allCampsites";
        else
            return"allCampsitesNotLoggedIn";
    }

    @RequestMapping(value = "showSudurland", method = RequestMethod.GET)
    public String showSudurland(Model model) {

        cList2 = CampsiteService.getCampinfo();
        ArrayList<Campinfo> cList3 = new ArrayList<Campinfo>();
        String Hofudborgarsvaedid = "Sudurland";
        for (Campinfo c : cList2) {
            if (c.getRegion().equals(Hofudborgarsvaedid)) {
                cList3.add(c);

            }
            model.addAttribute("camps", cList3);
        }
        if (Hofudborgarsvaedid.equals("All"))
            model.addAttribute("camps", cList2);
        model.addAttribute("username",user);
        if(isLoggedIn)
            return "allCampsites";
        else
            return"allCampsitesNotLoggedIn";
    }

    @RequestMapping(value = "showAusturland", method = RequestMethod.GET)
    public String showAusturland(Model model) {

        cList2 = CampsiteService.getCampinfo();
        ArrayList<Campinfo> cList3 = new ArrayList<Campinfo>();
        String Hofudborgarsvaedid = "Austurland";
        for (Campinfo c : cList2) {
            if (c.getRegion().equals(Hofudborgarsvaedid)) {
                cList3.add(c);

            }
            model.addAttribute("camps", cList3);
        }
        if (Hofudborgarsvaedid.equals("All"))
            model.addAttribute("camps", cList2);
        model.addAttribute("username",user);
        if(isLoggedIn)
            return "allCampsites";
        else
            return"allCampsitesNotLoggedIn";
    }

    @RequestMapping(value = "showNordurland", method = RequestMethod.GET)
    public String showNordurland(Model model) {

        cList2 = CampsiteService.getCampinfo();
        ArrayList<Campinfo> cList3 = new ArrayList<Campinfo>();
        String Hofudborgarsvaedid = "Nordurland";
        for (Campinfo c : cList2) {
            if (c.getRegion().equals(Hofudborgarsvaedid)) {
                cList3.add(c);

            }
            model.addAttribute("camps", cList3);
        }
        if (Hofudborgarsvaedid.equals("All"))
            model.addAttribute("camps", cList2);
        model.addAttribute("username",user);
        if(isLoggedIn)
            return "allCampsites";
        else
            return"allCampsitesNotLoggedIn";
    }

    @RequestMapping(value = "showVesturland", method = RequestMethod.GET)
    public String showVesturland(Model model) {

        cList2 = CampsiteService.getCampinfo();
        ArrayList<Campinfo> cList3 = new ArrayList<Campinfo>();
        String Hofudborgarsvaedid = "Vesturland";
        for (Campinfo c : cList2) {
            if (c.getRegion().equals(Hofudborgarsvaedid)) {
                cList3.add(c);

            }
            model.addAttribute("camps", cList3);
        }
        if (Hofudborgarsvaedid.equals("All"))
            model.addAttribute("camps", cList2);
        model.addAttribute("username",user);
        if(isLoggedIn)
            return "allCampsites";
        else
            return"allCampsitesNotLoggedIn";
    }
    @RequestMapping(value = "showVestfirdir", method = RequestMethod.GET)
    public String showVestfirdir(Model model) {

        cList2 = CampsiteService.getCampinfo();
        ArrayList<Campinfo> cList3 = new ArrayList<Campinfo>();
        String Hofudborgarsvaedid = "Vestfirdir";
        for (Campinfo c : cList2) {
            if (c.getRegion().equals(Hofudborgarsvaedid)) {
                cList3.add(c);

            }
            model.addAttribute("camps", cList3);
        }
        if (Hofudborgarsvaedid.equals("All"))
            model.addAttribute("camps", cList2);
        model.addAttribute("username",user);
        if(isLoggedIn)
            return "allCampsites";
        else
            return"allCampsitesNotLoggedIn";
    }
    /**
     * fetches information of the camp
     * @param campName      name of the camp (String)
     * @param model         model object
     * @return skilar síðu þar sem hægt er að skoða upplýsingar um tjaldsvæði
     */
    @RequestMapping(value = "/getInfo", method = RequestMethod.POST)
    public String getInfo(@RequestParam(value = "campName") String campName, Model model) {

        rList = userService.getReviews(campName);
        Campinfo campinfo = CampsiteService.getOneCampinfo(campName);
        model.addAttribute("reviews", rList);
        model.addAttribute("campinfo", campinfo);
        model.addAttribute("username",user);
        if(isLoggedIn)
            return "campInfo";
        else
            return "campInfoNotLoggedIn";
    }

    /**
     * returns a site with information about a travelplan
     * @param model
     * @return
     */
    @RequestMapping(value="/getTravelItems", method = RequestMethod.GET)
    public String getTravelInfo(Model model){
        //tpiList = travelplanService.getOneTravelPlanItems(travelplan,user);
       // model.addAttribute("travelplanitems", tpiList);
        tpList = travelplanService.getUserTravelplan(user);
       // tpiList = travelplanService.getUserTravelplanItems(user);

        model.addAttribute("travelplanitems", tpList);
        model.addAttribute("username",user);
        return "TravelPlanInfo";
    }

    @RequestMapping(value="deletePlan", method = RequestMethod.POST)
    public String deletePlan(Model model, @RequestParam(value="planName") String planName){
        travelplanService.deleteTraveplan(planName, user);
        //tpiList = travelplanService.getUserTravelplanItems(user);
        tpList = travelplanService.getUserTravelplan(user);
        model.addAttribute("travelplanitems",tpList);
        model.addAttribute("username", user);

        return "TravelPlanInfo";
    }
    // ===========================
    // ADMIN LOGIN SITE HANDLING
    // ===========================


    // -------------------------
    // Show campsites -ADMIN
    // -------------------------


    /**
     * Fetches all campsites and categories them after area
     *
     * @param model
     * @param area area user wants to see
     * @return Returns admin site where you can see the campsites.
     */
    @RequestMapping(value = "/adminShowCamps", method = RequestMethod.POST)
    public String adminShowCamps(Model model,
                                 @RequestParam(value = "area") String area) {

        cList2 = CampsiteService.getCampinfo();
        ArrayList<Campinfo> cList3 = new ArrayList<Campinfo>();

        for (Campinfo c : cList2) {
            if (c.getRegion().equals(area)) {
                cList3.add(c);
            }
            model.addAttribute("camps", cList3);
        }
        if (area.equals("All")) {
            model.addAttribute("camps", cList2);
        }
        model.addAttribute("username",user);
        return "adminLoginSite";
    }

    /**
     * @param model
     * @return site for admin showing list of the campsites
     */
    @RequestMapping(value = "/adminGetInfo", method = RequestMethod.POST)
    public String adminGetInfo(@RequestParam(value = "campName") String campName, Model model) {
        Campinfo campinfo = CampsiteService.getOneCampinfo((campName));
        model.addAttribute("campinfo", campinfo);
        model.addAttribute("username",user);
        return "adminCampInfo";
    }

    /**
     * Returns from under-pages and goes back to the main admin page
     * param    model
     * @return site for admin showing list of the campsites
     */
    @RequestMapping(value = "/goBack", method = RequestMethod.GET)
    public String goBack(Model model) {
        cList2 = CampsiteService.getCampinfo();
        model.addAttribute("camps", cList2);
        model.addAttribute("username",user);
        return "adminLoginSite";
    }



    // --------------------------------
    // BÆTA VIÐ NÝJU TJALDSVÆÐI -ADMIN
    // --------------------------------

    /**
     * Tekur inn nafn á nýju tjaldsvæði (camp) frá admin
     * @param myNewCamp     Name of the new camp (String)
     * @param model         Model Object
     * @return  skilar síðunni adminAddNewCamp (sem tekur inn allar upplýsingar fyrir nýja tjaldsvæðið og býr það til)
     */
    @RequestMapping(value = "/addNewCampRequest", method = RequestMethod.POST)
    public String postReview(@RequestParam(value = "newCampName") String myNewCamp, Model model) {

        boolean doesExist = CampsiteService.doesCampExist(myNewCamp);

        if (doesExist) {
            model.addAttribute("AdminMessage", "This campname does already exist");
            model.addAttribute("username",user);
            return "adminLoginSite";
        } else {
            model.addAttribute("campname", myNewCamp);
            model.addAttribute("username",user);
            return "adminAddNewCamp";
        }
    }

    /**
     * site where admin can add a new camp into the database
     * @param campname      name of the camp (String)
     * @param campaddress   address of the camp (String)
     * @param campzip       zip code of the camp (String)
     * @param campemail     email of the camp (String)
     * @param campphone     phone number of the camp (String)
     * @param campwebsite   website of the camp (String)
     * @param campseason    opening season of the camp (String)
     * @param maincategory  main category of the camp (String)
     *                      (e.g. gisting/veitingar/upplýsingar...)
     * @param category      category of the camp (String)
     *                      (e.g. tjaldsvæði/bændagisting/hostel/farfuglaheimili...)
     * @param region        region of the camp (String)
     *                      (e.g. Suðurland/Norðurland/Austurland/Vesturland/Vestfirðir/Höfuðborgarsvæðið...)
     * @param description   description of the camp (String)
     * @param xval          coordinates for latitude of the camp (int)
     * @param yval          ooordinates for longitude of the camp (int)
     * @param price         price of the camp (int)
     * @param model         model object
     * @return              the adminLoginSite with the updated camp list
     */
    @RequestMapping(value = "/addNewCamp", method = RequestMethod.POST)
    public String newCamp(@RequestParam(value="campname") String campname,
                          @RequestParam(value="campaddress") String campaddress,
                          @RequestParam(value="campzip") String campzip,
                          @RequestParam(value="campemail") String campemail,
                          @RequestParam(value="campphone") String campphone,
                          @RequestParam(value="campwebsite") String campwebsite,
                          @RequestParam(value="campseason") String campseason,
                          @RequestParam(value="maincategory") String maincategory,
                          @RequestParam(value="category") String category,
                          @RequestParam(value="region") String region,
                          @RequestParam(value="description") String description,
                          @RequestParam(value="xval") int xval,
                          @RequestParam(value="yval") int yval,
                          @RequestParam(value="price") int price, Model model) {

            //Average rating sett sem núll í upphafi
            double averageRating = 0;

            Campinfo newcampinfo = new Campinfo(campname, campaddress, campzip, campemail, campphone, campwebsite,
                    campseason, maincategory, category, region, description, xval, yval, averageRating, price);

            CampsiteService.addNewCamp(newcampinfo);
            model.addAttribute("AdminMessage", "The new camp " + campname + " has been added to the list");
            cList2 = CampsiteService.getCampinfo();
            model.addAttribute("camps", cList2);
            model.addAttribute("username", user);
            return "adminLoginSite";
    }





    // ------------------------
    // EYÐA TJALDSVÆÐI -ADMIN
    // ------------------------


    /**Beiðni um að eyða tilteknu tjaldsvæði
     *
     *
     * @param campname
     * @param model
     * @return skilar deleteCamp síðunni
     */
    @RequestMapping(value = "/delCampRequest", method = RequestMethod.POST)
    public String deleteCampRequest(@RequestParam(value = "campname") String campname, Model model) {
        model.addAttribute("campname", campname);
        model.addAttribute("username",user);
        return "deleteCamp";
    }


    /**
     * Eyðir tilteknu tjaldsvæði
     * @param campname
     * @param model
     * @return  Skilar adminLoginSite síðunni
     */
        @RequestMapping(value = "/delCamp", method = RequestMethod.POST)
    public String deleteCamp(@RequestParam(value="campname") String campname, Model model) {
        System.out.println("campName is :" + campname);
        CampsiteService.delCamp(campname);
        cList2 = CampsiteService.getCampinfo();
        model.addAttribute("camps", cList2);
        model.addAttribute("username",user);
        return "adminLoginSite";
    }



    // ------------------------
    // BREYTA UPPLÝSINGUM UM TJALDSVÆÐI -ADMIN
    // ------------------------


    /**
     * @param model
     * @return site for admin showing list of the campsites
     */
    @RequestMapping(value = "/updateCampRequest", method = RequestMethod.POST)
    public String adminChangeInfo(@RequestParam(value = "campName") String campName, Model model) {
        Campinfo camp = CampsiteService.getOneCampinfo((campName));
        model.addAttribute("camp", camp);
        model.addAttribute("username",user);
        return "updateCampInfo";
    }


    /**
     * site where admin change information in a selected camp
     * @param campname      name of the camp (String)
     * @param campaddress   address of the camp (String)
     * @param campzip       zip code of the camp (String)
     * @param campemail     email of the camp (String)
     * @param campphone     phone number of the camp (String)
     * @param campwebsite   website of the camp (String)
     * @param campseason    opening season of the camp (String)
     * @param maincategory  main category of the camp (String)
     *                      (e.g. gisting/veitingar/upplýsingar...)
     * @param category      category of the camp (String)
     *                      (e.g. tjaldsvæði/bændagisting/hostel/farfuglaheimili...)
     * @param region        region of the camp (String)
     *                      (e.g. Suðurland/Norðurland/Austurland/Vesturland/Vestfirðir/Höfuðborgarsvæðið...)
     * @param description   description of the camp (String)
     * @param xval          coordinates for latitude of the camp (int)
     * @param yval          ooordinates for longitude of the camp (int)
     * @param price         price of the camp (int)
     * @param model         model object
     * @return              the adminLoginSite with the updated camp list
     */
    @RequestMapping(value = "/updateCamp", method = RequestMethod.POST)
    public String updateCamp(@RequestParam(value="campname") String campname,
                          @RequestParam(value="campaddress") String campaddress,
                          @RequestParam(value="campzip") String campzip,
                          @RequestParam(value="campemail") String campemail,
                          @RequestParam(value="campphone") String campphone,
                          @RequestParam(value="campwebsite") String campwebsite,
                          @RequestParam(value="campseason") String campseason,
                          @RequestParam(value="maincategory") String maincategory,
                          @RequestParam(value="category") String category,
                          @RequestParam(value="region") String region,
                          @RequestParam(value="description") String description,
                          @RequestParam(value="xval") int xval,
                          @RequestParam(value="yval") int yval,
                          @RequestParam(value="price") int price,  Model model) {

        Campinfo campinfo = new Campinfo();
        campinfo = CampsiteService.getOneCampinfo(campname);
        double averageRating = campinfo.getAveragerating();

        Campinfo newcampinfo = new Campinfo(campname, campaddress, campzip, campemail, campphone, campwebsite,
                campseason, maincategory, category, region, description, xval, yval, averageRating, price);

        CampsiteService.updateCamp(newcampinfo);
        model.addAttribute("AdminMessage", "The camp " + campname + " has been updated");
        cList2 = CampsiteService.getCampinfo();
        model.addAttribute("camps", cList2);
        model.addAttribute("username",user);
        return "adminLoginSite";
    }




    // ===========================
    // REVIEW AND RATING HANDLING
    // ===========================
    /**
     * Returns a site where user can write a review
     * @return giveReview site
     */
    @RequestMapping(value = "giveReview")
    public String giveReview(Model model) {
        model.addAttribute("username",user);
        return "giveReview";
    }


    /**
     *
     * @param planname
     * @param model
     * @return
     */
    @RequestMapping(value="onetravel", method = RequestMethod.GET)
    public String onePlan(@RequestParam(value="travelname") String planname, Model model){
        System.out.println("Travelname= " + planname);
        try {
            tpiList = travelplanService.getOneTravelPlan(planname, user);
            tpList = travelplanService.getUserTravelplans(planname, user);
            TravelPlan travelplan = travelplanService.onePlan(planname,user);
            //tpiList = alternativeService.dateChanger(tpiList);
            if (tpiList == null) {
            } else {
                model.addAttribute("tra", travelplan);
                model.addAttribute("travelplans", tpiList);
                model.addAttribute("travelplan", tpList);
            }
        } catch (Exception e){
            System.out.println("Controller, onetravel "+e);

        }
        model.addAttribute("username",user);
        return "OneTravelPlan";

    }
    /**
     * Finds what campsite the user wants to give a review
     * @param campName      name of the camp (String)
     * @param model         model object
     * @return returns a site where user can write a review
     */
    @RequestMapping(value = "/review", method = RequestMethod.POST)
    public String review(@RequestParam(value = "campName") String campName, Model model) {
        if(!isLoggedIn) {
            return "campInfoError";
        }
        cList2 = CampsiteService.getCampinfo();
        try{
            for (Campinfo c : cList2) {
            if (c.getCampname().equals(campName))
                model.addAttribute("camp", c);
        }}catch(Exception e){
            System.out.println("inní /review: "+e);
        }
        model.addAttribute("username",user);
        return "giveReview";
    }

    /**
     * Site to handle the posting of the review
     * @param myReview      the user's review (String)
     * @param campName      the camp name for the review (String)
     * @param model         model object
     * @return              page that shows all the information of the camp in question
     */
    @RequestMapping(value = "/postReview", method = RequestMethod.POST)
    public String postReview(@RequestParam(value = "myReview") String myReview,
                             @RequestParam(value = "campName") String campName, Model model) {

        Review review = new Review(myReview, user, campName);
        userService.addReview(review);
        Campinfo campinfo = CampsiteService.getOneCampinfo(campName);
        model.addAttribute("campinfo", campinfo);
        ArrayList<Review> rList = userService.getReviews(campName);
        model.addAttribute("reviews", rList);
        model.addAttribute("username",user);

        return "campReviews";
    }

    /**
     *
     * Site for handling rating a camp
     * @param myRating      the user's rating for the camp (String)
     * @param campName2     the name of the camp (String)
     * @param model         model Object
     * @return
     */
    @RequestMapping(value = "giveRating", method = RequestMethod.POST)
    public String giveRating(@RequestParam(value = "rating") int myRating,
                             @RequestParam(value = "campName2") String campName2, Model model) {
        if(!isLoggedIn) {
            return "campInfoError";
        }
        AverageRating rat = new AverageRating(myRating, user, campName2);
        userService.setRating(rat);
        double avrat = userService.getRating(campName2);
        userService.setAvRating(avrat, campName2);
        Campinfo campinfo = CampsiteService.getOneCampinfo(campName2);
        model.addAttribute("campinfo", campinfo);
        model.addAttribute("reviews", rList);
        model.addAttribute("username",user);
        return "campInfo";
    }

    /**
     * Goes to site where user can see reviews
     * @param model
     * @param campName the name of the campsite
     * @return returns campReview site
     */
    @RequestMapping(value = "seeReviews", method = RequestMethod.POST)
    public String seeReviews(Model model, @RequestParam(value = "campName") String campName){
        Campinfo campinfo = CampsiteService.getOneCampinfo(campName);
        model.addAttribute("campinfo", campinfo);
        model.addAttribute("username",user);
        ArrayList<Review> rList = userService.getReviews(campName);
        model.addAttribute("reviews", rList);
        return "campReviews";
    }

    /**
     * site that shows reviews
     * @param model
     * @return campReviews
     */
    @RequestMapping("/campReviews")
    public String campReviews(Model model) {
        model.addAttribute("username",user);
        return "campReviews";
    }

    /**
     * Site to see the ratings for a single camp
     * @param campName2         name of the camp (String)
     * @param model             model object
     * @return                  page that shows the rating for the camp
     */
    @RequestMapping(value = "/allratings", method = RequestMethod.POST)
    public String seeAllRatings(@RequestParam(value = "allrat") String campName2, Model model){
        Campinfo campinfo = CampsiteService.getOneCampinfo(campName2);
        model.addAttribute("campinfo", campinfo);
        ratList = userService.getRatings(campName2);
        model.addAttribute("ratings", ratList);
        model.addAttribute("username",user);
        return "seeAllRatings";
    }

    /**
     * Dæmi til að sýna prófanir með kalli á service klasa
     * @return skilar frontpage ef þjónustan "er á lífi" annars allCampsites
     */
    @RequestMapping (value = "/lifir", method=RequestMethod.GET)
    public String lifir() {
        if(CampsiteService.erALifi())
            return "frontpage";
        else
            return "allCampsites";
    }
}

