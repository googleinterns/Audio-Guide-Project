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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public final class DatastorePlaceGuideRepositoryTest{
  
  private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
                                                            new LocalDatastoreServiceTestConfig());

  private static final long ID = 12345;
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
                                            .setId(ID)
                                            .setPlaceGuideStatus(IS_PUBLIC)
                                            .setLength(LENGTH)
                                            .setDescription(DESCRIPTION)
                                            .setImageKey(IMG_KEY)
                                            .build();

  private final PlaceGuide testPrivatePlaceGuideA = new PlaceGuide
                                            .Builder(NAME, AUDIO_KEY, CREATOR_A_ID, COORD)
                                            .setPlaceId(PLACE_ID)
                                            .setId(ID)
                                            .setLength(LENGTH)
                                            .setDescription(DESCRIPTION)
                                            .setImageKey(IMG_KEY)
                                            .build();

  private final PlaceGuide testPublicPlaceGuideB = new PlaceGuide
                                            .Builder(NAME, AUDIO_KEY, CREATOR_B_ID, COORD)
                                            .setPlaceId(PLACE_ID)
                                            .setId(ID)
                                            .setPlaceGuideStatus(IS_PUBLIC)
                                            .setLength(LENGTH)
                                            .setDescription(DESCRIPTION)
                                            .setImageKey(IMG_KEY)
                                            .build();

  private final PlaceGuide testPrivatePlaceGuideB = new PlaceGuide
                                            .Builder(NAME, AUDIO_KEY, CREATOR_B_ID, COORD)
                                            .setPlaceId(PLACE_ID)
                                            .setId(ID)
                                            .setLength(LENGTH)
                                            .setDescription(DESCRIPTION)
                                            .setImageKey(IMG_KEY)
                                            .build();

  private void saveTestPlaceGuideEntity(boolean isPublic) {
    Entity placeGuideEntity = new Entity(DatastorePlaceGuideRepository.ENTITY_KIND, ID);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.NAME_PROPERTY, NAME);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.AUDIO_KEY_PROPERTY, AUDIO_KEY);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.CREATOR_ID_PROPERTY, CREATOR_ID);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.IS_PUBLIC_PROPERTY, IS_PUBLIC);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.PLACE_ID_PROPERTY, PLACE_ID);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.COORD_PROPERTY, COORD);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.DESCRIPTION_PROPERTY, DESCRIPTION);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.LENGTH_PROPERTY, LENGTH);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.IMG_KEY_PROPERTY, IMG_KEY);
    datastore.put(placeGuideEntity);
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
    placeGuideRepository.savePlaceGuide(testPlaceGuide);
    datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query(DatastorePlaceGuideRepository.ENTITY_KIND);
    PreparedQuery result = datastore.prepare(query);
    Entity placeGuide = result.asSingleEntity();
    assertEquals(NAME, placeGuide.getProperty(DatastorePlaceGuideRepository.NAME_PROPERTY));
    assertEquals(AUDIO_KEY, placeGuide.getProperty(
                                            DatastorePlaceGuideRepository.AUDIO_KEY_PROPERTY));
    assertEquals(CREATOR_ID, placeGuide.getProperty(
                                            DatastorePlaceGuideRepository.CREATOR_ID_PROPERTY));
    assertEquals(PLACE_ID, placeGuide.getProperty(
                                            DatastorePlaceGuideRepository.PLACE_ID_PROPERTY));
    assertEquals(COORD, placeGuide.getProperty(DatastorePlaceGuideRepository.COORD_PROPERTY));
    assertEquals(IS_PUBLIC, placeGuide.getProperty(
                                            DatastorePlaceGuideRepository.IS_PUBLIC_PROPERTY));
    assertEquals(LENGTH, placeGuide.getProperty(DatastorePlaceGuideRepository.LENGTH_PROPERTY));
    assertEquals(DESCRIPTION, placeGuide.getProperty(
                                            DatastorePlaceGuideRepository.DESCRIPTION_PROPERTY));
    assertEquals(IMG_KEY, placeGuide.getProperty(DatastorePlaceGuideRepository.IMG_KEY_PROPERTY));
  }

  @Test
  public void getAllPlaceGuides_placeGuideExists_returnPlaceGuide() {
    Entity placeGuideEntity = new Entity(DatastorePlaceGuideRepository.ENTITY_KIND, ID);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.NAME_PROPERTY, NAME);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.AUDIO_KEY_PROPERTY, AUDIO_KEY);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.CREATOR_ID_PROPERTY, CREATOR_ID);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.PLACE_ID_PROPERTY, PLACE_ID);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.IS_PUBLIC_PROPERTY, IS_PUBLIC);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.COORD_PROPERTY, COORD);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.DESCRIPTION_PROPERTY, DESCRIPTION);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.LENGTH_PROPERTY, LENGTH);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.IMG_KEY_PROPERTY, IMG_KEY);
    datastore.put(placeGuideEntity);
    List<PlaceGuide> results = placeGuideRepository.getAllPlaceGuides();
    assertEquals(1, results.size());
    PlaceGuide result = results.get(0);
    assertTrue(testPlaceGuide.equals(result));
  }

  @Test
  public void getAllPlaceGuides_placeGuideDoesntExists_placeGuideListIsEmpty() {
    List<PlaceGuide> results = placeGuideRepository.getCreatedPlaceGuides(CREATOR_ID);
    assertTrue(results.isEmpty());
  }

  @Test
  public void getCreatedPlaceGuides_placeGuideExistsButNotOwnedByUser_placeGuideListIsEmpty() {
    Entity placeGuideEntity = new Entity(DatastorePlaceGuideRepository.ENTITY_KIND, ID);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.NAME_PROPERTY, NAME);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.AUDIO_KEY_PROPERTY, AUDIO_KEY);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.CREATOR_ID_PROPERTY, CREATOR_ID);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.PLACE_ID_PROPERTY, PLACE_ID);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.IS_PUBLIC_PROPERTY, IS_PUBLIC);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.COORD_PROPERTY, COORD);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.DESCRIPTION_PROPERTY, DESCRIPTION);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.LENGTH_PROPERTY, LENGTH);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.IMG_KEY_PROPERTY, IMG_KEY);
    datastore.put(placeGuideEntity);
    List<PlaceGuide> results = placeGuideRepository.getCreatedPlaceGuides(OTHER_USER_ID);
    assertTrue(results.isEmpty());
  }

  @Test
  public void getCreatedPlaceGuides_placeGuideExistsAndOwnedByUser_returnPlaceGuideList() {
    Entity placeGuideEntity = new Entity(DatastorePlaceGuideRepository.ENTITY_KIND, ID);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.NAME_PROPERTY, NAME);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.AUDIO_KEY_PROPERTY, AUDIO_KEY);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.CREATOR_ID_PROPERTY, CREATOR_ID);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.PLACE_ID_PROPERTY, PLACE_ID);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.IS_PUBLIC_PROPERTY, IS_PUBLIC);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.COORD_PROPERTY, COORD);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.DESCRIPTION_PROPERTY, DESCRIPTION);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.LENGTH_PROPERTY, LENGTH);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.IMG_KEY_PROPERTY, IMG_KEY);
    datastore.put(placeGuideEntity);
    List<PlaceGuide> results = placeGuideRepository.getCreatedPlaceGuides(CREATOR_ID);
    assertEquals(1, results.size());
    PlaceGuide result = results.get(0);
    assertTrue(testPlaceGuide.equals(result));
  }

  @Test
  public void getCreatedPlaceGuides_placeGuideDoesntExist_placeGuideListIsEmpty() {
    List<PlaceGuide> results = placeGuideRepository.getCreatedPlaceGuides(CREATOR_ID);
    assertTrue(results.isEmpty());
  }

  @Test
  public void getCreatedPublicPlaceGuides_placeGuideDoesntExist_placeGuideListIsEmpty() {
    List<PlaceGuide> results = placeGuideRepository.getCreatedPublicPlaceGuides(CREATOR_ID);
    assertTrue(results.isEmpty());
  }

  @Test
  public void getCreatedPublicPlaceGuides_placeGuideExistsAndPublicButNotOwnedByUser_placeGuideListIsEmpty() {
    Entity placeGuideEntity = new Entity(DatastorePlaceGuideRepository.ENTITY_KIND, ID);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.NAME_PROPERTY, NAME);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.AUDIO_KEY_PROPERTY, AUDIO_KEY);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.CREATOR_ID_PROPERTY, CREATOR_ID);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.IS_PUBLIC_PROPERTY, IS_PUBLIC);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.PLACE_ID_PROPERTY, PLACE_ID);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.COORD_PROPERTY, COORD);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.DESCRIPTION_PROPERTY, DESCRIPTION);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.LENGTH_PROPERTY, LENGTH);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.IMG_KEY_PROPERTY, IMG_KEY);
    datastore.put(placeGuideEntity);
    List<PlaceGuide> results = placeGuideRepository.getCreatedPublicPlaceGuides(OTHER_USER_ID);
    assertTrue(results.isEmpty());
  }

  @Test
  public void getCreatedPublicPlaceGuides_placeGuideExistsAndPrivateAndNotOwnedByUser_placeGuideListIsEmpty() {
    Entity placeGuideEntity = new Entity(DatastorePlaceGuideRepository.ENTITY_KIND, ID);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.NAME_PROPERTY, NAME);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.AUDIO_KEY_PROPERTY, AUDIO_KEY);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.CREATOR_ID_PROPERTY, CREATOR_ID);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.PLACE_ID_PROPERTY, PLACE_ID);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.COORD_PROPERTY, COORD);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.DESCRIPTION_PROPERTY, DESCRIPTION);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.LENGTH_PROPERTY, LENGTH);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.IMG_KEY_PROPERTY, IMG_KEY);
    datastore.put(placeGuideEntity);
    List<PlaceGuide> results = placeGuideRepository.getCreatedPublicPlaceGuides(OTHER_USER_ID);
    assertTrue(results.isEmpty());
  }

  @Test
  public void getCreatedPublicPlaceGuides_placeGuideExistsAndPrivateAndOwnedByUser_placeGuideListIsEmpty() {
    Entity placeGuideEntity = new Entity(DatastorePlaceGuideRepository.ENTITY_KIND, ID);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.NAME_PROPERTY, NAME);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.AUDIO_KEY_PROPERTY, AUDIO_KEY);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.CREATOR_ID_PROPERTY, CREATOR_ID);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.PLACE_ID_PROPERTY, PLACE_ID);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.COORD_PROPERTY, COORD);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.DESCRIPTION_PROPERTY, DESCRIPTION);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.LENGTH_PROPERTY, LENGTH);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.IMG_KEY_PROPERTY, IMG_KEY);
    datastore.put(placeGuideEntity);
    List<PlaceGuide> results = placeGuideRepository.getCreatedPublicPlaceGuides(CREATOR_ID);
    assertTrue(results.isEmpty());
  }
}