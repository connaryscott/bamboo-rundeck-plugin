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
        INLINE("INLINE"), FILE("FILE");

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
                        .getText("bamboo_rundeck_plugin.jobArgs.location.inline")),
                new PairType(RundeckAPIBaseTaskConfigurator.JobArgsLocation.FILE.toString(), this.getI18nBean()
                        .getText("bamboo_rundeck_plugin.jobArgs.location.file"))
        );
    }


    public Map<String, String> generateTaskConfigMap(@NotNull final ActionParametersMap params, @Nullable final TaskDefinition previousTaskDefinition)
    {
        final Map<String, String> config = super.generateTaskConfigMap(params, previousTaskDefinition);
     
        config.put("jobId", params.getString("jobId"));
        taskConfiguratorHelper.populateTaskConfigMapWithActionParameters(config, params, Arrays.asList("jobArgsLocationTypes"));
        config.put("jobArgs", params.getString("jobArgs"));

        config.put("jobArgsLocation", params.getString("jobArgsLocation"));
        config.put("jobArgsFile", params.getString("jobArgsFile"));
        config.put("jobArgsInline", params.getString("jobArgsInline"));
     
        return config;
    }


    public void validate(@NotNull final ActionParametersMap params, @NotNull final ErrorCollection errorCollection)
    {
        super.validate(params, errorCollection);
     
        final String jobId = params.getString("jobId");
        if (StringUtils.isEmpty(jobId))
        {
            errorCollection.addError("jobId", textProvider.getText("bamboo_rundeck_plugin.jobId.error"));
        }
    }


    @Override
    public void populateContextForCreate(@NotNull final Map<String, Object> context)
    {

        super.populateContextForCreate(context);
     
        context.put("jobId", "");
        context.put("jobArgsLocationTypes", getJobArgsLocationTypes());
        context.put("jobArgsLocation", RundeckAPIBaseTaskConfigurator.JobArgsLocation.INLINE);
        context.put("jobArgs", "");

        context.put("jobArgsFile", "");
        context.put("jobArgsInline", "");
    }


    @Override
    public void populateContextForEdit(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition) 
    {
        super.populateContextForEdit(context, taskDefinition);
     
        context.put("jobId", taskDefinition.getConfiguration().get("jobId"));

        context.put("jobArgsLocationTypes", getJobArgsLocationTypes());


        context.put("jobArgsLocation", taskDefinition.getConfiguration().get("jobArgsLocation"));
        if (taskDefinition.getConfiguration().get("jobArgsLocation").equals("INLINE")) {
           context.put("jobArgsInline", taskDefinition.getConfiguration().get("jobArgsInline"));
        }
        if (taskDefinition.getConfiguration().get("jobArgsLocation").equals("FILE")) {
           context.put("jobArgsFile", taskDefinition.getConfiguration().get("jobArgsFile"));
        }

        context.put("jobArgs", taskDefinition.getConfiguration().get("jobArgs"));
    }

     
    @Override
    public void populateContextForView(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition)
    {
        super.populateContextForView(context, taskDefinition);
        context.put("jobId", taskDefinition.getConfiguration().get("jobId"));
        context.put("jobArgsLocation", taskDefinition.getConfiguration().get("jobArgsLocation"));

        context.put("jobArgsLocation", taskDefinition.getConfiguration().get("jobArgsLocation"));
        if (taskDefinition.getConfiguration().get("jobArgsLocation").equals("INLINE")) {
           context.put("jobArgsInline", taskDefinition.getConfiguration().get("jobArgsInline"));
        }
        if (taskDefinition.getConfiguration().get("jobArgsLocation").equals("FILE")) {
           context.put("jobArgsFile", taskDefinition.getConfiguration().get("jobArgsFile"));
        }

        context.put("jobArgs", taskDefinition.getConfiguration().get("jobArgs"));
    }

    public void setTextProvider(final TextProvider textProvider)
    {
        this.textProvider = textProvider;
    }
}
