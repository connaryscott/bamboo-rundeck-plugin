[@ww.textfield labelKey="bamboo_rundeck_plugin.jobId" name="jobId" required='true'/]

[@ww.select
        labelKey='bamboo_rundeck_plugin.jobArgs.location'
        listKey='key'
        listValue='value'
        list=jobArgsLocationTypes
        name='jobArgsLocation'
        toggle=true /]

[@ui.bambooSection dependsOn='jobArgsLocation' showOn='FILE']
    [@ww.textfield labelKey='bamboo_rundeck_plugin.jobArgs.location.file' name='jobArgsFile' cssClass="long-field" /]
[/@ui.bambooSection]

[@ui.bambooSection dependsOn='jobArgsLocation' showOn='INLINE']
    [@ww.textarea rows=25 wrap="off" labelKey='bamboo_rundeck_plugin.jobArgs.location.inline' name='jobArgsInline' cssClass="long-field" /]
[/@ui.bambooSection]

[@ww.textfield labelKey="bamboo_rundeck_plugin.jobArgs" name="jobArgs" required='false'/]
