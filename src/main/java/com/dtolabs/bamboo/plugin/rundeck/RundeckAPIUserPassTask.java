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

        String rundeckJobId = RundeckTaskHelper.getVariableKeyValue(Constants.RUNDECK_JOB_ID_VARNAME, variableDefinitions);
        if (null == rundeckJobId) {
           throw new TaskException("no variable: " + Constants.RUNDECK_JOB_ID_VARNAME + " defined");
        }

        buildLogger.addBuildLogEntry("************:Constructing RundeckClient:***********");
        buildLogger.addBuildLogEntry("rundeckUrl:" + "\""+rundeckUrl+"\"");
        buildLogger.addBuildLogEntry("rundeckUser:"  + "\""+rundeckApiUser+"\"");

        RundeckClient rc = new RundeckClient(rundeckUrl, rundeckApiUser, rundeckApiPassword);

        buildLogger.addBuildLogEntry("**************** Pinging RundeckClient ********************");
        rc.ping();

        Properties jobArgProperties = RundeckTaskHelper.getJobArgs(variableDefinitions, buildLogger);
        buildLogger.addBuildLogEntry("executing RundeckClient.runJob: " + rundeckJobId  + " with properties: " + jobArgProperties.toString());

        rc.runJob(rundeckJobId, jobArgProperties);

        // no idea what this does but was part of the original hello world example
        // final String toSay = taskContext.getConfigurationMap().get("say");
        // buildLogger.addBuildLogEntry(toSay);

        return TaskResultBuilder.create(taskContext).success().build();
    }
}
