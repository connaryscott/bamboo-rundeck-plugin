[#assign cancelUri = '/admin/viewRundeckConfig.action' /]
[#assign showActionErrors = 'true' /]
 
[@ww.form action="updateRundeckConfig"
         submitLabelKey='global.buttons.update'
         titleKey='rundeck.addRundeckConfig.title'
         cancelUri=cancelUri
         showActionErrors=showActionErrors
 
]
[@ww.textfield name='installationName' labelKey='rundeck.installationName' descriptionKey='rundeck.installationName.description' /]
[@ww.textfield name='rundeckUrl' labelKey='rundeck.rundeckUrl' descriptionKey='rundeck.rundeckUrl.description' /]
[@ww.textfield name='rundeckUser' labelKey='rundeck.rundeckUser' descriptionKey='rundeck.rundeckUser.description' /]
[@ww.textfield name='rundeckPassword' labelKey='rundeck.rundeckPassword' descriptionKey='rundeck.rundeckPassword.description' /]
[@ww.textfield name='rundeckApiToken' labelKey='rundeck.rundeckApiToken' descriptionKey='rundeck.rundeckApiToken.description' /]
 
 
[/@ww.form]
