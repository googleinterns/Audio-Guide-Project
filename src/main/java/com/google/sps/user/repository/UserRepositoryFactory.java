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

package com.google.sps.user.repository;

import com.google.sps.user.User;
import com.google.sps.data.RepositoryType;
import com.google.sps.user.repository.impl.DatastoreUserRepository;
import org.jetbrains.annotations.Nullable;

/** Creates a UserRepository according to the requested type. */
public class UserRepositoryFactory {
    
    public static UserRepository getUserRepository(RepositoryType repositoryType) {
        if (repositoryType == null) {
            throw new IllegalArgumentException("repositoryType can't be null!");
        }
        switch(repositoryType) {
            case DATASTORE:
                return new DatastoreUserRepository();
            default:
                throw new IllegalArgumentException("not an existing respository type");
        }
    }
}