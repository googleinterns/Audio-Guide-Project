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

  @Rule
  private final ExpectedException thrown = ExpectedException.none();
  
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

  // Find out if the 2 lists of placeguides are equal.
  private boolean compare(List<PlaceGuide> a, List<PlaceGuide> b) {
    List<PlaceGuide> b_copy = new ArrayList<>(b);
    if (a.size() != b_copy.size()) {
      return false;
    }
    for (PlaceGuide a_pg : a) {
      boolean hasEqual = false;
      int index_b_copy = 0;
      while (index_b_copy < b_copy.size()) {
        if (a_pg.equals(b_copy.get(index_b_copy))) {
          hasEqual = true;
          b_copy.remove(index_b_copy);
          break;
        }
        index_b_copy++;
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
    List<PlaceGuide> testPlaceGuidesList = Arrays.asList(testPublicPlaceGuideA,
                                                         testPublicPlaceGuideB,
                                                         testPrivatePlaceGuideB,
                                                         testPrivatePlaceGuideA);
    saveTestPlaceGuidesEntities(testPlaceGuidesList);
    List<PlaceGuide> result = placeGuideRepository.getAllPlaceGuides();
    List<PlaceGuide> expected = Arrays.asList(testPublicPlaceGuideA, testPublicPlaceGuideB);
    assertTrue(compare(expected, result));
  }

  @Test
  public void getAllPlaceGuides_placeGuideDoesntExists_resultIsEmpty() {
    List<PlaceGuide> result = placeGuideRepository.getCreatedPlaceGuides(CREATOR_A_ID);
    assertTrue(result.isEmpty());
  }

  @Test
  public void getCreatedPlaceGuides_UserDoesntOwnAnyPlaceGuides_resultIsEmpty() {
    List<PlaceGuide> testPlaceGuidesList = Arrays.asList(testPublicPlaceGuideA,
                                                         testPublicPlaceGuideB,
                                                         testPrivatePlaceGuideB,
                                                         testPrivatePlaceGuideA);
    saveTestPlaceGuidesEntities(testPlaceGuidesList);
    List<PlaceGuide> result = placeGuideRepository.getCreatedPlaceGuides(OTHER_USER_ID);
    assertTrue(result.isEmpty());
  }

  @Test
  public void getCreatedPlaceGuides_UserOwnsPublicAndPrivate_resultContainsPlaceGuide() {
    List<PlaceGuide> testPlaceGuidesList = Arrays.asList(testPublicPlaceGuideA,
                                                         testPublicPlaceGuideB,
                                                         testPrivatePlaceGuideB,
                                                         testPrivatePlaceGuideA);
    saveTestPlaceGuidesEntities(testPlaceGuidesList);
    List<PlaceGuide> result = placeGuideRepository.getCreatedPlaceGuides(CREATOR_A_ID);
    List<PlaceGuide> expected = Arrays.asList(testPublicPlaceGuideA, testPrivatePlaceGuideA);
    assertTrue(compare(expected, result));
  }

  @Test
  public void getCreatedPlaceGuides_placeGuideDoesntExist_resultIsEmpty() {
    List<PlaceGuide> result = placeGuideRepository.getCreatedPlaceGuides(CREATOR_A_ID);
    assertTrue(result.isEmpty());
  }

  @Test
  public void getCreatedPublicPlaceGuides_placeGuideDoesntExist_resultIsEmpty() {
    List<PlaceGuide> result = placeGuideRepository.getCreatedPublicPlaceGuides(CREATOR_A_ID);
    assertTrue(result.isEmpty());
  }

  @Test
  public void getCreatedPublicPlaceGuides_userDoesntOwnAnyPublicPlaceGuides_resultIsEmpty() {
    List<PlaceGuide> testPlaceGuidesList = Arrays.asList(testPublicPlaceGuideB,
                                                         testPrivatePlaceGuideB,
                                                         testPrivatePlaceGuideA);
    saveTestPlaceGuidesEntities(testPlaceGuidesList);
    List<PlaceGuide> result = placeGuideRepository.getCreatedPublicPlaceGuides(CREATOR_A_ID);
    assertTrue(result.isEmpty());
  }

  @Test
  public void getCreatedPublicPlaceGuides_UserHasPublicPlaceGuide_resultHasPlaceGuide() {
    List<PlaceGuide> testPlaceGuidesList = Arrays.asList(testPublicPlaceGuideA,
                                                         testPublicPlaceGuideB,
                                                         testPrivatePlaceGuideB,
                                                         testPrivatePlaceGuideA);
    saveTestPlaceGuidesEntities(testPlaceGuidesList);
    List<PlaceGuide> result = placeGuideRepository.getCreatedPublicPlaceGuides(CREATOR_A_ID);
    List<PlaceGuide> expected = Arrays.asList(testPublicPlaceGuideA);
    assertTrue(compare(expected, result));
  }

  @Test
  public void getCreatedPrivatePlaceGuides_placeGuideDoesntExist_resultIsEmpty() {
    List<PlaceGuide> result = placeGuideRepository.getCreatedPrivatePlaceGuides(CREATOR_A_ID);
    assertTrue(result.isEmpty());
  }

  @Test
  public void getCreatedPrivatePlaceGuides_userDoesntOwnAnyPrivatePlaceGuides_resultIsEmpty() {
    List<PlaceGuide> testPlaceGuidesList = Arrays.asList(testPublicPlaceGuideB,
                                                         testPrivatePlaceGuideB,
                                                         testPublicPlaceGuideA);
    saveTestPlaceGuidesEntities(testPlaceGuidesList);
    List<PlaceGuide> result = placeGuideRepository.getCreatedPrivatePlaceGuides(CREATOR_A_ID);
    assertTrue(result.isEmpty());
  }

  @Test
  public void getCreatedPrivatePlaceGuides_UserHasPrivatePlaceGuide_resultHasPlaceGuide() {
    List<PlaceGuide> testPlaceGuidesList = Arrays.asList(testPublicPlaceGuideA,
                                                         testPublicPlaceGuideB,
                                                         testPrivatePlaceGuideB,
                                                         testPrivatePlaceGuideA);
    saveTestPlaceGuidesEntities(testPlaceGuidesList);
    List<PlaceGuide> result = placeGuideRepository.getCreatedPrivatePlaceGuides(CREATOR_A_ID);
    List<PlaceGuide> expected = Arrays.asList(testPrivatePlaceGuideA);
    assertTrue(compare(expected, result));
  }

  @Test(expected = EntityNotFoundException.class)
  public void deletePlaceGuideTest() throws Exception{
    List<PlaceGuide> testPlaceGuidesList = Arrays.asList(testPublicPlaceGuideA,
                                                         testPublicPlaceGuideB,
                                                         testPrivatePlaceGuideB,
                                                         testPrivatePlaceGuideA);
    saveTestPlaceGuidesEntities(testPlaceGuidesList);
    placeGuideRepository.deletePlaceGuide(A_PUBLIC_ID);
    Key deletedEntityKey = KeyFactory.createKey(DatastorePlaceGuideRepository.ENTITY_KIND, 
                                                                             A_PUBLIC_ID);
    Entity deletedEntity = datastore.get(deletedEntityKey);
  }
}