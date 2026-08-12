package com.social.media.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class SocialUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private SocialProfile socialProfile;

    @OneToMany(mappedBy = "socialUser")
    private List<Post> post = new ArrayList<>();

    @ManyToMany
    private Set<SocialGroups> grrous = new HashSet<>();

}
