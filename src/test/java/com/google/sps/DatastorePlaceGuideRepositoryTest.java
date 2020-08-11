package com.google.sps;

import com.google.gson.Gson;
import com.google.appengine.api.datastore.*;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.data.RepositoryType;
import com.google.sps.placeGuide.PlaceGuide;
import com.google.sps.placeGuide.repository.PlaceGuideRepository;
import com.google.sps.placeGuide.repository.PlaceGuideRepositoryFactory;
import com.google.sps.placeGuide.repository.impl.DatastorePlaceGuideRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public final class DatastorePlaceGuideRepositoryTest{
  
  private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
                                                            new LocalDatastoreServiceTestConfig());

  private static final long A_PUBLIC_ID = 12345;
  private static final long B_PUBLIC_ID = 23456;
  private static final long A_PRIVATE_ID = 34567;
  private static final long B_PRIVATE_ID = 45678;
  private static final String NAME = "name";
  private static final String AUDIO_KEY = "audioKey";
  private static final String CREATOR_A_ID = "creatorA_Id";
  private static final String CREATOR_B_ID = "creatorB_Id";
  private static final String OTHER_USER_ID = "otherUserId";
  private static final String PLACE_ID = "placeId";
  private static final GeoPt COORD = new GeoPt((float) 3.14, (float) 2.56);
  private static final boolean IS_PUBLIC = true;
  private static final long LENGTH = new Long(60);
  private static final String DESCRIPTION = "description";
  private static final String IMG_KEY = "imgKey";

  private final PlaceGuide testPublicPlaceGuideA = new PlaceGuide
                                            .Builder(NAME, AUDIO_KEY, CREATOR_A_ID, COORD)
                                            .setPlaceId(PLACE_ID)
                                            .setId(A_PUBLIC_ID)
                                            .setPlaceGuideStatus(IS_PUBLIC)
                                            .setLength(LENGTH)
                                            .setDescription(DESCRIPTION)
                                            .setImageKey(IMG_KEY)
                                            .build();

  private final PlaceGuide testPrivatePlaceGuideA = new PlaceGuide
                                            .Builder(NAME, AUDIO_KEY, CREATOR_A_ID, COORD)
                                            .setPlaceId(PLACE_ID)
                                            .setId(A_PRIVATE_ID)
                                            .setLength(LENGTH)
                                            .setDescription(DESCRIPTION)
                                            .setImageKey(IMG_KEY)
                                            .build();

  private final PlaceGuide testPublicPlaceGuideB = new PlaceGuide
                                            .Builder(NAME, AUDIO_KEY, CREATOR_B_ID, COORD)
                                            .setPlaceId(PLACE_ID)
                                            .setId(B_PUBLIC_ID)
                                            .setPlaceGuideStatus(IS_PUBLIC)
                                            .setLength(LENGTH)
                                            .setDescription(DESCRIPTION)
                                            .setImageKey(IMG_KEY)
                                            .build();

  private final PlaceGuide testPrivatePlaceGuideB = new PlaceGuide
                                            .Builder(NAME, AUDIO_KEY, CREATOR_B_ID, COORD)
                                            .setPlaceId(PLACE_ID)
                                            .setId(B_PRIVATE_ID)
                                            .setLength(LENGTH)
                                            .setDescription(DESCRIPTION)
                                            .setImageKey(IMG_KEY)
                                            .build();

  private final List<PlaceGuide> testPlaceGuidesList = Arrays.asList(testPublicPlaceGuideA,
                                                                     testPrivatePlaceGuideA,
                                                                     testPublicPlaceGuideB,
                                                                     testPrivatePlaceGuideB);
  
  private Entity getEntityFromPlaceGuide(PlaceGuide placeGuide) {
    Entity placeGuideEntity = new Entity(DatastorePlaceGuideRepository.ENTITY_KIND, 
                                                          placeGuide.getId());
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.NAME_PROPERTY, NAME);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.AUDIO_KEY_PROPERTY, AUDIO_KEY);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.CREATOR_ID_PROPERTY, 
                                                          placeGuide.getCreatorId());
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.IS_PUBLIC_PROPERTY, 
                                                          placeGuide.isPublic());
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.PLACE_ID_PROPERTY, PLACE_ID);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.COORD_PROPERTY, COORD);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.DESCRIPTION_PROPERTY, 
                                                          DESCRIPTION);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.LENGTH_PROPERTY, LENGTH);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.IMG_KEY_PROPERTY, IMG_KEY);
    return placeGuideEntity;
  }

  private void saveTestPlaceGuidesEntities(List<PlaceGuide> placeGuides) {
    for (PlaceGuide placeGuide : placeGuides) {
      datastore.put(getEntityFromPlaceGuide(placeGuide));
    }
  }

  private boolean placeGuidesListsAreEquals(List<PlaceGuide> aList, List<PlaceGuide> bList) {
    if (aList.size() != bList.size()) {
      return false;
    }
    for (PlaceGuide a : aList) {
      boolean hasEqual = false;
      int index_bList = 0;
      while (index_bList < bList.size()) {
        if (a.equals(bList.get(index_bList))) {
          hasEqual = true;
          bList.remove(index_bList);
          break;
        }
        index_bList++;
      }
      if (!hasEqual) {
          return false;
      }
    }
    return true;
  }

  private DatastoreService datastore;
  private PlaceGuideRepository placeGuideRepository;

  @Before
  public void setUp() {
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();
    placeGuideRepository = PlaceGuideRepositoryFactory.getPlaceGuideRepository(
                                                                        RepositoryType.DATASTORE);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void savePlaceGuideTest() {
    placeGuideRepository.savePlaceGuide(testPublicPlaceGuideA);
    datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query(DatastorePlaceGuideRepository.ENTITY_KIND);
    PreparedQuery preparedResult = datastore.prepare(query);
    Entity result = preparedResult.asSingleEntity();
    Entity expected = getEntityFromPlaceGuide(testPublicPlaceGuideA);
    assertTrue(result.equals(expected));
  }

  @Test
  public void getAllPlaceGuides_placeGuideExists_returnPlaceGuide() {
    saveTestPlaceGuidesEntities(testPlaceGuidesList);
    List<PlaceGuide> result = placeGuideRepository.getAllPlaceGuides();
    assertTrue(placeGuidesListsAreEquals(testPlaceGuidesList, result));
  }

  @Test
  public void getAllPlaceGuides_placeGuideDoesntExists_resultIsEmpty() {
    List<PlaceGuide> result = placeGuideRepository.getCreatedPlaceGuides(CREATOR_A_ID);
    assertTrue(result.isEmpty());
  }

  @Test
  public void getCreatedPlaceGuides_placeGuidesExistButNotOwnedByUser_resultIsEmpty() {
    saveTestPlaceGuidesEntities(testPlaceGuidesList);
    List<PlaceGuide> result = placeGuideRepository.getCreatedPlaceGuides(OTHER_USER_ID);
    assertTrue(result.isEmpty());
  }

  @Test
  public void getCreatedPlaceGuides_placeGuideExistsAndOwnedByUser_resultContainsPlaceGuide() {
    saveTestPlaceGuidesEntities(testPlaceGuidesList);
    List<PlaceGuide> result = placeGuideRepository.getCreatedPlaceGuides(CREATOR_A_ID);
    List<PlaceGuide> expected = Arrays.asList(testPublicPlaceGuideA, testPrivatePlaceGuideA);
    assertTrue(placeGuidesListsAreEquals(expected, result));
  }

  @Test
  public void getCreatedPlaceGuides_placeGuideDoesntExist_placeGuideListIsEmpty() {
    List<PlaceGuide> result = placeGuideRepository.getCreatedPlaceGuides(CREATOR_A_ID);
    assertTrue(result.isEmpty());
  }

  @Test
  public void getCreatedPublicPlaceGuides_placeGuideDoesntExist_placeGuideListIsEmpty() {
    List<PlaceGuide> result = placeGuideRepository.getCreatedPublicPlaceGuides(CREATOR_A_ID);
    assertTrue(result.isEmpty());
  }

//   @Test
//   public void getCreatedPublicPlaceGuides_placeGuideExistsAndPublicButNotOwnedByUser_placeGuideListIsEmpty() {
//     saveTestPlaceGuideEntity(testPlaceGuidesList);
//     List<PlaceGuide> results = placeGuideRepository.getCreatedPublicPlaceGuides(OTHER_USER_ID);
//     assertTrue(results.isEmpty());
//   }

//   @Test
//   public void getCreatedPublicPlaceGuides_placeGuideExistsAndPrivateAndNotOwnedByUser_placeGuideListIsEmpty() {
//     saveTestPlaceGuideEntity(testPlaceGuidesList);
//     List<PlaceGuide> results = placeGuideRepository.getCreatedPublicPlaceGuides(OTHER_USER_ID);
//     assertTrue(results.isEmpty());
//   }

//   @Test
//   public void getCreatedPublicPlaceGuides_placeGuideExistsAndPrivateAndOwnedByUser_placeGuideListIsEmpty() {
//     saveTestPlaceGuideEntity(testPlaceGuidesList);
//     List<PlaceGuide> results = placeGuideRepository.getCreatedPublicPlaceGuides(CREATOR_ID);
//     assertTrue(results.isEmpty());
//   }
}