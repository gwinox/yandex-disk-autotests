package enums;

import lombok.Getter;

public enum QueryParam {
    PATH("path"),
    OVERWRITE("overwrite"),
    FROM("from"),
    LIMIT("limit"),
    SORT("sort");

    @Getter
    private final String value;

    QueryParam(String value) {
        this.value = value;
    }
}