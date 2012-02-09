package com.dtolabs.bamboo.plugin.rundeck;

import java.io.Serializable;
import java.util.Map;


public class RundeckConfiguration implements Serializable {

   public static final String RUNDECK_INSTALLATION_NAME = "custom.rundeck.installationName";
   public static final String RUNDECK_PASSWORD = "custom.rundeck.password";
   public static final String RUNDECK_USER = "custom.rundeck.user";
   public static final String RUNDECK_URL = "custom.rundeck.url";
   public static final String RUNDECK_APITOKEN = "custom.rundeck.apiToken";

   public static final String PLUGIN_KEY = "com.dtolabs.rundeck.plugin.rundeck-plugin";
   public static final String CONFIGURATION_KEY = PLUGIN_KEY + ":rundeckConfigs";

   private String name;
   private String rundeckPassword;
   private String rundeckUser;
   private String rundeckUrl;
   private String rundeckApiToken;


   public RundeckConfiguration() {
      super();
   }

   public RundeckConfiguration(Map<String, String> customConfiguration) {
      this.name = customConfiguration.get(RUNDECK_INSTALLATION_NAME);
      this.rundeckPassword = customConfiguration.get(RUNDECK_PASSWORD);
      this.rundeckUser = customConfiguration.get(RUNDECK_USER);
      this.rundeckUrl = customConfiguration.get(RUNDECK_URL);
      this.rundeckApiToken = customConfiguration.get(RUNDECK_APITOKEN);
   }



   public String getName() {
       if (name == null) {
           return "blank";
       }
       return name;
   }
   public void setName(String name) {
       this.name = name;
   }

   public void setRundeckPassword(String rundeckPassword) {
      this.rundeckPassword = rundeckPassword;
   }
   public String getRundeckPassword() {
      return this.rundeckPassword;
   }

   public void setRundeckUser(String rundeckUser) {
      this.rundeckUser = rundeckUser;
   }
   public String getRundeckUser() {
      return this.rundeckUser;
   }

   public void setRundeckUrl(String rundeckUrl) {
      this.rundeckUrl = rundeckUrl;
   }
   public String getRundeckUrl() {
      return this.rundeckUrl;
   }

   public void setRundeckApiToken(String rundeckApiToken) {
      this.rundeckApiToken = rundeckApiToken;
   }
   public String getRundeckApiToken() {
      return this.rundeckApiToken;
   }

   public String toString() {

      StringBuilder buffer = new StringBuilder();

      buffer.append(this.rundeckUser);
      buffer.append(" : ");
      buffer.append(this.rundeckUrl);
      return buffer.toString();
   }
 

}

