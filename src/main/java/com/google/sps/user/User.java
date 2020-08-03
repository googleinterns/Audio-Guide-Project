package com.google.sps.user;

import org.jetbrains.annotations.Nullable;

/** Stores the data related to one user */
public class User {
    private final String id;
    @Nullable 
    private final String selfIntroduction, imgUrl;

    public static class Builder{
        //Required
        private final String id;
        //Optional
        private String selfIntroduction, imgUrl;

        public Builder(String id){
            this.id = id;
        }

        public Builder addImgUrl(String imgUrl){
            this.imgUrl = imgUrl;
        }

        public Builder addSelfIntroduction(String selfIntroduction){
            this.selfIntroduction = selfIntroduction;
        }

        public User build(){
            return new User(this);
        }
    }

    private User(Builder builder){
        this.id = builder.id;
        this.selfIntroduction = builder.selfIntroduction;
        this.imgUrl = builder.imgUrl;
    }

    public String getId(){
        return id;
    }

    public String getSelfIntroduction(){
        return selfIntroduction;
    }

    public String getImgUrl(){
        return imgUrl;
    }
}