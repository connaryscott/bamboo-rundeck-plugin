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

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.atlassian.bamboo.task.TaskType;
import org.jetbrains.annotations.NotNull;

import org.rundeck.api.RundeckClient;
import com.atlassian.bamboo.v2.build.BuildContext;
import com.atlassian.bamboo.variable.VariableContext;
import java.util.Map;
import java.util.Properties;

    //public RundeckExecution runJob(String jobId, Properties options) throws RundeckApiException,

/**
 * running a rundeck job via  api token
 * TODO, Since this class is the api token strategy, and there will be others (e.g. auth strategy), we will have a higher level interface
 */
public class RundeckAPIUserPassTask implements TaskType
{

    // TODO, make this configurable in the plugin to identify what plan variables we use

    // before we figure out how to make a configurable interface for this plugin,
    //    we require configuration to be derived from paln properties.

    @NotNull
    @java.lang.Override
    public TaskResult execute(@NotNull final TaskContext taskContext) throws TaskException
    {
        final BuildLogger buildLogger = taskContext.getBuildLogger();

        BuildContext buildContext = taskContext.getBuildContext();
        VariableContext variableContext = buildContext.getVariableContext();
        Map variableDefinitions = variableContext.getDefinitions();

        String rundeckUrl = RundeckTaskHelper.getVariableKeyValue(Constants.RUNDECK_URL_VARNAME, variableDefinitions);
        if (null == rundeckUrl) {
           throw new TaskException("no variable: " + Constants.RUNDECK_URL_VARNAME + " defined");
        }

        String rundeckApiUser = RundeckTaskHelper.getVariableKeyValue(Constants.RUNDECK_API_USER_VARNAME, variableDefinitions);
        if (null == rundeckApiUser) {
           throw new TaskException("no variable: " + Constants.RUNDECK_API_USER_VARNAME + " defined");
        }

        String rundeckApiPassword = RundeckTaskHelper.getVariableKeyValue(Constants.RUNDECK_API_PASSWORD_VARNAME, variableDefinitions);
        if (null == rundeckApiPassword) {
           throw new TaskException("no variable: " + Constants.RUNDECK_API_PASSWORD_VARNAME + " defined");
        }

        String rundeckJobId = taskContext.getConfigurationMap().get(Constants.PARAM_JOBID);
        String rundeckJobArgs = null;

        String rundeckJobArgsLocation = taskContext.getConfigurationMap().get(Constants.PARAM_JOBARGSLOCATION);
        if (null != rundeckJobArgsLocation) {
           if (rundeckJobArgsLocation.equals(Constants.INLINE)) {
              rundeckJobArgs = taskContext.getConfigurationMap().get(Constants.PARAM_JOBARGSINLINE);
           } else if (rundeckJobArgsLocation.equals(Constants.FILE)) {
              //TODO process a properties file with args and set this string accordingly
              String rundeckJobArgsFile = taskContext.getConfigurationMap().get(Constants.PARAM_JOBARGSFILE);
              rundeckJobArgs = RundeckTaskHelper.convertFileToArgs(rundeckJobArgsFile);
           } else {
              // this should not happen
              rundeckJobArgs = "";
           }
        } else {
           rundeckJobArgs = "";
        }

        Properties jobArgProperties = RundeckTaskHelper.convertArgsToProperties(rundeckJobArgs);

        buildLogger.addBuildLogEntry("************:Constructing RundeckClient:***********");
        buildLogger.addBuildLogEntry("rundeckUrl:" + "\""+rundeckUrl+"\"");
        buildLogger.addBuildLogEntry("rundeckUser:"  + "\""+rundeckApiUser+"\"");

        RundeckClient rc = new RundeckClient(rundeckUrl, rundeckApiUser, rundeckApiPassword);

        buildLogger.addBuildLogEntry("**************** Pinging RundeckClient ********************");
        rc.ping();

        buildLogger.addBuildLogEntry("executing RundeckClient.runJob: " + rundeckJobId  + " with properties: " + jobArgProperties.toString());

        rc.runJob(rundeckJobId, jobArgProperties);

        return TaskResultBuilder.create(taskContext).success().build();
    }
}
