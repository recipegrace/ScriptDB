package com.recipegrace.hadooprunner.tree;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Created by fjacob on 5/12/15.
 */
public class NavigatorTreeContent {


    public NavigatorTreeContent(){

    }
    public enum TreeItemType {

         root,
        project,
        job,
    }


    private  final Node rootIcon =
            new ImageView(new Image(getClass().getResourceAsStream("/cb.png")));
    private   final Node projectIcon =
            new ImageView(new Image(getClass().getResourceAsStream("/scalding.png")));
    private final Node jobIcon =
            new ImageView(new Image(getClass().getResourceAsStream("/hadoop.png")));


    public  TreeItem<NavigatorTreeContent> newRootNode() {
        NavigatorTreeContent item= new NavigatorTreeContent("All projects", TreeItemType.root);
        return new TreeItem<NavigatorTreeContent>(item, rootIcon);
    }
    public TreeItem<NavigatorTreeContent> newProjectNode(String project) {
        NavigatorTreeContent item= new NavigatorTreeContent(project, TreeItemType.project);
        return new TreeItem<NavigatorTreeContent>(item, projectIcon);
    }
    public  TreeItem<NavigatorTreeContent> newJobNode(String job) {
        NavigatorTreeContent item= new NavigatorTreeContent(job, TreeItemType.job);
        return new TreeItem<NavigatorTreeContent>(item, jobIcon);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public TreeItemType getType() {
        return type;
    }

    public void setType(TreeItemType type) {
        this.type = type;
    }

    private String fullName;
    private TreeItemType type;

    public NavigatorTreeContent(String fullName, TreeItemType type) {
        this.type = type;
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        String[] parts = fullName.split("\\.") ;
       return parts[parts.length-1];
    }

    @Override
    public boolean equals(Object obj) {

        if(! (obj instanceof NavigatorTreeContent)) return false;
        NavigatorTreeContent content = (NavigatorTreeContent) obj;
        if(content.type== type && content.fullName.equals(fullName)) return true;
        return false;
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }
}
