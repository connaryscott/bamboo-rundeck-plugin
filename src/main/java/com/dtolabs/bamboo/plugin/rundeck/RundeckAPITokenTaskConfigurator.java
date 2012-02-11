package com.dtolabs.bamboo.plugin.rundeck;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.opensymphony.xwork.TextProvider;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class RundeckAPITokenTaskConfigurator extends AbstractTaskConfigurator
{
    private TextProvider textProvider;



    public Map<String, String> generateTaskConfigMap(@NotNull final ActionParametersMap params, @Nullable final TaskDefinition previousTaskDefinition)
    {
        final Map<String, String> config = super.generateTaskConfigMap(params, previousTaskDefinition);
     
        config.put("jobId", params.getString("jobId"));
        config.put("jobArgs", params.getString("jobArgs"));
     
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
        context.put("jobArgs", "");
    }


    @Override
    public void populateContextForEdit(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition)
    {
        super.populateContextForEdit(context, taskDefinition);
     
        context.put("jobId", taskDefinition.getConfiguration().get("jobId"));
        context.put("jobArgs", taskDefinition.getConfiguration().get("jobArgs"));
    }
     
    @Override
    public void populateContextForView(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition)
    {
        super.populateContextForView(context, taskDefinition);
        context.put("jobId", taskDefinition.getConfiguration().get("jobId"));
        context.put("jobArgs", taskDefinition.getConfiguration().get("jobArgs"));
    }

    public void setTextProvider(final TextProvider textProvider)
    {
        this.textProvider = textProvider;
    }
}
