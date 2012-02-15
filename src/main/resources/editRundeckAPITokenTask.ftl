[#--
  Copyright 2010 DTO Labs, Inc. (http://dtolabs.com) 
 
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
 
       http://www.apache.org/licenses/LICENSE-2.0
 
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
--]
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
