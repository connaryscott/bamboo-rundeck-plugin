package com.dtolabs.bamboo.plugin.rundeck;


public class Constants {
    // name of the rundeck url variable
    public static String RUNDECK_URL_VARNAME = "rundeck_url";

    // name of the rundeck user variable
    public static String RUNDECK_API_USER_VARNAME = "rundeck_apiUser";

    // name of the rundeck password variable
    public static String RUNDECK_API_PASSWORD_VARNAME = "rundeck_apiPassword";

    // name of the rundeck jobId variable
    public static String RUNDECK_JOB_ID_VARNAME = "rundeck_jobId";

    // regex of the rundeck jobArg variables
    public static String RUNDECK_JOB_ARGS_REGEX = "^rundeck_jobArg_([a-zA-Z0-9_][a-zA-Z0-9_]*)$";

    // name of the rundeck apiToken variable
    public static String RUNDECK_API_TOKEN_VARNAME = "rundeck_apiToken";

}
