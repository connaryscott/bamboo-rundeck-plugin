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
public class RundeckAPITokenTask implements TaskType
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
        if (null == rundeckUrl || "".equals(rundeckUrl)) {
           throw new TaskException("no variable: " + Constants.RUNDECK_URL_VARNAME + " is defined or is empty");
        }

        String rundeckApiToken = RundeckTaskHelper.getVariableKeyValue(Constants.RUNDECK_API_TOKEN_VARNAME, variableDefinitions);
        if (null == rundeckApiToken || "".equals(rundeckApiToken)) {
           throw new TaskException("no variable: " + Constants.RUNDECK_API_TOKEN_VARNAME + " is defined or is empty");
        }

        String rundeckJobId = taskContext.getConfigurationMap().get("jobId");
        String rundeckJobArgs = null;

        String rundeckJobArgsLocation = taskContext.getConfigurationMap().get("jobArgsLocation");
        if (null != rundeckJobArgsLocation) {
           if (rundeckJobArgsLocation.equals("INLINE")) {
              rundeckJobArgs = taskContext.getConfigurationMap().get("jobArgsInline");
           } else if (rundeckJobArgsLocation.equals("FILE")) {
              //TODO process a properties file with args and set this string accordingly
              String rundeckJobArgsFile = taskContext.getConfigurationMap().get("jobArgsFile");
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
        buildLogger.addBuildLogEntry("rundeckApiToken:"  + "\""+rundeckApiToken+"\"");

        RundeckClient rc = new RundeckClient(rundeckUrl, rundeckApiToken);

        buildLogger.addBuildLogEntry("**************** Pinging RundeckClient ********************");
        rc.ping();

        buildLogger.addBuildLogEntry("executing RundeckClient.runJob: " + rundeckJobId  + " with properties: " + jobArgProperties.toString());

        rc.runJob(rundeckJobId, jobArgProperties);

        return TaskResultBuilder.create(taskContext).success().build();
    }
}
