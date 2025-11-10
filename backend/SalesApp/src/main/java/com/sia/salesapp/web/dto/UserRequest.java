package com.sia.salesapp.web.dto;

public record UserRequest(String username,
                          String email,
                          String password) { }
