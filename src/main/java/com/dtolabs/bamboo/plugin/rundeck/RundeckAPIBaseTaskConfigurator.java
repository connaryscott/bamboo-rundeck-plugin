/*
 * Copyright 2010 DTO Labs, Inc. (http://dtolabs.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.dtolabs.bamboo.plugin.rundeck;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.core.util.PairType;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


import com.opensymphony.xwork.TextProvider;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class RundeckAPIBaseTaskConfigurator extends AbstractTaskConfigurator 
{
    protected static final Logger log = Logger.getLogger(RundeckAPIBaseTaskConfigurator.class);

    private TextProvider textProvider;

    static public enum JobArgsLocation {
        INLINE(Constants.INLINE), FILE(Constants.FILE);

        private final String name;

        JobArgsLocation(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public List<PairType> getJobArgsLocationTypes() {
        // https://answers.atlassian.com/questions/20566/textprovider-in-sdk-bamboo-helloworld-task-example-does-not-work
        return Lists.newArrayList(
                new PairType(RundeckAPIBaseTaskConfigurator.JobArgsLocation.INLINE.toString(), this.getI18nBean()
                        .getText(Constants.I18N_JOBARGS_LOCATION_INLINE)),
                new PairType(RundeckAPIBaseTaskConfigurator.JobArgsLocation.FILE.toString(), this.getI18nBean()
                        .getText(Constants.I18N_JOBARGS_LOCATION_FILE))
        );
    }


    public Map<String, String> generateTaskConfigMap(@NotNull final ActionParametersMap params, @Nullable final TaskDefinition previousTaskDefinition)
    {
        final Map<String, String> config = super.generateTaskConfigMap(params, previousTaskDefinition);
     
        config.put(Constants.PARAM_JOBID, params.getString(Constants.PARAM_JOBID));
        taskConfiguratorHelper.populateTaskConfigMapWithActionParameters(config, params, Arrays.asList(Constants.PARAM_JOBARGSLOCATIONTYPES));

        config.put(Constants.PARAM_JOBARGSLOCATION, params.getString(Constants.PARAM_JOBARGSLOCATION));
        config.put(Constants.PARAM_JOBARGSFILE, params.getString(Constants.PARAM_JOBARGSFILE));
        config.put(Constants.PARAM_JOBARGSINLINE, params.getString(Constants.PARAM_JOBARGSINLINE));
     
        return config;
    }

/*
TODO:
example of a better example of a validate() routine:
 public void validate(@NotNull final ActionParametersMap params, @NotNull final ErrorCollection errorCollection)
    {
        super.validate(params, errorCollection);

        String host = params.getString("host");
        if (StringUtils.isEmpty(host))
        {
            errorCollection.addError("host", "You must specify a host to connect to");
        }

        String username = params.getString("username");
        if (StringUtils.isEmpty(username))
        {
            errorCollection.addError("username", "You must specify a username");
        }

        String password = params.getString("password");

        if (params.getBoolean("change_password"))
        {
            password = params.getString("new_password");
        }

        if (StringUtils.isEmpty(password))
        {
            errorCollection.addError("password", "You must specify a password");
        }

        String localPath = params.getString("localPath");
        if (StringUtils.isEmpty(localPath))
        {
            errorCollection.addError("localPath", "You must specify a path to a local file");
        }

        String remotePath = params.getString("remotePath");
        if (StringUtils.isEmpty(remotePath))
        {
            errorCollection.addError("remotePath", "You must specify the remote path on the server");
        }
    }

*/


    public void validate(@NotNull final ActionParametersMap params, @NotNull final ErrorCollection errorCollection)
    {
        super.validate(params, errorCollection);
     
        final String jobId = params.getString(Constants.PARAM_JOBID);
        if (StringUtils.isEmpty(jobId))
        {
            errorCollection.addError(Constants.PARAM_JOBID, textProvider.getText(Constants.I18N_JOBID_ERROR));
        }
    }


    @Override
    public void populateContextForCreate(@NotNull final Map<String, Object> context)
    {

        super.populateContextForCreate(context);
     
        context.put(Constants.PARAM_JOBID, "");
        context.put(Constants.PARAM_JOBARGSLOCATIONTYPES, getJobArgsLocationTypes());
        context.put(Constants.PARAM_JOBARGSLOCATION, RundeckAPIBaseTaskConfigurator.JobArgsLocation.INLINE);

        context.put(Constants.PARAM_JOBARGSFILE, "");
        context.put(Constants.PARAM_JOBARGSINLINE, "");
    }


    @Override
    public void populateContextForEdit(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition) 
    {
        super.populateContextForEdit(context, taskDefinition);
     
        context.put(Constants.PARAM_JOBID, taskDefinition.getConfiguration().get(Constants.PARAM_JOBID));

        context.put(Constants.PARAM_JOBARGSLOCATIONTYPES, getJobArgsLocationTypes());


        context.put(Constants.PARAM_JOBARGSLOCATION, taskDefinition.getConfiguration().get(Constants.PARAM_JOBARGSLOCATION));
        if (taskDefinition.getConfiguration().get(Constants.PARAM_JOBARGSLOCATION).equals(Constants.INLINE)) {
           context.put(Constants.PARAM_JOBARGSINLINE, taskDefinition.getConfiguration().get(Constants.PARAM_JOBARGSINLINE));
        }
        if (taskDefinition.getConfiguration().get(Constants.PARAM_JOBARGSLOCATION).equals(Constants.FILE)) {
           context.put(Constants.PARAM_JOBARGSFILE, taskDefinition.getConfiguration().get(Constants.PARAM_JOBARGSFILE));
        }

    }

     
    @Override
    public void populateContextForView(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition)
    {
        super.populateContextForView(context, taskDefinition);
        context.put(Constants.PARAM_JOBID, taskDefinition.getConfiguration().get(Constants.PARAM_JOBID));

        context.put(Constants.PARAM_JOBARGSLOCATION, taskDefinition.getConfiguration().get(Constants.PARAM_JOBARGSLOCATION));
        if (taskDefinition.getConfiguration().get(Constants.PARAM_JOBARGSLOCATION).equals(Constants.INLINE)) {
           context.put(Constants.PARAM_JOBARGSINLINE, taskDefinition.getConfiguration().get(Constants.PARAM_JOBARGSINLINE));
        }
        if (taskDefinition.getConfiguration().get(Constants.PARAM_JOBARGSLOCATION).equals(Constants.FILE)) {
           context.put(Constants.PARAM_JOBARGSFILE, taskDefinition.getConfiguration().get(Constants.PARAM_JOBARGSFILE));
        }

    }

    public void setTextProvider(final TextProvider textProvider)
    {
        this.textProvider = textProvider;
    }
}
