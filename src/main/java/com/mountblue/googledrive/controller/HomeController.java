package com.mountblue.googledrive.controller;

import com.mountblue.googledrive.entity.File;
import com.mountblue.googledrive.entity.Folder;
import com.mountblue.googledrive.entity.ParentFolder;
import com.mountblue.googledrive.entity.Users;
import com.mountblue.googledrive.service.FileService;
import com.mountblue.googledrive.service.FolderService;
import com.mountblue.googledrive.service.ParentFolderService;
import com.mountblue.googledrive.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.util.Map;
import java.util.stream.Collectors;

import java.util.Set;

@Controller
public class HomeController {

    private FileService fileService;
    private FolderService folderService;
    private ParentFolderService parentFolderService;

    private UserService userService;

    @Autowired
    public HomeController(FileService fileService, FolderService folderService,
                          ParentFolderService parentFolderService, UserService userService) {
        this.fileService = fileService;
        this.folderService = folderService;
        this.parentFolderService = parentFolderService;
        this.userService = userService;
    }

    @GetMapping("/start")
    public String getStarted() {
        return "start";
    }

    @GetMapping("/logout")
    public String logout() {
        return "start";
    }

//    @GetMapping({"/", "/My Drive"})
//    public String home(Model model){
//
//        List<ParentFolder> parentFolders = parentFolderService.getAllParentFolders();
//        ParentFolder parentFolder = parentFolderService.getParentFolderByName("My Drive");
//        ParentFolder starredFolder = parentFolderService.getParentFolderByName("Starred");
//        List<Folder> folders = parentFolder.getFolders();
//        List<File> files= parentFolder.getFiles();
//
//        folders.addAll(starredFolder.getFolders());
//        files.addAll(starredFolder.getFiles());
//
//        Iterator<File> iterator = files.iterator();
//        while (iterator.hasNext()) {
//            File file = iterator.next();
//            if (file.getFolder()!=null) {
//                iterator.remove();  // Safe removal using Iterator
//            }
//        }
//        model.addAttribute("parentFolderName","My Drive");
//        model.addAttribute("parentFolders",parentFolders);
//        model.addAttribute("folders",folders);
//        model.addAttribute("files",files);
//        return "home";
//    }

    @GetMapping({"/", "/My Drive"})
    public String home(Model model, Principal principal) {
        if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) principal;

            // Retrieve user attributes from the OAuth2 token
            Map<String, Object> userAttributes = oauthToken.getPrincipal().getAttributes();

            String userEmail = (String) userAttributes.get("email");
            String userName = (String) userAttributes.get("name");
            String userPicture = (String) userAttributes.get("picture");

            Users user = userService.getUserByEmail(userEmail);

            if (user == null) {
                user = new Users();
                user.setEmail(userEmail);
                userService.saveUser(user);
            }
//
//            List<ParentFolder> parentFolders = parentFolderService.getParentFoldersByUserEmail(userEmail);
//
//            List<Folder> folders = new ArrayList<>();
//            List<File> files = new ArrayList<>();
//
//            for (ParentFolder parentFolder : parentFolders) {
//                folders.addAll(parentFolder.getFolders());
//                files.addAll(parentFolder.getFiles());
//            }
            List<ParentFolder> parentFolders = parentFolderService.getAllParentFolders();
            ParentFolder parentFolder = parentFolderService.getParentFolderByName("My Drive");
            ParentFolder starredFolder = parentFolderService.getParentFolderByName("Starred");
            List<Folder> folders = parentFolder.getFolders();
            List<File> files= parentFolder.getFiles();

            folders.addAll(starredFolder.getFolders());
            files.addAll(starredFolder.getFiles());

            Iterator<File> iterator = files.iterator();
            while (iterator.hasNext()) {
                File file = iterator.next();
                if (file.getFolder() != null) {
                    iterator.remove();  // Safe removal using Iterator
                }
            }

            model.addAttribute("parentFolderName", "My Drive");
            model.addAttribute("parentFolders", parentFolders);
            model.addAttribute("folders", folders);
            model.addAttribute("files", files);
            return "home";
        }
        return "error";
    }

}