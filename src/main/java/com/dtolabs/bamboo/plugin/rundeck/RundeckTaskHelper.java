package com.dtolabs.bamboo.plugin.rundeck;

import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Properties;
import com.atlassian.bamboo.variable.VariableDefinitionContext;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.build.logger.BuildLogger;

import org.rundeck.api.OptionsBuilder;


public class RundeckTaskHelper 
{

    protected static String getVariableKeyValue(String key, Map variableDefinitions) throws TaskException {

       // get a list of variable keys for rundeck info in order to connect properly to the rundeck server
       VariableDefinitionContext variableDefinitionContext =  (VariableDefinitionContext)variableDefinitions.get((Object)key);

       String returnValue = (String)variableDefinitionContext.getValue();
       if (null == returnValue) throw new TaskException("variable " + key + " is not defined");
       return returnValue;
    }


    protected static Properties getJobArgs(Map variableDefinitions, BuildLogger buildLogger) throws TaskException {
       return getVariableKeyValuesByRegex(Constants.RUNDECK_JOB_ARGS_REGEX, variableDefinitions, buildLogger);
    }

    protected static Properties getVariableKeyValuesByRegex(String keyRegex, Map variableDefinitions, BuildLogger buildLogger) throws TaskException {
       
       Set variableKeySet = variableDefinitions.keySet();
       Object[] keys = variableKeySet.toArray();


       OptionsBuilder options = new OptionsBuilder();

       for (int i=0; i<keys.length; i++) {

          String planVarKey = (String)keys[i];
          buildLogger.addBuildLogEntry("checking key: " + planVarKey + " to match regex: " + keyRegex);

          if (planVarKey.matches(keyRegex)) {
             buildLogger.addBuildLogEntry("key: " + planVarKey + " key matches jobarg pattern");
             String jobArgKey=planVarKey.replaceAll(keyRegex, "$1");
             buildLogger.addBuildLogEntry("jobArgKey: " + jobArgKey + " from: " + planVarKey);
             String keyValue = getVariableKeyValue(planVarKey, variableDefinitions);
             buildLogger.addBuildLogEntry("keyValue: " + keyValue) ;
             options.addOption(jobArgKey, keyValue);
          } else {
             buildLogger.addBuildLogEntry("key: " + planVarKey + " NO key match jobarg pattern:");
          }
       }

       return options.toProperties();

    }


   public static Properties convertArgsToProperties(String args) throws TaskException {
      Properties argProperties = new Properties();


      String[] subStrings = args.split("[\\s]+");
      boolean isParam=false;
      boolean isValue=false;

      for (int i=0; i<subStrings.length;) {
         System.out.println("substring: " + subStrings[i]);
         String paramString = subStrings[i];

         String param = null;
         String value = null;
         if (paramString.startsWith("-")) {
            param = paramString.substring(1);
         } else {
             System.out.println("throw arg format param error + " + param);
             throw new TaskException("jobArgs format error, param: " + param + " does not begin with -");
         }

         i++;
         if (i == subStrings.length) {
             throw new TaskException("jobArgs format param error, param: " + param + " does not have a value");
         }
         value = subStrings[i];

         //System.out.println("param: " + param);
         //System.out.println("value: " + value);

         argProperties.setProperty(param, value);

         i++;
      }

      return argProperties;
   }


}
