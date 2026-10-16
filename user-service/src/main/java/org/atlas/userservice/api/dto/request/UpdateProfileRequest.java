package org.atlas.userservice.api.dto.request;

import jakarta.validation.constraints.Size;

import java.util.EnumSet;
import java.util.Set;

public class UpdateProfileRequest {

    public enum Field { FULL_NAME, DISPLAY_NAME, USERNAME, ABOUT, POSITION }

    @Size(max = 255)  private String fullName;
    @Size(max = 100)  private String displayName;
    @Size(max = 50)   private String username;
    @Size(max = 1000) private String about;
    @Size(max = 100)  private String position;

    private final Set<Field> provided = EnumSet.noneOf(Field.class);

    public boolean has(Field field) {
        return provided.contains(field);
    }

    public String getFullName() { return fullName; }

    public String getDisplayName() { return displayName; }

    public String getUsername() { return username; }

    public String getAbout() { return about; }

    public String getPosition() { return position; }
}
