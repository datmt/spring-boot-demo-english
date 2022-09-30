package com.xkcoding.oauth.repostiory;

import com.xkcoding.oauth.entity.SysClientDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

/**
 * Client information.
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2020-01-06 13:09
 */
public interface SysClientDetailsRepository extends JpaRepository<SysClientDetails, Long> {

    /**
     * Find client information via clientId.
     *
     * @param clientId clientId
     * @return Results
     */
    Optional<SysClientDetails> findFirstByClientId(String clientId);

    /**
     * Remove the client based on the client ID
     *
     * @param clientId client ID
     */
    @Modifying
    void deleteByClientId(String clientId);

}
