package com.dtolabs.bamboo.plugin.rundeck.actions;


import com.atlassian.bamboo.ww2.BambooActionSupport;
import com.atlassian.bamboo.bandana.PlanAwareBandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.dtolabs.bamboo.plugin.rundeck.RundeckConfiguration;

import java.util.List;
import java.util.ArrayList;


public abstract class RundeckActionSupport extends BambooActionSupport {

   /**
     * Populated by dependency injection.
     */
   private BandanaManager bandanaManager;

  /**
     * List of stored configuration instances.
     */
   private List<RundeckConfiguration> rundeckConfigs;



   /**
     * Retrieves the list of configuration instances from the bandanaManager, which is stored in the rundeckConfigs
     * list.  If no items exist, then store an empty list.
     */
   protected void loadConfiguration() {
       rundeckConfigs = (List<RundeckConfiguration>) bandanaManager.getValue(PlanAwareBandanaContext.GLOBAL_CONTEXT, RundeckConfiguration.CONFIGURATION_KEY);
       if (rundeckConfigs == null) {
           rundeckConfigs = new ArrayList<RundeckConfiguration>();
       }
   }
 
   public BandanaManager getBandanaManager() {
       return bandanaManager;
   }
 
   public void setBandanaManager(BandanaManager bandanaManager) {
       this.bandanaManager = bandanaManager;
   }
 
   public List<RundeckConfiguration> getRundeckConfigs() {
       return rundeckConfigs;
   }
 
   public void setRundeckConfigs(List<RundeckConfiguration> rundeckConfigs) {
       this.rundeckConfigs = rundeckConfigs;
   }
 
   /**
     * Returns the stored configuration instance with a name equal to <code>selectedConfig</code>. If no
     * valid configs are found, null is returned.
     *
     * @param selectedConfig
     * @return
     */
   protected RundeckConfiguration loadConfiguration(String selectedConfig) {
       RundeckConfiguration foundConfig = null;
       loadConfiguration();
       for (RundeckConfiguration config : getRundeckConfigs()) {
           if (selectedConfig.equals(config.getName())) {
               foundConfig = config;
               break;
           }
       }
       return foundConfig;
   }
 
   /**
     * Stores the list of configuration objects within the bandana manager.
     */
   protected void storeConfiguration() {
       getBandanaManager().setValue(PlanAwareBandanaContext.GLOBAL_CONTEXT, RundeckConfiguration.CONFIGURATION_KEY, getRundeckConfigs());
   }
 
   /**
     * Handles the default action, which is just to load the configuration instances.
     * @return 'DEFAULT'
     * @throws Exception
     */
   @Override
   public String doDefault() throws Exception {
       loadConfiguration();
       return super.doDefault();
   }

}
