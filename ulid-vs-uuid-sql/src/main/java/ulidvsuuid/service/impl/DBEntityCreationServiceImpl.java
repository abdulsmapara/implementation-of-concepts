package ulidvsuuid.service.impl;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ulidvsuuid.enums.IDStrategy;
import ulidvsuuid.model.Entity;
import ulidvsuuid.repository.EntityRepository;
import ulidvsuuid.service.DBEntityCreationService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class DBEntityCreationServiceImpl implements DBEntityCreationService {

    private EntityRepository entityRepository;

    @Override
    public void createObjects(IDStrategy idStrategy, int numberOfObjects) {
        List<Entity> objects = new ArrayList<>();
        for (int i = 0; i < numberOfObjects; i++) {
            Entity entity = new Entity(
                    idStrategy.equals(IDStrategy.UUID)
                            ? UUID.randomUUID().toString()
                            : UuidCreator.getTimeOrderedEpoch().toString()
            );
            objects.add(entity);
        }
        entityRepository.saveAll(objects);
    }

    private void createAndMeasureTimeToCreateObjects(IDStrategy idStrategy) {
        int numObjects = 1000*1000;
        long startTimestamp = System.currentTimeMillis();
        this.createObjects(idStrategy, numObjects);
        long endTimestamp = System.currentTimeMillis();
        log.info("Created {} entities using {} in {} ms", numObjects, idStrategy.toString(), endTimestamp - startTimestamp);
    }

    @PostConstruct
    public void createObjects() {
        log.info("Creating objects with UUID and ULID strategies");
        createAndMeasureTimeToCreateObjects(IDStrategy.UUID);
        createAndMeasureTimeToCreateObjects(IDStrategy.ULID);
    }


}
