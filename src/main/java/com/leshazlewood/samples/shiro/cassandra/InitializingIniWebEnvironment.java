/*
 * Copyright (C) 2013 Les Hazlewood
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.leshazlewood.samples.shiro.cassandra;

import org.apache.shiro.util.LifecycleUtils;
import org.apache.shiro.web.env.IniWebEnvironment;

/**
 * Fixes a bug in Shiro 1.2.2 where Initializable objects' init() method is not called correctly.
 *
 * @since 2013-06-10
 */
public class InitializingIniWebEnvironment extends IniWebEnvironment {

    @Override
    public void init() {
        super.init();
        LifecycleUtils.init(this.objects.values());
    }
}
