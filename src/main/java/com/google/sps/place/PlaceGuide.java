// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.place;

import org.jetbrains.annotations.Nullable;

/** Class containing place guide's information. */
public class PlaceGuide {

    private final String name, audioUrl, creatorId;
    private final PlaceCoordinate coord;

    // Specify how long user usually spends to follow this place guide in minutes.
    private final int length;

    @Nullable
    private final String desc, imgUrl;

    private PlaceGuide(String name, String audioUrl, String creatorId, 
                                    PlaceCoordinate coord, int length, 
                                    String desc, String imgUrl) {
        this.name = name;
        this.audioUrl = audioUrl;
        this.creatorId = creatorId;
        this.coord = coord;
        this.length = length;
        this.desc = desc;
        this.imgUrl = imgUrl;
    }

    public static class Builder {
        private final String name, audioUrl, creatorId;
        private final PlaceCoordinate coord;
        private final int length;
        private String desc, imgUrl;
        
        public Builder(String name, String audioUrl, String creatorId, 
                                    PlaceCoordinate coord, int length) {
            this.name = name;
            this.audioUrl = audioUrl;
            this.creatorId = creatorId;
            this.coord = coord;
            this.length = length;
        }
        public Builder setDescription(String desc) {
            this.desc = desc;
            return this;
        }
        public Builder setImageUrl(String imgUrl) {
            this.imgUrl = imgUrl;
            return this;
        }
        public UserAuthenticationStatus build() {
            return new PlaceGuide(name, audioUrl, creatorId, coord, length, desc, imgUrl);
        }
    }

    public String getName() {
        return name;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public PlaceCoordinate getCoordinate() {
        return coord;
    }

    @Nullable
    public String getLength() {
        return length;
    }

    @Nullable
    public String getDescription() {
        return desc;
    }

    @Nullable
    public String getImageUrl() {
        return imgUrl;
    }
}