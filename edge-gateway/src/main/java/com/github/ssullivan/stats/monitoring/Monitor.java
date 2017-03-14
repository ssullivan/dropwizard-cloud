/*
 * Copyright 2013 Netflix, Inc.
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */
package com.github.ssullivan.stats.monitoring;

/**
 * Interface to register a counter to monitor
 * @author Mikey Cohen
 * Date: 3/18/13
 * Time: 4:33 PM
 */
public interface Monitor {
    /**
     * Implement this to add this Counter to a Registry
     * @param monitorObj
     */
    void register(NamedCount monitorObj);
}