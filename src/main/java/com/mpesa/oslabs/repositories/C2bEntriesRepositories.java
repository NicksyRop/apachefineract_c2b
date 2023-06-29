package com.mpesa.oslabs.repositories;

import com.mpesa.oslabs.models.C2b_Entries;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface C2bEntriesRepositories extends JpaRepository<C2b_Entries, Long> {

    Optional <C2b_Entries> findByTransactionId(String transactionId);
}
