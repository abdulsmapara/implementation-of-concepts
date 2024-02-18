package ulidvsuuid.service;

import ulidvsuuid.enums.IDStrategy;

public interface DBEntityCreationService {

    void createObjects(IDStrategy idStrategy, int numberOfObjects);

}
