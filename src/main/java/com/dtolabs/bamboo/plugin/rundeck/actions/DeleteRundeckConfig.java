package com.dtolabs.bamboo.plugin.rundeck.actions;
 
import org.apache.commons.lang.StringUtils;
import com.dtolabs.bamboo.plugin.rundeck.RundeckConfiguration; 
 
/**
 * WebWork Action that handles the 'delete' operations in the Rundeck admin interface.
 * 
 * @author Chuck Scott
 */
public class DeleteRundeckConfig extends RundeckActionSupport {
 
   private String selectedConfig;
 
   /**
     * Handles a click on a 'delete' link.  The configuration is loaded, then the selected
     * configuration is removed from the list of configurations, and the updated list is persisted.
     * @return 'input'
     * @throws Exception
     */
   @Override
   public String doExecute() throws Exception {
       loadConfiguration();
       if (!StringUtils.isEmpty(selectedConfig)) {
           RundeckConfiguration config = loadConfiguration(selectedConfig);
           if (config == null) {
               addActionError("Unable to find configuraiton for name: "+ selectedConfig);
           } else {
               getRundeckConfigs().remove(config);
               storeConfiguration();
           }
       }
       return INPUT;
   }
 
   public void setSelectedConfig(String selectedConfig) {
       this.selectedConfig = selectedConfig;
   }
 
}
