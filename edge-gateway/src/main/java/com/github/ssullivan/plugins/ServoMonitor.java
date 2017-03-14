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
package com.github.ssullivan.plugins;

import com.github.ssullivan.stats.monitoring.Monitor;
import com.github.ssullivan.stats.monitoring.NamedCount;
import com.netflix.servo.monitor.Monitors;

/**
 * implementation to hook up the Servo Monitors to register Named counters
 * @author Mikey Cohen
 * Date: 4/16/13
 * Time: 4:40 PM
 */
public class ServoMonitor implements Monitor {
    @Override
    public void register(NamedCount monitorObj) {
        Monitors.registerObject(monitorObj);
    }
}
