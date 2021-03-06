package com.bizfit.bizfitUusYritysKeskusAlpha.utils;

/**
 * Created by jariJ on 4.1.2017.
 */

public class Constants
{
	//User

	public static final String user_name = "_id";
	public static String getUser_Name(){
		return "_id";
	}
	public static final int db_version = 103;
    public static final String conversations = "conversations";
    public static final String check_sum = "checkSum";
    public static final String trackers = "trackers";
    public static final String save = "save";
    public static final String user = "user";
    public static final String save_tracker = "save_tracker";
    public static final String save_conversation = "save_conversation";
    public static final String tracker = "tracker";
    public static final String conversation = "conversation";
    public static final String load = "load";
	public static final String atte_email = "atte.yliverronen@gmail.com";
	public static final String jariM_email = "jari.myllymaki@gmail.com";
	public static final String pasi_email = "pasojan@gmail.com";
	public static final String jari_email = "jari.k4rita@gmail.com";
	public static final String support_email = "bizfithelp@bizfit.fi";


	// ensimetri constants

	public static final String kaj_email = "kaj.heinio@gmail.com";
	public static final String taina_email = "tainanmaili@gmail.com";
	public static final String tapani_email = "tapani.kaskela@gmail.com";

	//Message
	public static final String owner = "owner";
	public static final String other = "other";
	public static final String messages = "messages";
	public static final String job = "job";
	public static final String get_message_incoming = "get_message_incoming";
	public static final String creationTime = "creationTime";
	public static final String get_message_outgoing = "get_message_outgoing";
	public static final String resipient = "resipient";
	public static final String sender = "sender";
	public static final String message = "message";
	public static final String hasBeenSent = "hasBeenSent";
	public static final String send_message = "send_message";
	public static final String hasBeenSeen="hasBeenSeen";
    public static final String timestamp="timestamp";



    //Tracker
	public static final String start_date = "startDate";
	public static final String end_date = "endDate";
	public static final String end_progress = "endProgress";
	public static final String target_progress = "targetProgress";
	public static final String id = "id";

	public static final String daily_progress = "dailyProgress";
	public static final String old_progress = "oldProgress";
	public static final String last_reset = "lastReset";
	public static final String day_interval = "dayInterval";
	public static final String month_interval = "monthInterval";
	public static final String year_interval = "yearInterval";
	public static final String current_progress = "currentProgress";
	public static final String default_increment = "defaultIncrement";
	public static final String time_progress = "timeProgress";
	public static final String time_progress_need = "timeProgressNeed";
	public static final String tracker_name = "name";
	public static final String target_type = "targetType";
	public static final String daily = "daily";
	public static final String weekly = "weekly";
	public static final String repeat = "repeat";
	public static final String completed = "completed";
	public static final String tolerance = "tolerance";
	public static final String color = "color";
	public static final String number_tracked = "numberTracked";
	public static final String trackerShaderWith="trackerSharedWith";
	public static final String deleted_trackers = "DeletedTrackers";
	public static final String shared_trackers = "SharedTrackers";
	public static final String shared_tracker_name = "TrackerName";
	public static final String shared_tracker_status = "SharedTrackerStatus";
	public static final String cancel_sharing = "ChangeTrackerSharingStatus";




	//public static final String last_tracker_id = "lastTrackerID";
	//public static final String next_free_daily_progress_id = "nextFreeDailyProgressID";
	//public static final String user_number = "userNumber";

    //NetMessage
	private static final String debug_address="http://bizfit-nyyppa.c9users.io";
	private static final String deployment_address ="http://51.15.37.28:8080";
	public static final String connection_address = deployment_address;

    //CoachPage
	public static final String coach_id = "coachID";
	public static final String networkconn_failed = "failed";
    public static final String telNumber="telNumber";

	//Generic
	public static final String UUID="UUID";
	public static final String version="Alpha";

    //ChatRequest
    public static final String customer = "customer";
    public static final String coach = "coach";
    public static final String message_ChatRequest = "message";

	//ChatRequestArrayAdapter
	public static final String handle_chat_request="handleChatRequest";
	public static final String resultChat = "resultChat";

}
