/*
 * Copyright 2010 DTO Labs, Inc. (http://dtolabs.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.dtolabs.bamboo.plugin.rundeck;


public class Constants {
    // name of the rundeck url plan variable
    public static String RUNDECK_URL_VARNAME = "rundeck.url";

    // name of the rundeck apiToken plan variable
    public static String RUNDECK_API_TOKEN_VARNAME = "rundeck.apiToken";

    // name of the rundeck user plan variable
    public static String RUNDECK_API_USER_VARNAME = "rundeck.apiUser";

    // name of the rundeck password plan variable
    public static String RUNDECK_API_PASSWORD_VARNAME = "rundeck.apiPassword";

    // INLINE method for job args
    public static String INLINE = "INLINE";

    // FILE method for job args
    public static String FILE = "FILE";


    // UI Configuration Map Parameters
    public static String PARAM_JOBARGSFILE = "jobArgsFile";
    public static String PARAM_JOBARGSLOCATION = "jobArgsLocation";
    public static String PARAM_JOBID = "jobId";
    public static String PARAM_JOBARGSINLINE = "jobArgsInline";
    public static String PARAM_JOBARGSLOCATIONTYPES = "jobArgsLocationTypes";

    //I18N PROPERTY NAMES
    public static String I18N_JOBARGS_LOCATION_FILE = "bamboo_rundeck_plugin.jobArgs.location.file";
    public static String I18N_JOBARGS_LOCATION_INLINE = "bamboo_rundeck_plugin.jobArgs.location.inline";
    public static String I18N_JOBID_ERROR = "bamboo_rundeck_plugin.jobId.error";

    // property prefix for file generated jobArgs
    public static String FILE_JOB_ARGS_PROPERTY_PREFIX = "bamboo_rundeck_plugin.jobArgs.";

}
