package com.spdpboss.repository; 

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; // IMPORTANT: Add this import
import com.spdpboss.model.FreeAnk; // Make sure this points to your FreeAnk class

public interface FreeAnkRepository extends JpaRepository<FreeAnk, Long> {

    // Add this line to define the method:
    Optional<FreeAnk> findByName(String name);

}