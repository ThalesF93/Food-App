package com.thales.authorization.dto;

import java.util.List;

public record RegisterDTO(String username, String password, List<String> roles) {
}
