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
import org.rundeck.api.domain.RundeckExecution;
import org.rundeck.api.domain.RundeckOutput;
import org.rundeck.api.domain.RundeckOutputEntry;
import com.atlassian.bamboo.v2.build.BuildContext;
import com.atlassian.bamboo.variable.VariableContext;
import java.util.Map;
import java.util.List;
import java.util.Properties;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;


public abstract class RundeckAPITaskBase implements TaskType
{

    public BuildLogger buildLogger;

    private TaskContext taskContext;
    private void setTaskContext(TaskContext taskContext) {
       buildLogger = taskContext.getBuildLogger();
       this.taskContext = taskContext;
    }

    private Map variableDefinitions;
    private void setVariableDefinitions() {
        BuildContext buildContext = taskContext.getBuildContext();
        VariableContext variableContext = buildContext.getVariableContext();
        variableDefinitions = variableContext.getDefinitions();
        this.taskContext = taskContext;
    }

    private boolean rundeckDisable = false;
    private void setRundeckDisable() throws TaskException {
      
       String rundeckDisable = null;
       try {
          rundeckDisable = RundeckTaskHelper.getVariableKeyValue(Constants.RUNDECK_DISABLE_VARNAME, variableDefinitions);
       } catch (TaskException e) {
          buildLogger.addBuildLogEntry("plan variable: " + Constants.RUNDECK_DISABLE_VARNAME + " not defined, automatically setting to false");
       }
       if (null == rundeckDisable || "".equals(rundeckDisable)) {
          return;
       }
       if ("TRUE".equals(rundeckDisable) || 
           "true".equals(rundeckDisable) ||
           "YES".equals(rundeckDisable)  ||
           "yes".equals(rundeckDisable)) {
          this.rundeckDisable = true;
          buildLogger.addBuildLogEntry("bamboo variable: " + Constants.RUNDECK_DISABLE_VARNAME + " set to: " + rundeckDisable + " disabling rundeck plugin");
       }
       return;
    }

    private String rundeckUrl;
    private void setRundeckUrl() throws TaskException {
        buildLogger.addBuildLogEntry("getting rundeck url from bamboo variable:  " + Constants.RUNDECK_URL_VARNAME);
        rundeckUrl = RundeckTaskHelper.getVariableKeyValue(Constants.RUNDECK_URL_VARNAME, variableDefinitions);
        if (null == rundeckUrl || "".equals(rundeckUrl)) {
           throw new TaskException("no variable: " + Constants.RUNDECK_URL_VARNAME + " is defined or is empty");
        }
    }

    private String rundeckApiToken;
    private void setRundeckApiToken() throws TaskException {
        buildLogger.addBuildLogEntry("getting rundeck api token from bamboo variable:  " + Constants.RUNDECK_API_TOKEN_VARNAME);
        rundeckApiToken = RundeckTaskHelper.getVariableKeyValue(Constants.RUNDECK_API_TOKEN_VARNAME, variableDefinitions);
        if (null == rundeckApiToken || "".equals(rundeckApiToken)) {
           throw new TaskException("no variable: " + Constants.RUNDECK_API_TOKEN_VARNAME + " is defined or is empty");
        }
    }

    private String rundeckApiUser;
    private void setRundeckApiUser() throws TaskException {
        buildLogger.addBuildLogEntry("getting rundeck api user from bamboo variable:  " + Constants.RUNDECK_API_USER_VARNAME);
        rundeckApiUser = RundeckTaskHelper.getVariableKeyValue(Constants.RUNDECK_API_USER_VARNAME, variableDefinitions);
        if (null == rundeckApiUser || "".equals(rundeckApiUser)) {
           throw new TaskException("no variable: " + Constants.RUNDECK_API_USER_VARNAME + " is defined or is empty");
        }
    }

    private String rundeckApiPassword;
    private void setRundeckApiPassword() throws TaskException {
        buildLogger.addBuildLogEntry("getting rundeck password from bamboo variable:  " + Constants.RUNDECK_API_PASSWORD_VARNAME);
        rundeckApiPassword = RundeckTaskHelper.getVariableKeyValue(Constants.RUNDECK_API_PASSWORD_VARNAME, variableDefinitions);
        if (null == rundeckApiPassword || "".equals(rundeckApiPassword)) {
           throw new TaskException("no variable: " + Constants.RUNDECK_API_PASSWORD_VARNAME + " is defined or is empty");
        }
    }

    private String rundeckJobId;
    private void setRundeckJobId() throws TaskException {
        buildLogger.addBuildLogEntry("getting rundeck jobId from task param:  " + Constants.PARAM_JOBID);
       rundeckJobId = taskContext.getConfigurationMap().get(Constants.PARAM_JOBID);
        if (null == rundeckJobId || "".equals(rundeckJobId)) {
           throw new TaskException("no variable: " + Constants.PARAM_JOBID + " is defined or is empty");
        }
    }

    private Properties jobArgProperties;
    private void setJobArgProperties() throws TaskException {

        String rundeckJobArgs = null;
        String rundeckJobArgsLocation = taskContext.getConfigurationMap().get(Constants.PARAM_JOBARGSLOCATION);
        if (null != rundeckJobArgsLocation) {
           if (rundeckJobArgsLocation.equals(Constants.INLINE)) {
              buildLogger.addBuildLogEntry("getting jobArgs from param: " + Constants.INLINE);
              rundeckJobArgs = taskContext.getConfigurationMap().get(Constants.PARAM_JOBARGSINLINE);
           } else if (rundeckJobArgsLocation.equals(Constants.FILE)) {
              String rundeckJobArgsFile = taskContext.getConfigurationMap().get(Constants.PARAM_JOBARGSFILE);
              buildLogger.addBuildLogEntry("loading jobArgs from file: " + rundeckJobArgsFile);
              rundeckJobArgs = RundeckTaskHelper.convertFileToArgs(rundeckJobArgsFile, buildLogger);
           } else {
              // this should not happen
              rundeckJobArgs = "";
           }
        } else {
           buildLogger.addBuildLogEntry("no jobArgs detected, setting to empty string");
           rundeckJobArgs = "";
        }
       jobArgProperties = RundeckTaskHelper.convertArgsToProperties(rundeckJobArgs, buildLogger);
    }

    private RundeckClient getRundeckClient() throws TaskException {
       RundeckClient rc = null;
       setRundeckUrl();
       if (null != rundeckApiToken) {
          buildLogger.addBuildLogEntry("constructing RundeckClient using api token method");
          rc = new RundeckClient(rundeckUrl, rundeckApiToken);
          return rc;
       }
       if (null != rundeckApiUser && null != rundeckApiPassword) {
          buildLogger.addBuildLogEntry("constructing RundeckClient using apip user/password method");
          rc = new RundeckClient(rundeckUrl, rundeckApiUser, rundeckApiPassword);
          return rc;
       }
       throw new TaskException("RundeckClient not set, rundeck apiToken or apiUser/apiPasssword not set");
    }

    private TaskResult runJob()  throws TaskException {
        setRundeckDisable();
        if (rundeckDisable != true) {
           setRundeckJobId();
           setJobArgProperties();
           RundeckClient rc = getRundeckClient();
           buildLogger.addBuildLogEntry("pinging rundeck server");
           rc.ping();
           buildLogger.addBuildLogEntry("running rundeck job, jobId: " + rundeckJobId + " with argProperties: " + jobArgProperties.toString());
           RundeckExecution rundeckExecution = rc.runJob(rundeckJobId, jobArgProperties); 
           RundeckExecution.ExecutionStatus jobStatus = rundeckExecution.getStatus();

           // we must get the logs here and send it back to the api caller

           buildLogger.addBuildLogEntry("BEGIN RUNDECK LOG OUTPUT");
           int offset=0;
           while (true) {
              RundeckOutput rundeckOutput = rc.getJobExecutionOutput(rundeckExecution.getId(), offset, 0, 0);
              if (null == rundeckOutput) {
                 break;
              }
              List<RundeckOutputEntry> logEntries = rundeckOutput.getLogEntries();
              if (null == logEntries) {
                 break;
              }
              for (int i=0; i<logEntries.size(); i++) {   
                 RundeckOutputEntry rundeckOutputEntry = (RundeckOutputEntry)logEntries.get(i);
                 buildLogger.addBuildLogEntry(rundeckOutputEntry.getMessage());
              }
              offset+=rundeckOutput.getOffset();
           }
           buildLogger.addBuildLogEntry("END RUNDECK LOG OUTPUT");

           if (jobStatus == jobStatus.FAILED) {
              throw new TaskException("rundeckJobId:  " + rundeckJobId + " failed");
           }
           if (jobStatus == jobStatus.ABORTED) {
              throw new TaskException("rundeckJobId:  " + rundeckJobId + " aborted");
           }
           if (jobStatus == jobStatus.RUNNING) {
              throw new TaskException("rundeckJobId:  " + rundeckJobId + " still running");
           }
           if (jobStatus != jobStatus.SUCCEEDED) {
              throw new TaskException("rundeckJobId:  " + rundeckJobId + " did not succeed, unknown reason");
           }
        } else {
           buildLogger.addBuildLogEntry("rundeck integration disabled, NOT running rundeck job");
        }
        return TaskResultBuilder.create(taskContext).success().build();
    }


    public TaskResult executeUserPassStrategy(@NotNull final TaskContext taskContext)  throws TaskException
    {
        setTaskContext(taskContext);
        setVariableDefinitions();
        setRundeckApiUser();
        setRundeckApiPassword();
        return runJob();
    }

    public TaskResult executeTokenStrategy(@NotNull final TaskContext taskContext)  throws TaskException
    {
        setTaskContext(taskContext);
        setVariableDefinitions();
        setRundeckApiToken();
        return runJob();
    }

    public abstract TaskResult execute(@NotNull final TaskContext taskContext) throws TaskException;

}
