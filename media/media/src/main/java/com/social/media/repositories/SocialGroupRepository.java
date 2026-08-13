package com.social.media.repositories;

import com.social.media.models.SocialGroups;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialGroupRepository extends JpaRepository<SocialGroups, Long> {
}
