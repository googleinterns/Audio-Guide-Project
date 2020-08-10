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

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public final class DatastorePlaceGuideRepositoryTest {

  private final PlaceGuideRepository placeGuideRepository = PlaceGuideRepositoryFactory
                                                .getPlaceGuideRepository(RepositoryType.DATASTORE);

  private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
                                                            new LocalDatastoreServiceTestConfig());

  private static final String NAME = "name";
  private static final String AUDIO_KEY = "audioKey";
  private static final String CREATOR_ID = "creatorId";
  private static final String PLACE_ID = "placeId";
  private static final GeoPt COORD = new GeoPt(3.14, 2.56);
  private static final long ID = "id";
  private static final boolean IS_PUBLIC = true;
  private static final int LENGTH = 60;
  private static final String DESC = "desc";
  private static final String IMG_KEY = "imgKey";
  private final PlaceGuide testPlaceGuide = new PlaceGuide
                                            .Builder(NAME, AUDIO_KEY, CREATOR_ID, PLACE_ID, COORD)
                                            .setId(ID)
                                            .setPlaceGuideToPublic(IS_PUBLIC);
                                            .setLength(LENGTH)
                                            .setDescription(DESC)
                                            .setImageKey(IMG_KEY)
                                            .build();

  @Before
  public void setUp() {
    helper.setUp();
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
    assertEquals()
  }
}