package com.google.sps.user;

import org.jetbrains.annotations.Nullable;

/** Stores the data related to one user */
public class User {
    private final String id, email;
    private final boolean publicPortfolio;
    @Nullable 
    private final String name, selfIntroduction, imgUrl;

    public static class Builder{
        //Required
        private final String id, email;
        //Optional
        private String name, selfIntroduction, imgUrl;
        private boolean publicPortfolio = false;

        public Builder(String id, String email){
            this.id = id;
            this.email = email;
        }

        public Builder setName(String name){
            this.name = name;
            return this;
        }

        public Builder addImgUrl(String imgUrl){
            this.imgUrl = imgUrl;
            return this;
        }

        public Builder addSelfIntroduction(String selfIntroduction){
            this.selfIntroduction = selfIntroduction;
            return this;
        }

        public Builder setPublicPortfolio(){
            this.publicPortfolio = true;
            return this;
        }

        public User build(){
            return new User(this);
        }
    }

    private User(Builder builder){
        this.id = builder.id;
        this.email = builder.email;
        this.name = builder.name;
        this.selfIntroduction = builder.selfIntroduction;
        this.imgUrl = builder.imgUrl;
        this.publicPortfolio = builder.publicPortfolio;
    }

    public String getId(){
        return id;
    }

    public String getEmail(){
        return this.email;
    }

    public String getName(){
        return name;
    }

    public String getSelfIntroduction(){
        return selfIntroduction;
    }

    public String getImgUrl(){
        return imgUrl;
    }

    public boolean portfolioIsPublic(){
        return publicPortfolio;
    }
}