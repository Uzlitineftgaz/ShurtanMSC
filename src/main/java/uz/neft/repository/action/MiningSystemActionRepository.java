package uz.neft.repository.action;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.neft.entity.MiningSystem;
import uz.neft.entity.action.MiningSystemAction;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface MiningSystemActionRepository extends JpaRepository<MiningSystemAction,Long> {

    Optional<MiningSystemAction> findFirstByMiningSystem(MiningSystem miningSystem);
    Optional<MiningSystemAction> findFirstByMiningSystemOrderByCreatedAtDesc(MiningSystem miningSystem);
    List<MiningSystemAction> findAllByMiningSystem(MiningSystem miningSystem);
    List<MiningSystemAction> findAllByMiningSystemOrderByCreatedAtDesc(MiningSystem miningSystem);
    Page<MiningSystemAction> findAllByMiningSystemOrderByCreatedAtDesc(MiningSystem miningSystem, Pageable pageable);
    List<MiningSystemAction> findAllByMiningSystemOrderByCreatedAtAsc(MiningSystem miningSystem);


    List<MiningSystemAction> findAllByCreatedAtBetween(Timestamp from, Timestamp until);

    List<MiningSystemAction> findAllByMiningSystemAndCreatedAtBetweenOrderByCreatedAtAsc(MiningSystem miningSystem, Timestamp from, Timestamp until);
    List<MiningSystemAction> findAllByMiningSystemAndCreatedAtBetweenOrderByCreatedAtDesc(MiningSystem miningSystem, Timestamp from, Timestamp until);


}
