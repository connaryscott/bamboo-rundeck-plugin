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

import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import com.atlassian.bamboo.variable.VariableDefinitionContext;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.build.logger.BuildLogger;

import org.rundeck.api.OptionsBuilder;


public class RundeckTaskHelper 
{

    protected static String getVariableKeyValue(String key, Map variableDefinitions) throws TaskException {

       // get a list of variable keys for rundeck info in order to connect properly to the rundeck server
       VariableDefinitionContext variableDefinitionContext =  (VariableDefinitionContext)variableDefinitions.get((Object)key);

       if (null == variableDefinitionContext) throw new TaskException("unable to get key variable: " + key);

       String returnValue = (String)variableDefinitionContext.getValue();

       if (null == returnValue) throw new TaskException("key variable:" + key + " does not have any value");

       return returnValue;
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

   public static Properties convertArgsToProperties(String args, BuildLogger buildLogger) throws TaskException {
      Properties argProperties = new Properties();

      String[] words = getWords(args, buildLogger);

      String key = null;
      for (int i=0; i<words.length; i++) {
         if (i % 2 == 0) {
            //should be a key prefixed by hyphen
            if (!words[i].startsWith("-")) {
              throw new TaskException("jobArgs format error, param: " + words[i] + " does not begin with -");
            }
            key = words[i].substring(1);
         } else {
            if (null == key) {
              throw new TaskException("null key, cannot set null key with value: " + words[i]);
            }
            argProperties.setProperty(key, words[i]);
            key = null;
         }
      }

      return argProperties;
   }

   public static String convertFileToArgs(String fileName, BuildLogger buildLogger) throws TaskException {
      Properties properties = new Properties();
      try {
         properties.load(new FileInputStream(fileName));
      } catch  (FileNotFoundException e) {
         throw new TaskException("file: "+ fileName + " not found");
      } catch  (IOException e) {
         throw new TaskException("while loading file: "+ fileName + " caught IOException");
      }

      Set propertyNamesSet = properties.stringPropertyNames();
      String propertyNames[] = (String [])propertyNamesSet.toArray(new String[0]);


      StringBuffer sb = new StringBuffer();
      for (int i=0; i<propertyNames.length; i++) {
          String propertyName = propertyNames[i];
          if (propertyName.startsWith(Constants.FILE_JOB_ARGS_PROPERTY_PREFIX)) {
             if (sb.length() != 0) {
                sb.append(" ");
             }
             sb.append(properties.getProperty(propertyName));
          }
      }
      return sb.toString();
   }

   // parse out words, strings enclosed in double quotes, or whitespace separated strings
   private static String[] getWords(String args, BuildLogger buildLogger) throws TaskException {

     buildLogger.addBuildLogEntry("args:  " + args);

      // if no double quotes, then white space is our token separator
      if (args.indexOf("\"") == -1) {
         return args.split("[\\s]+");
      }

     // odd number of quotes is not valid since quotes will be unbalanced
     if   ( ( args.replaceAll("[^\"]","").trim().length() % 2 ) != 0 ) throw new TaskException ("unbalanced quotes: " + args);

      // tokenize on double quotes
      String[] argStrings = args.split("[\"]");

      // add a list of words to the wordList
      ArrayList<String> wordList = new <String>ArrayList();
      for (int i=0; i<argStrings.length; i++) {
         if (i % 2 == 0) {
            // if first, third, ... then we should have strings which are whitespace separated and not enclosed in quotes
            String[] words = argStrings[i].split("[\\s]+");
            for (int j=0; j<words.length; j++) {
               if (words[j].equals("")) {
                  continue;
               }
               wordList.add(words[j]);
            }
         } else {
            // if second, fourth, ... then we should have a quoted word 
           String quotedWord = argStrings[i];
           wordList.add(quotedWord);
         }

      }

      return wordList.toArray(new String[wordList.size()]);
   }

}
