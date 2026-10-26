package enums;

import lombok.Getter;

public enum HttpHeader {
    AUTHORIZATION("Authorization");

    @Getter
    private final String name;

    HttpHeader(String name) {
        this.name = name;
    }
}