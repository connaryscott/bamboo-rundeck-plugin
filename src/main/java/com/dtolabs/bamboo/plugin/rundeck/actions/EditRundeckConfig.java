package com.dtolabs.bamboo.plugin.rundeck.actions;
 
import com.atlassian.bamboo.bandana.PlanAwareBandanaContext;
import com.dtolabs.bamboo.plugin.rundeck.RundeckConfiguration;
 
import org.apache.commons.lang.StringUtils;
 
/**
 * WebWork Action that handles the 'edit' operations in the Rundeck admin interface.
 *
 * @author Ross Rowe
 */
public class EditRundeckConfig extends RundeckActionSupport {
 
   private String installationName;
   private String rundeckPassword;
   private String rundeckUser;
   private String rundeckUrl;
   private String rundeckApiToken;
   private String selectedConfig;
 
   /**
     * Invoked when the 'Edit' link is clicked.  The content for the selected configuration
     * is loaded, and the corresponding instance variables are populated with the configuration
     * information.
     *
     * @return 'input'
     * @throws Exception
     */
   @Override
   public String doExecute() throws Exception {
       loadConfiguration();
       if (!StringUtils.isEmpty(selectedConfig)) {
           RundeckConfiguration config = loadConfiguration(selectedConfig);
           if (config == null) {
               addActionError("Unable to find configuration for name: "+ selectedConfig);
           } else {
               setInstallationName(config.getName());
               setRundeckPassword(config.getRundeckPassword());
               setRundeckUser(config.getRundeckUser());
               setRundeckUrl(config.getRundeckUrl());
               setRundeckApiToken(config.getRundeckApiToken());
           }
       }
       return INPUT;
   }
 
   /**
     * Invoked when the 'Save' button is clicked'.
     *
     * @return 'success'
     * @throws Exception
     */
   public String doUpdate() throws Exception {
       clearErrorsAndMessages();
       if (!validateFields()) {
           return INPUT;
       }
       loadConfiguration();
       if (includesConfiguration()) {
           editConfiguration();
       } else {
           addNewConfiguration();
       }
       storeConfiguration();
       return SUCCESS;
   }
 
   private void editConfiguration() {
       RundeckConfiguration config = loadConfiguration(installationName);
       modifyConfiguration(config);
   }
 
   /**
     *
     * @return boolean indicating whether a configuration that has a name set to 'installationName'
     * exists in the list of configurations
     *
     */
   private boolean includesConfiguration() {
       return loadConfiguration(installationName) != null;
   }
 
   /**
     * Creates a new RundeckConfiguration instance populated with the information held in the instance
     * variables, then adds the instance into the list of configurations.
     */
   private void addNewConfiguration() {
       RundeckConfiguration config = new RundeckConfiguration();
       modifyConfiguration(config);
       getRundeckConfigs().add(config);
   }
 
   /**
     * Updates the variables in the <code>config</code> instance to use those that have been entered
     * via the UI.
     *
     * @param config
     */
   private void modifyConfiguration(RundeckConfiguration config) {
       config.setName(getInstallationName());
       config.setRundeckPassword(getRundeckPassword());
       config.setRundeckUser(getRundeckUser());
       config.setRundeckUrl(getRundeckUrl());
       config.setRundeckApiToken(getRundeckApiToken());
   }
 
   /**
     * Runs the validation on the instance variables.
     *
     * @return boolean indicating whether or not we have an error
     */
   private boolean validateFields() {
       boolean isValid = true;
       if (StringUtils.isBlank(installationName)) {
           addActionError("Installation Name must be entered");
           isValid = false;
       }
       if (StringUtils.isBlank(rundeckUrl)) {
           addActionError("Server Url must be entered");
           isValid = false;
       }
       return isValid;
   }


   public String getInstallationName() {
       return installationName;
   }
 
   public void setInstallationName(String name) {
       this.installationName = name;
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


//
 
 
   public String getSelectedConfig() {
       return selectedConfig;
   }
 
   public void setSelectedConfig(String selectedConfig) {
       this.selectedConfig = selectedConfig;
   }
}
