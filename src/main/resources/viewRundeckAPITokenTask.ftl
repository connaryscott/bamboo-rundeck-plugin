[@ww.label labelKey="bamboo_rundeck_plugin.jobId" name="jobId"/]

[@ui.bambooSection dependsOn='jobArgsLocation' showOn='FILE']
    [@ww.label labelKey='bamboo_rundeck_plugin.jobArgs.location.file' name='jobArgsFile' hideOnNull='true' /]
[/@ui.bambooSection]

[@ui.bambooSection dependsOn='jobArgsLocation' showOn='INLINE']
    [@ww.label labelKey='bamboo_rundeck_plugin.jobArgs.location.inline' name='jobArgsInline' hideOnNull='true' /]
[/@ui.bambooSection]
