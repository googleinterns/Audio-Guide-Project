// Copyright 2020 Google LLC

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at

//     https://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.data;

/** Specifies the possible form types of queries for PlaceGuides. */
public enum PlaceGuideQueryType {
  ALL_PUBLIC(false),
  CREATED_ALL(false),
  CREATED_PUBLIC(false),
  CREATED_PRIVATE(false),
  BOOKMARKED(false),
  ALL_PUBLIC_IN_MAP_AREA(true),
  CREATED_ALL_IN_MAP_AREA(true),
  CREATED_PUBLIC_IN_MAP_AREA(true),
  CREATED_PRIVATE_IN_MAP_AREA(true);

  private final boolean requiresCoordinates;

  PlaceGuideQueryType(boolean requiresCoordinates) {
    this.requiresCoordinates = requiresCoordinates;
  }

  public boolean requiresCoordinates() {
    return this.requiresCoordinates;
  }
}
