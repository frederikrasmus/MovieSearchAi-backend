package com.example.moviesearch.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO som definerer hvad en genre er.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreDto {
    private Integer id;
    private String name;
}