package com.google.sps.user;

import org.jetbrains.annotations.Nullable;
import java.util.Optional;

/** Stores the data related to one user. */
public class User {
  private final String id;
  private final String email;
  private final boolean publicPortfolio;
  private final Optional<String> name;
  private final Optional<String> selfIntroduction;
  private final Optional<String> imgUrl;

  public static class Builder {
    // Required.
    private final String id;
    private final String email;
    // Optional.
    private Optional<String> name = Optional.empty();
    private Optional<String> selfIntroduction = Optional.empty();
    private Optional<String> imgUrl = Optional.empty();
    private boolean publicPortfolio = false;

    public Builder(String id, String email) {
      this.id = id;
      this.email = email;
    }

    public Builder setName(String name) {
      this.name = Optional.of(name);
      return this;
    }

    public Builder addImgUrl(String imgUrl) {
      this.imgUrl = Optional.of(imgUrl);
      return this;
    }

    public Builder addSelfIntroduction(String selfIntroduction) {
      this.selfIntroduction = Optional.of(selfIntroduction);
      return this;
    }

    public Builder setPublicPortfolio() {
      this.publicPortfolio = true;
      return this;
    }

    public User build() {
      return new User(this);
    }
  }

  private User(Builder builder) {
    this.id = builder.id;
    this.email = builder.email;
    this.name = builder.name;
    this.selfIntroduction = builder.selfIntroduction;
    this.imgUrl = builder.imgUrl;
    this.publicPortfolio = builder.publicPortfolio;
  }

  public String getId() {
    return id;
  }

  public String getEmail() {
    return this.email;
  }

  public Optional<String> getName() {
    return name;
  }

  public Optional<String> getSelfIntroduction() {
    return selfIntroduction;
  }

  public Optional<String> getImgUrl() {
    return imgUrl;
  }

  public boolean portfolioIsPublic() {
    return publicPortfolio;
  }
}
