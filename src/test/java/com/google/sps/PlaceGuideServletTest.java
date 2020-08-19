// // Copyright 2020 Google LLC

// // Licensed under the Apache License, Version 2.0 (the "License");
// // you may not use this file except in compliance with the License.
// // You may obtain a copy of the License at

// //     https://www.apache.org/licenses/LICENSE-2.0

// // Unless required by applicable law or agreed to in writing, software
// // distributed under the License is distributed on an "AS IS" BASIS,
// // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// // See the License for the specific language governing permissions and
// // limitations under the License.

// package com.google.sps;

// import static org.junit.Assert.*;
// import static org.mockito.Mockito.mock;
// import static org.mockito.Mockito.when;
// import com.google.appengine.api.blobstore.BlobInfo;
// import com.google.appengine.api.blobstore.BlobInfoFactory;
// import com.google.appengine.api.blobstore.BlobKey;
// import com.google.appengine.api.blobstore.BlobstoreService;
// import com.google.appengine.api.datastore.*;
// import com.google.appengine.tools.development.testing.LocalBlobstoreServiceTestConfig;
// import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
// import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
// import com.google.gson.Gson;
// import com.google.sps.servlets.UserDataServlet;
// import com.google.sps.user.User;
// import com.google.sps.user.repository.impl.DatastoreUserRepository;
// import java.io.IOException;
// import java.io.PrintWriter;
// import java.io.StringWriter;
// import java.util.*;
// import javax.servlet.ServletException;
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;
// import org.junit.After;
// import org.junit.Before;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.junit.runners.JUnit4;
// import java.util.Set;
// import java.util.HashSet;
// import java.util.Collections;

// @RunWith(JUnit4.class)
// public final class PlaceGuideServletTest {
//   public static final long A_PUBLIC_ID = 12345;
//   public static final long B_PUBLIC_ID = 23456;
//   public static final long A_PRIVATE_ID = 34567;
//   public static final long B_PRIVATE_ID = 45678;
//   public static final String NAME = "name";
//   public static final String AUDIO_KEY = "audioKey";
//   public static final String CREATOR_A_ID = "creatorA_Id";
//   public static final String CREATOR_B_ID = "creatorB_Id";
//   public static final String OTHER_USER_ID = "otherUserId";
//   public static final String PLACE_ID = "placeId";
//   public static final GeoPt COORDINATE = new GeoPt((float) 3.14, (float) 2.56);
//   public static final boolean IS_PUBLIC = true;
//   public static final long LENGTH = new Long(60);
//   public static final String DESCRIPTION = "description";
//   public static final String PREVIOUS_DESCRIPTION = "previous description";
//   public static final String IMAGE_KEY = "imageKey";

//   public static final String OTHER_USER_EMAIL = "otherUser@gmail.com";
//   public static final Set<Long> OTHER_USER_BOOKMARKED_PLACE_GUIDES_IDS = new HashSet<>();
//   public static final String CREATOR_A_EMAIL = "creatorA@gmail.com";
//   public static final Set<Long> CREATOR_A_BOOKMARKED_PLACE_GUIDES_IDS = 
//       new HashSet<>(Arrays.asList(A_PUBLIC_ID, B_PUBLIC_ID));

//   private final User testUser = 
//       new User.Builder(OTHER_USER_ID, OTHER_USER_EMAIL, OTHER_USER_BOOKMARKED_PLACE_GUIDES_IDS)
//       .build();

//   private final User userA = 
//       new User.Builder(CREATOR_A_ID, CREATOR_A_EMAIL, CREATOR_A_BOOKMARKED_PLACE_GUIDES_IDS)
//       .build();

//   private final PlaceGuide testPublicPlaceGuideA = 
//       new PlaceGuide.Builder(A_PUBLIC_ID, NAME, AUDIO_KEY, CREATOR_A_ID, COORDINATE)
//       .setPlaceId(PLACE_ID)
//       .setPlaceGuideStatus(IS_PUBLIC)
//       .setLength(LENGTH)
//       .setDescription(DESCRIPTION)
//       .setImageKey(IMAGE_KEY)
//       .build();

//   private final PlaceGuide previousTestPublicPlaceGuideA = 
//       new PlaceGuide.Builder(A_PUBLIC_ID, NAME, AUDIO_KEY, CREATOR_A_ID, COORDINATE)
//       .setPlaceGuideStatus(IS_PUBLIC)
//       .build();

//   private final PlaceGuide testPrivatePlaceGuideA = 
//       new PlaceGuide.Builder(A_PRIVATE_ID, NAME, AUDIO_KEY, CREATOR_A_ID, COORDINATE)
//       .setPlaceId(PLACE_ID)
//       .setLength(LENGTH)
//       .setDescription(DESCRIPTION)
//       .setImageKey(IMAGE_KEY)
//       .build();

//   private final PlaceGuide testPublicPlaceGuideB = 
//       new PlaceGuide.Builder(B_PUBLIC_ID, NAME, AUDIO_KEY, CREATOR_B_ID, COORDINATE)
//       .setPlaceId(PLACE_ID)
//       .setPlaceGuideStatus(IS_PUBLIC)
//       .setLength(LENGTH)
//       .setDescription(DESCRIPTION)
//       .setImageKey(IMAGE_KEY)
//       .build();

//   private final PlaceGuide testPrivatePlaceGuideB = 
//       new PlaceGuide.Builder(B_PRIVATE_ID, NAME, AUDIO_KEY, CREATOR_B_ID, COORDINATE)
//       .setPlaceId(PLACE_ID)
//       .setLength(LENGTH)
//       .setDescription(DESCRIPTION)
//       .setImageKey(IMAGE_KEY)
//       .build();

//   private void saveTestPlaceGuidesEntities(List<PlaceGuide> placeGuides) {
//     for (PlaceGuide placeGuide : placeGuides) {
//       datastore.put(getEntityFromPlaceGuide(placeGuide));
//     }
//   }
  
//   private Entity getEntityFromPlaceGuide(PlaceGuide placeGuide) {
//     Entity placeGuideEntity = new Entity(DatastorePlaceGuideRepository.ENTITY_KIND, 
//                                                           placeGuide.getId());
//     placeGuideEntity.setProperty(DatastorePlaceGuideRepository.NAME_PROPERTY, placeGuide.getName());
//     placeGuideEntity.setProperty(DatastorePlaceGuideRepository.AUDIO_KEY_PROPERTY, placeGuide.getAudioKey());
//     placeGuideEntity.setProperty(DatastorePlaceGuideRepository.CREATOR_ID_PROPERTY, 
//                                                           placeGuide.getCreatorId());
//     placeGuideEntity.setProperty(DatastorePlaceGuideRepository.IS_PUBLIC_PROPERTY, 
//                                                           placeGuide.isPublic());
//     placeGuideEntity.setProperty(DatastorePlaceGuideRepository.PLACE_ID_PROPERTY, placeGuide.getPlaceId());
//     placeGuideEntity.setProperty(DatastorePlaceGuideRepository.COORDINATE_PROPERTY, placeGuide.getCoordinate());
//     placeGuideEntity.setProperty(DatastorePlaceGuideRepository.DESCRIPTION_PROPERTY, 
//                                                           placeGuide.getDescription());
//     placeGuideEntity.setProperty(DatastorePlaceGuideRepository.LENGTH_PROPERTY, placeGuide.getLength());
//     placeGuideEntity.setProperty(DatastorePlaceGuideRepository.IMAGE_KEY_PROPERTY, placeGuide.getImageKey());
//     return placeGuideEntity;
//   }

//   // Find out if the 2 lists of placeguides are equal.
//   private boolean compare(List<PlaceGuide> a, List<PlaceGuide> b) {
//     List<PlaceGuide> b_copy = new ArrayList<>(b);
//     if (a.size() != b_copy.size()) {
//       return false;
//     }
//     for (PlaceGuide a_pg : a) {
//       boolean hasEqual = false;
//       int index_b_copy = 0;
//       while (index_b_copy < b_copy.size()) {
//         if (a_pg.getId() == (b_copy.get(index_b_copy)).getId()) {
//           hasEqual = true;
//           b_copy.remove(index_b_copy);
//           break;
//         }
//         index_b_copy++;
//       }
//       if (!hasEqual) {
//         return false;
//       }
//     }
//     return true;
//   }

//   private DatastoreService datastore;
//   private Map<String, Object> attributeToValue = new HashMap<>();
//   private LocalServiceTestHelper helper;
//   private HttpServletRequest request;
//   private HttpServletResponse response;
//   private BlobstoreService blobstoreService;
//   private BlobInfoFactory blobInfoFactory;

//   @Before
//   public void setup() {
//     // Set the userdata that the Userservice will return.
//     attributeToValue.put("com.google.appengine.api.users.UserService.user_id_key", (Object) ID);
//     helper =
//         new LocalServiceTestHelper(
//                 new LocalDatastoreServiceTestConfig(), new LocalBlobstoreServiceTestConfig());
//     helper.setUp();

//     request = mock(HttpServletRequest.class);
//     response = mock(HttpServletResponse.class);
//     blobstoreService = mock(BlobstoreService.class);
//     blobInfoFactory = mock(BlobInfoFactory.class);
//   }

//   @Test
//   public void doGet_queryAllPublicPlaceGuides_returnCorrespondingPlaceGuides() {
    
//   }

//   @Test
//   public void doGet_queryAllCreatedPlaceGuides_returnCorrespondingPlaceGuides() {
    
//   }

//   @Test
//   public void doGet_queryCreatedPublicPlaceGuides_returnCorrespondingPlaceGuides() {
    
//   }

//   @Test
//   public void doGet_queryCreatedPrivatePlaceGuides_returnCorrespondingPlaceGuides() {
    
//   }

//   @Test
//   public void doGet_queryBookmarkedPlaceGuides_returnCorrespondingPlaceGuides() {
    
//   }

//   @After
//   public void tearDown() {
//     helper.tearDown();
//   }
// }