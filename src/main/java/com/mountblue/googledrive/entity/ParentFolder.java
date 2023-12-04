package com.mountblue.googledrive.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class ParentFolder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "parentId")
    private List<File> files;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "parentId")
    private List<Folder> folders;

    public ParentFolder() {
    }

    public ParentFolder(Long id, List<File> files, List<Folder> folders) {
        this.id = id;
        this.files = files;
        this.folders = folders;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public List<Folder> getFolders() {
        return folders;
    }

    public void setFolders(List<Folder> folders) {
        this.folders = folders;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}