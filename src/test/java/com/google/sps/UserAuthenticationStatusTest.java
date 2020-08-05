// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import com.google.gson.Gson;
import com.google.sps.authentication.UserAuthenticationStatus;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.lang.IllegalStateException;

@RunWith(JUnit4.class)
public class UserAuthenticationStatusTest extends UserAuthenticationStatus{

    @Before
    public void setUp() {
        
    }

    @Test
    public void doGet_userLoggedOut_returnsFalseAndLoginUrl() throws IOException {

    }

    @After
    public void tearDown() {
        
    }
}