/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.contentprovidersample;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.contentprovidersample.data.StarLordDatabase;
import com.example.android.contentprovidersample.data.Volant;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;


@RunWith(AndroidJUnit4.class)
@SmallTest
public class VolantTest {

    private StarLordDatabase mDatabase;

    @Before
    public void createDatabase() {
        mDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getTargetContext(),
                StarLordDatabase.class).build();
    }

    @After
    public void closeDatabase() throws IOException {
        mDatabase.close();
    }

    @Test
    public void insertAndCount() {
        assertThat(mDatabase.volant().count(), is(0));
        Volant volant = new Volant();
        volant.name = "abc";
        mDatabase.volant().insert(volant);
        assertThat(mDatabase.volant().count(), is(1));
    }

}