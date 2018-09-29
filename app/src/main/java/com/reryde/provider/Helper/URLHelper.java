package com.reryde.provider.Helper;

/**
 * Created by jayakumar on 26/12/16.
 */

public class URLHelper {
    public static final String base = "https://rideshare.reryde.com/";
//    public static final String base = "http://stagging.reryde.com/";
    //public static final String base = "http://6586b14b.ngrok.io/";
    public static final String HELP_URL = base+"";
    public static final String CALL_PHONE = "1";
    public static final String APP_URL = "https://play.google.com/store/apps/details?id=com.reryde.provider&hl=en";
    public static final String login = base + "api/provider/oauth/token";
    public static final String register = base + "api/provider/register";
    public static final String CHECK_MAIL_ALREADY_REGISTERED = base+"api/provider/verify";
    public static final String USER_PROFILE_API = base + "api/provider/profile";
    public static final String UPDATE_AVAILABILITY_API = base + "api/provider/profile/available";
    public static final String GET_HISTORY_API = base + "api/provider/requests/history";
    public static final String GET_HISTORY_DETAILS_API = base + "api/provider/requests/history/details";
    public static final String CHANGE_PASSWORD_API = base + "api/provider/profile/password";
    public static final String UPCOMING_TRIP_DETAILS = base + "api/provider/requests/upcoming/details";
    public static final String UPCOMING_TRIPS = base + "api/provider/requests/upcoming";
    public static final String CANCEL_REQUEST_API = base + "api/provider/cancel";
    public static final String TARGET_API = base + "api/provider/target";
    public static final String RESET_PASSWORD = base + "api/provider/reset/password";
    public static final String FORGET_PASSWORD = base + "api/provider/forgot/password";
    public static final String FACEBOOK_LOGIN = base + "api/provider/auth/facebook";
    public static final String GOOGLE_LOGIN = base + "api/provider/auth/google";
    public static final String LOGOUT = base + "api/provider/logout";
    public static final String SUMMARY = base + "api/provider/summary";
    public static final String HELP = base + "api/provider/help";
    public static final String TERMS = base + "terms";
    public static final String GET_SERVICE_TYPES = base + "services";

    public static final String GET_DOC = base + "api/provider/profile/document";
    public static final String UPDATE_DOC = base + "api/provider/profile/documentupload";

    public static final String map_address_url = "https://maps.googleapis.com/maps/api/geocode/";

    public static final String Login_otp = base + "api/provider/provider/otp";
    public static final String register_otp = base + "api/provider/provider/sent/otp";
    public static final String RatingApi = "api/provider/ratings";

    public static final String GetLostITEM = base +"api/provider/found/item";
    public static final String SendResponse = base +"api/provider/response/";
    public static final String CheckReferral = base +"api/provider/referral/check";


}
