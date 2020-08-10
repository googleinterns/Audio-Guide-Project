package com.google.sps;

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

@RunWith(JUnit4.class)
public final class DatastorePlaceGuideRepositoryTest {

  private PlaceGuideRepository placeGuideRepository;
  private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
                                                            new LocalDatastoreServiceTestConfig());

  private static final long ID = 12345;
  private static final String NAME = "name";
  private static final String AUDIO_KEY = "audioKey";
  private static final String CREATOR_ID = "creatorId";
  private static final String PLACE_ID = "placeId";
  private static final GeoPt COORD = new GeoPt(3.14, 2.56);
  private static final boolean IS_PUBLIC = true;
  private static final int LENGTH = 60;
  private static final String DESC = "desc";
  private static final String IMG_KEY = "imgKey";
  private final PlaceGuide placeGuideToSave = new PlaceGuide
                                            .Builder(NAME, AUDIO_KEY, CREATOR_ID, PLACE_ID, COORD)
                                            .setPlaceGuideToPublic(IS_PUBLIC);
                                            .setLength(LENGTH)
                                            .setDescription(DESC)
                                            .setImageKey(IMG_KEY)
                                            .build();
  private PlaceGuide placeGuideToGet; 

  @Before
  public void setUp() {
    helper.setUp();
    placeGuideRepository = PlaceGuideRepositoryFactory
                                    .getPlaceGuideRepository(RepositoryType.DATASTORE);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void savePlaceGuide() {
    placeGuideRepository.savePlaceGuide(CREATOR_ID, testPlaceGuide);
    DatastoreService datastore = new DatastoreServiceFactory.getDatastoreService();
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
    assertEquals(DESC, placeGuide.getProperty(DatastorePlaceGuideRepository.DESC_PROPERTY));
    assertEquals(IMG_KEY, placeGuide.getProperty(DatastorePlaceGuideRepository.IMG_KEY_PROPERTY));
  }

  @Test
  public getAllPlaceGuides() {
    Entity placeGuideEntity = new Entity(DatastorePlaceGuideRepository.ENTITY_KIND, ID);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.NAME_PROPERTY, NAME);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.AUDIO_KEY_PROPERTY, AUDIO_KEY);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.CREATOR_ID_PROPERTY, CREATOR_ID);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.PLACE_ID_PROPERTY, PLACE_ID);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.IS_PUBLIC_PROPERTY, IS_PUBLIC);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.COORD_PROPERTY, COORD);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.DESC_PROPERTY, DESC);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.LENGTH_PROPERTY, LENGTH);
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.IMG_KEY_PROPERTY, IMG_KEY);
    datastore.put(placeGuideEntity);
    List<PlaceGuide> result = placeGuideRepository.getAllPlaceGuides();
    List<PlaceGuide> expected = new ArrayList<>();
    expected.add(testPlaceGuide);
    assertEquals(expected, result);
  }
}