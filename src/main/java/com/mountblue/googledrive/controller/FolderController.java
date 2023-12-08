package com.mountblue.googledrive.controller;

import com.mountblue.googledrive.entity.File;
import com.mountblue.googledrive.entity.Folder;
import com.mountblue.googledrive.entity.ParentFolder;
import com.mountblue.googledrive.entity.Users;
import com.mountblue.googledrive.service.FolderService;
import com.mountblue.googledrive.service.ParentFolderService;
import com.mountblue.googledrive.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
public class FolderController {

    private FolderService folderService;
    @Autowired
    private ParentFolderService parentFolderService;
    @Autowired
    private UserService userService;
    @Autowired
    public FolderController(FolderService folderService){
        this.folderService=folderService;
    }

    @PostMapping("/uploadFolder")
    public String uploadFolder(@RequestParam("files") List<MultipartFile> files,
                               @RequestParam("parentFolder") String parentFolderName,
                               Principal principal) {
        try {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) principal;
            // Retrieve user attributes from the OAuth2 token
            Map<String, Object> userAttributes = oauthToken.getPrincipal().getAttributes();

            String userEmail = (String) userAttributes.get("email");
            Users user = userService.getUserByEmail(userEmail);

            String folderName = folderService.getFolderNameFromFilename(files.get(0).getOriginalFilename());
            System.out.println(folderName);
            Folder folder = folderService.createFolder(folderName, files,user);

            folder.setUser(user);
            ParentFolder parentFolder = parentFolderService.getParentFolderByName(parentFolderName,user);
            parentFolder.getFolders().add(folder);
            parentFolderService.save(parentFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/"+parentFolderName;
    }

    @GetMapping("/openFolder")
    public String showFilesInFolder(@RequestParam Long folderId, Model model) {
        Folder folder = folderService.getFolderById(folderId);
        List<File> files = folder.getFiles();
        model.addAttribute("files", files);
        return "files";
    }

    @PostMapping("/deleteFolder")
    public String deleteFolder(@RequestParam Long folderId,
                               @RequestParam("parentFolder") String parentFolderName,
                               Principal principal,
                               Model model) {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) principal;
        Map<String, Object> userAttributes = oauthToken.getPrincipal().getAttributes();
        String userEmail = (String) userAttributes.get("email");
        Users user = userService.getUserByEmail(userEmail);

        Folder folder = folderService.getFolderById(folderId);
        ParentFolder parentFolder = parentFolderService.getParentFolderByName(parentFolderName,user);
        parentFolder.getFolders().remove(folder);
        folderService.deleteFolderById(folderId);
        parentFolderService.save(parentFolder);

        return "redirect:/"+parentFolderName;
    }
    @PostMapping("/starFolder")
    public String starFolder(@RequestParam Long folderId,
                               @RequestParam("parentFolder") String parentFolderName,
                               Principal principal,
                               Model model) {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) principal;
        Map<String, Object> userAttributes = oauthToken.getPrincipal().getAttributes();
        String userEmail = (String) userAttributes.get("email");
        Users user = userService.getUserByEmail(userEmail);

        Folder folder = folderService.getFolderById(folderId);
        ParentFolder parentFolder = parentFolderService.getParentFolderByName("Starred",user);
        parentFolder.getFolders().add(folder);
        if(!folder.isStarred()){
            folder.setStarred(true);
        }
        else{
            folder.setStarred(false);
        }

        folderService.save(folder);

        return "redirect:/"+parentFolderName;
    }
}
