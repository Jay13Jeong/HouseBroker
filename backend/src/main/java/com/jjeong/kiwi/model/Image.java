package com.jjeong.kiwi.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "image", indexes = {
    @Index(columnList = "etagHash"),
    @Index(columnList = "lastModified"),
    @Index(columnList = "pendingDeletion")})
@RequiredArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Getter
    @Setter
    @Column(nullable = false)
    private String fileName;

    @Getter
    @Setter
    @Column(nullable = false)
    private String etagHash; //murmur

    @Getter
    @Setter
    @Column(nullable = false)
    private String extraHash; //sha256

    @Getter
    @Setter
    @Column(nullable = false)
    private boolean pendingDeletion = false;

    @Getter
    @Setter
    @Column(nullable = false)
    private LocalDateTime lastModified = LocalDateTime.now();

    @Getter
    @Setter()
    @ManyToMany(mappedBy = "images")
    private Set<RealEstate> realEstates = new HashSet<>();

}
