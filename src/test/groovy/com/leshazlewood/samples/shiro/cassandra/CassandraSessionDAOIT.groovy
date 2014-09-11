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
package com.leshazlewood.samples.shiro.cassandra

import org.apache.shiro.session.mgt.SimpleSession
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import static org.testng.Assert.*

/**
 * @since 2013-06-10
 */
class CassandraSessionDAOIT {

    CassandraSessionDAO dao;

    @BeforeClass
    void setUp() {
        dao = new CassandraSessionDAO();
        dao.cluster = new ClusterFactory().getInstance();
        dao.keyspaceName = 'shirosessions'
        dao.tableName = 'sessions'

        dao.init()
    }

    @Test
    void dropTheHammer() {

        int count = 10000;

        List<Callable> tasks = new ArrayList<Callable>(count);

        for(int i = 0; i < count; i++) {

            Callable c = new Callable() {
                Object call() {
                    //Create
                    SimpleSession ss = new SimpleSession();
                    ss.setTimeout(5000); //5 second timeout
                    def id = dao.create(ss);
                    assertEquals(id, ss.getId())

                    //Read
                    def retrieved = dao.readSession(id);
                    assertNotNull(retrieved);

                    //Update
                    ss.stopTimestamp = new Date()
                    dao.update(ss);

                    //Delete
                    dao.delete(ss);

                    //Read to assert it's gone:
                    retrieved = dao.doReadSession(id);
                    assertNull(retrieved);

                    return null;
                }
            }

            tasks.add(c);
        }

        int processors = Runtime.getRuntime().availableProcessors();

        ExecutorService svc = Executors.newFixedThreadPool(processors);

        println "Executing ${tasks.size()} tasks with ${processors} threads..."
        long start = System.currentTimeMillis();
        svc.invokeAll(tasks);
        long stop = System.currentTimeMillis();

        println "Execution time: ${stop - start} millis"

        Thread.sleep(200)
    }
}
