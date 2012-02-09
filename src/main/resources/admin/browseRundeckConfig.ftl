[#if rundeckConfigs.isEmpty()]
 <p>[@ww.text name='rundeck.browseRundeckConfig.noConfigs' /]</p>
[#else]
<p> [@ww.text name='rundeck.browseRundeckConfig.list' /]
</p>
<table id="existingRundeck" class="grid maxWidth">
   <thead>
   <tr>
       <th>[@ww.text name='rundeck.browseRundeckConfig.name' /]</th>
       <th>[@ww.text name='rundeck.browseRundeckConfig.rundeckUrl' /]</th>
       <th>[@ww.text name='rundeck.browseRundeckConfig.operations' /]</th>
   </tr>
   </thead>
   [#foreach config in rundeckConfigs ]
   <tr>
       <td>
           ${config.name}
       </td>
       <td>
           ${config.rundeckUrl?if_exists}
       </td>
       <td>
           ${config.rundeckUrl?if_exists}
       </td>
       <td class="operations">
           <a id="editConfig-${config.name}" href="[@ww.url action='editRundeckConfig' selectedConfig=config.name /]">[@ww.text name='global.buttons.edit' /]</a>
               |
               <a id="deleteConfig-${config.name}" href="[@ww.url action='deleteRundeckConfig' selectedConfig=config.name /]">[@ww.text name='global.buttons.delete' /]</a>
       </td>
   </tr>
   [/#foreach]
</table>
[/#if]
