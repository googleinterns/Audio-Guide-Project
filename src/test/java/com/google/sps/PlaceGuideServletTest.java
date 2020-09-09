// Copyright 2020 Google LLC

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at

//     https://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import static org.junit.Assert.*;

import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.datastore.*;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class PlaceGuideServletTest {
  private HttpServletRequest request;
  private HttpServletResponse response;
  private BlobstoreService blobstoreService;
  private BlobInfoFactory blobInfoFactory;

  @Before
  public void setup() {
    // Set the userdata that the Userservice will return.
    attributeToValue.put("com.google.appengine.api.users.UserService.user_id_key", (Object) ID);
    helper =
        new LocalServiceTestHelper(
                new LocalDatastoreServiceTestConfig(), new LocalBlobstoreServiceTestConfig())
            .setEnvIsLoggedIn(true)
            .setEnvAuthDomain("localhost")
            .setEnvEmail(EMAIL)
            .setEnvAttributes(attributeToValue);
    helper.setUp();

    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    blobstoreService = mock(BlobstoreService.class);
    blobInfoFactory = mock(BlobInfoFactory.class);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }
}
