package com.dtolabs.bamboo.plugin.rundeck.actions;
 
import com.dtolabs.bamboo.plugin.rundeck.RundeckConfiguration;
import org.apache.commons.lang.StringUtils;
 
/**
 * WebWork action that is invoked when an Ajax request to retrieve specific Rundeck configuration information is retrieved.
 *
 * @author Ross Rowe
 */
public class GetRundeckConfig extends RundeckActionSupport {
 
   private String selectedConfig;
 
   private RundeckConfiguration configuration;
 
   /**
     * Retrieves the configuration matching the selectedConfig instance variable, and stores the
     * configuration in an instance variable.
     *  
     * @return 'success'
     * @throws Exception
     */
   @Override
   public String doExecute() throws Exception {
       if (StringUtils.isEmpty(selectedConfig)) {
           addActionError("No selected configuration specified");
       }
       configuration = loadConfiguration(selectedConfig);
       if (configuration == null) {
           addActionError("Unable to find configuration for name: "+ selectedConfig);
       }
       return SUCCESS;
   }
 
   public void setSelectedConfig(String selectedConfig) {
       this.selectedConfig = selectedConfig;
   }
 
   public RundeckConfiguration getConfiguration() {
       return configuration;
   }
 
   public void setConfiguration(RundeckConfiguration configuration) {
       this.configuration = configuration;
   }
}
