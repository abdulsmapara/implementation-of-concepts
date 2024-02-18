package ulidvsuuid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ulidvsuuid.model.Entity;

@Repository
public interface EntityRepository extends JpaRepository<Entity, String> {
}
