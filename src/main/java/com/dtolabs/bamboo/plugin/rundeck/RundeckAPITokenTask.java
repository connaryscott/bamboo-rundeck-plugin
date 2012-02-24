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

import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.task.TaskResult;
import org.jetbrains.annotations.NotNull;

/**
 * running a rundeck job via api token credentials
 */
public class RundeckAPITokenTask extends RundeckAPITaskBase
{

    // TODO, make this configurable in the plugin to identify what plan variables we use

    // before we figure out how to make a configurable interface for this plugin,
    //    we require configuration to be derived from plan properties.

    @NotNull
    @java.lang.Override
    public TaskResult execute(@NotNull final TaskContext taskContext) throws TaskException
    {
        return executeTokenStrategy(taskContext);
    }
}
