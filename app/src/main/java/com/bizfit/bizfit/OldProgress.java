package com.bizfit.bizfit;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Atte on 22.11.2015.
 * Class containing information on expired tracking period
 */
public class OldProgress implements java.io.Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -8599112903840590954L;
    public int id = -1;
    private long startDate;
    private long endDate;
    private float endProgress;
    private float targetProgress;
    private DailyProgress daily;


    /**
     * @param startDate      Start date of tracked time period in milliseconds
     * @param endDate        end date of tracked time period in milliseconds
     * @param endProgress    how much progress was achieved
     * @param targetProgress what was desired progress
     */
    OldProgress(long startDate, long endDate, float endProgress, float targetProgress, DailyProgress dailyProgress) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.endProgress = endProgress;
        this.targetProgress = targetProgress;
        this.daily = dailyProgress;

    }

    /**
     * Constructs OldProgress from given JSON
     *
     * @param jsonObject JSON containing nessessary information for constructing OldProgress
     */
    OldProgress(JSONObject jsonObject) {
        try
        {
            if(jsonObject.has("startDate"))
            {
                startDate = jsonObject.getLong("startDate");
            }

            if(jsonObject.has("endDate"))
            {
                endDate = jsonObject.getLong("endDate");
            }
            if(jsonObject.has("endProgress"))
            {
                endProgress = (float) jsonObject.getDouble("endProgress");
            }
            if(jsonObject.has("targetProgress"))
            {
                targetProgress = (float) jsonObject.getDouble("targetProgress");
            }
            if(jsonObject.has("id"))
            {
                id = jsonObject.getInt("id");
            }
            if(jsonObject.has("dailyProgress"))
            {
                daily = new DailyProgress(jsonObject.getJSONObject("DailyProgress"));
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Converts OLdProgress and it's dependensies to JSON
     *
     * @return JSON containing OldProgress and it's dependensies
     */
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("startDate", startDate);
            jsonObject.put("endDate", endDate);
            jsonObject.put("endProgress", endProgress);
            jsonObject.put("targetProgress", targetProgress);
            jsonObject.put("id", id);
            jsonObject.put("DailyProgress", daily.toJSon());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * @return start date in milliseconds
     */
    public long getStartDate() {
        return startDate;
    }

    /**
     * @return end date in milliseconds
     */
    public long getEndDate() {
        return endDate;
    }

    /**
     * @return how great progress was achieved in persents, 1=100%
     */
    public float getProgressPercent() {
        return endProgress / targetProgress;
    }

    /**
     * @return achieved progress as absolute amount
     */
    public float getProgress() {
        return endProgress;
    }

    /**
     * @return desired progress as absolute amount
     */
    public float getTargetProgress() {
        return targetProgress;
    }

    /**
     * @return DailyProgress of this oldProgress
     */
    public DailyProgress getDailyProgress() {
        return daily;
    }

}

